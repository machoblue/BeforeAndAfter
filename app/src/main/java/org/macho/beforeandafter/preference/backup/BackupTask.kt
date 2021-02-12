
package org.macho.beforeandafter.preference.backup

import android.accounts.Account
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import org.macho.beforeandafter.shared.data.record.Record
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.lang.ref.WeakReference
import java.net.SocketTimeoutException
import java.util.*


class BackupTask(context: Context, val account: Account, listener: BackupTaskListener): AsyncTask<List<Record>, BackupTask.BackupStatus, Unit>() {
    companion object {
        const val TAG = "BackupTask"
        const val FILE_NAME = "backup.json"
    }

    private val contextRef = WeakReference(context)
    private val listenerRef = WeakReference(listener)

    private lateinit var driveService: Drive

    var recoverableAuthIOException: UserRecoverableAuthIOException? = null

    override fun doInBackground(vararg recordLists: List<Record>): Unit {
        try {
            val records = recordLists.firstOrNull() ?: let {
                cancel(true)
                publishProgress(BackupStatus(BackupStatus.BACKUP_STATUS_CODE_ERROR_NO_RECORDS))
                return
            }

            this.driveService = contextRef.get()?.let { context ->
                DriveUtil.buildDriveService(context, account)
            } ?: let {
                cancel(true)
                publishProgress(BackupStatus(BackupStatus.BACKUP_STATUS_CODE_ERROR_DRIVE_CONNECTION_FAILED))
                return
            }

            // save images
            val imageFileNames = extractImageFileNames(records)
            val size = imageFileNames.size
            val imageFileNameToDriveFileId: MutableMap<String, String> = hashMapOf()

            for ((index, fileName) in imageFileNames.withIndex()) {
                if (isCancelled) return
                publishProgress(BackupStatus(BackupStatus.BACKUP_STATUS_CODE_SAVING_IMAGES, index + 1, size))

                val imageFilePathStr = fileNameToFilePath(fileName) ?: return // contextが開放されていたら、何もせず終了
                val imageFile = java.io.File(imageFilePathStr)

                val existsImageFile = imageFile.exists() && imageFile.length() > 0
                if (!existsImageFile) {
                    Log.w(TAG, "Image isn't exists or is empty.: $imageFilePathStr")
                    continue
                }

                val fileId = saveImage(imageFile)
                if (fileId == null) {
                    FirebaseCrashlytics.getInstance().recordException(RuntimeException("Unexpectedly Drive API 3 Files: create return null."))
                    continue
                }

                imageFileNameToDriveFileId[fileName] = fileId
            }

            // save records
            if (isCancelled) return
            publishProgress(BackupStatus(BackupStatus.BACKUP_STATUS_CODE_SAVING_RECORDS))
            saveData(BackupData(records, imageFileNameToDriveFileId)) ?: let {
                cancel(true)
                publishProgress(BackupStatus(BackupStatus.BACKUP_STATUS_CODE_ERROR_FILES_CREATE_FAILED))
                return
            }

        } catch (e: UserRecoverableAuthIOException) {
            Log.w(TAG, "doInBackground.catch UserRecoverableException:${e::class.java}", e)
            publishProgress(BackupStatus(BackupStatus.BACKUP_STATUS_CODE_ERROR_RECOVERABLE))
            cancel(true)
            return


        } catch (e: SocketTimeoutException) {
            publishProgress(BackupStatus(BackupStatus.BACKUP_STATUS_CODE_ERROR_TIMEOUT))
            return

        } catch (e: GoogleJsonResponseException) {
            if (e.details?.errors?.any { errorInfo -> errorInfo.message.contains("The user's Drive storage quota has been exceeded.") } == true) {
                publishProgress(BackupStatus(BackupStatus.BACKUP_STATUS_CODE_ERROR_NO_ENOUGH_SPACE))
                FirebaseCrashlytics.getInstance().recordException(e)
                return

            } else {
                Log.e(TAG, "doInBackground.catch Exception:${e::class.java}", e)
                FirebaseCrashlytics.getInstance().recordException(e)
                throw e
            }

        } catch (e: Exception) {
            Log.e(TAG, "doInBackground.catch Exception:${e::class.java}", e)
            FirebaseCrashlytics.getInstance().recordException(e)
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
        return records
            .flatMap { mutableListOf(it.frontImagePath, it.sideImagePath, it.otherImagePath1, it.otherImagePath2, it.otherImagePath3) }
            .filter { !it.isNullOrEmpty() && java.io.File(fileNameToFilePath(it)).exists() }
            .filterNotNull()
            .toList()
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
            const val BACKUP_STATUS_CODE_ERROR_NO_RECORDS = 1001
            const val BACKUP_STATUS_CODE_ERROR_DRIVE_CONNECTION_FAILED = 1002
            const val BACKUP_STATUS_CODE_ERROR_FILES_CREATE_FAILED = 1003
            const val BACKUP_STATUS_CODE_ERROR_RECOVERABLE = 1004
            const val BACKUP_STATUS_CODE_ERROR_TIMEOUT = 1005
            const val BACKUP_STATUS_CODE_ERROR_NO_ENOUGH_SPACE = 1006
        }
    }

    interface BackupTaskListener {
        fun onProgress(status: BackupStatus)
    }

}