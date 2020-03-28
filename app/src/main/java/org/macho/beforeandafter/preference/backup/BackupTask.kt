package org.macho.beforeandafter.preference.backup

import android.accounts.Account
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.gson.Gson
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.data.Record
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.lang.ref.WeakReference
import java.util.*


class BackupTask(context: Context, val account: Account, listener: BackupTaskListener): AsyncTask<List<Record>, BackupTask.BackupStatus, Unit>() {
    companion object {
        const val TAG = "BackupTask"
        const val FILE_NAME = "backup.json"
    }

    private val contextRef = WeakReference(context)
    private val listenerRef = WeakReference(listener)

    private lateinit var driveService: Drive

    override fun doInBackground(vararg recordLists: List<Record>): Unit {
        try {
            val records = recordLists.firstOrNull() ?: let {
                cancel(true)
                listenerRef.get()?.onFail(R.string.backup_error_description_no_records)
                return
            }

            this.driveService = contextRef.get()?.let { context ->
                DriveUtil.buildDriveService(context, account)
            } ?: let {
                cancel(true)
                listenerRef.get()?.onFail(R.string.backup_error_drive_connection_error)
                return
            }

            // save images
            val imageFileNames = extractImageFileNames(records)
            val size = imageFileNames.size
            val imageFileNameToDriveFileId: MutableMap<String, String> = hashMapOf()
            imageFileNames.forEachIndexed { index, fileName ->
                if (isCancelled) return
                publishProgress(BackupStatus(BackupStatus.BACKUP_STATUS_CODE_SAVING_IMAGES, index + 1, size))

                val imageFilePathStr = fileNameToFilePath(fileName) ?: let { return } // contextが開放されていたら、何もせず終了
                val imageFile = java.io.File(imageFilePathStr)
                if (imageFile.exists() && imageFile.length() > 0) {
                    val fileId = saveImage(imageFile) ?: let { return }
                    imageFileNameToDriveFileId[fileName] = fileId

                } else {
                    Log.w(TAG, "Image isn't exists or is empty.: $imageFilePathStr")
                    // do nothing
                }
            }

            // save records
            if (isCancelled) return
            publishProgress(BackupStatus(BackupStatus.BACKUP_STATUS_CODE_SAVING_RECORDS))
            saveData(BackupData(records, imageFileNameToDriveFileId)) ?: let {
                cancel(true)
                listenerRef.get()?.onFail(R.string.backup_error_description)
                return
            }

        } catch (e: UserRecoverableAuthIOException) {
            Log.w(TAG, "doInBackground.catch UserRecoverableException:${e::class.java}", e)
            listenerRef.get()?.onRecoverableAuthErrorOccured(e)
            cancel(true)
            return

        } catch (e: IOException) {
            Log.e(TAG, "doInBackground.catch IOException:${e::class.java}", e)
            throw e

        } catch (e: Exception) {
            Log.e(TAG, "doInBackground.catch Exception:${e::class.java}", e)
            throw e
        }
    }

    override fun onProgressUpdate(vararg statusArray: BackupStatus?) {
        statusArray.firstOrNull()?.let { status ->
            Log.i(TAG, "*** In progress: (%d/%d) *** ".format(status.finishFilesCount, status.allFilesCount))
            listenerRef.get()?.onProgress(status)
        }
    }

    override fun onPostExecute(result: Unit?) {
        Log.i(TAG, "*** Finished! *** ")
        listenerRef.get()?.onProgress(BackupStatus(BackupStatus.BACKUP_STATUS_CODE_COMPLETE))
    }

    override fun onCancelled() {
        Log.w(TAG, "BackupTask has been cancelled.")
    }

    override fun onCancelled(result: Unit?) {
        Log.w(TAG, "BackupTask has been cancelled.: ${result}")
    }

    private fun saveData(data: BackupData): String? {
        return contextRef.get()?.let { context ->
            val recordsJson = Gson().toJson(data)
            Log.d(TAG, "recordsJson: ${recordsJson.toString()}")
            PrintWriter(OutputStreamWriter(context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE), "UTF-8")).use {
                it.append(recordsJson)
                it.close()
            }

            val fileMetadata = File().also {
                it.setName(FILE_NAME)
                it.setParents(Collections.singletonList(DriveUtil.DRIVE_SPACE_APPDATA))
            }
            val filePathStr = fileNameToFilePath(FILE_NAME) ?: let { return null }
            val mediaContent = FileContent("application/json", java.io.File(filePathStr))
            val file = driveService.files()?.create(fileMetadata, mediaContent)?.setFields("id")?.execute()
            Log.i(TAG, "File ID: " + file?.id)
            return file?.id
        }
    }

    private fun extractImageFileNames(records: List<Record>): List<String> {
        var imageFileNames = mutableListOf<String>()
        for (record in records) {
            val frontImageFileName = record.frontImagePath
            if (frontImageFileName != null && java.io.File(fileNameToFilePath(frontImageFileName)).exists()) {
                imageFileNames.add(frontImageFileName)
            }
            val sideImageFileName = record.sideImagePath
            if (sideImageFileName != null && java.io.File(fileNameToFilePath(sideImageFileName)).exists()) {
                imageFileNames.add(sideImageFileName)
            }
        }
        return imageFileNames
    }

    private fun saveImage(imageFile: java.io.File): String? {
        val fileMetadata = File().also {
            it.name = imageFile.name
            it.parents = Collections.singletonList(DriveUtil.DRIVE_SPACE_APPDATA)
        }

        val mediaContent = FileContent("image/jpg", imageFile)
        val file = driveService.files()?.create(fileMetadata, mediaContent)?.setFields("id")?.execute()
        return file?.id
    }

    private fun fileNameToFilePath(fileName: String): String? {
        return contextRef.get()?.let { context ->
            return "${context.filesDir}/${fileName}"
        }
    }

    // SAVING_IMAGESには、全何ファイル中何ファイルを処理中かを表す値をもたせたいので、enumではなくクラスで実装する。
    class BackupStatus(val statusCode: Int, val finishFilesCount: Int = 0, val allFilesCount: Int = 0) {
        companion object {
            const val BACKUP_STATUS_CODE_SAVING_RECORDS = 0
            const val BACKUP_STATUS_CODE_SAVING_IMAGES = 1
            const val BACKUP_STATUS_CODE_COMPLETE = 2
        }
    }

    interface BackupTaskListener {
        fun onProgress(status: BackupStatus)
        fun onFail(messageId: Int)
        fun onRecoverableAuthErrorOccured(e: UserRecoverableAuthIOException)
    }

}