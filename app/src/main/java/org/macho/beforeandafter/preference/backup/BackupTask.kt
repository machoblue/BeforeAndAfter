package org.macho.beforeandafter.preference.backup

import android.accounts.Account
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.gson.Gson
import org.macho.beforeandafter.R
import org.macho.beforeandafter.record.Record
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.lang.ref.WeakReference
import java.util.*


class BackupTask(context: Context, val account: Account, listener: BackupTaskListener): AsyncTask<List<Record>, BackupTask.BackupStatus, Unit>() {
    companion object {
        const val TAG = "BackupTask"
        const val DRIVE_SPACE = "appDataFolder"
        const val FILE_NAME = "backup.json"
    }

    private val contextRef: WeakReference<Context>
    private val listenerRef: WeakReference<BackupTaskListener>

    private var driveService: Drive? = null

    private lateinit var appFilesDir: String

    init {
        contextRef = WeakReference(context)
        listenerRef = WeakReference(listener)
    }

    override fun doInBackground(vararg recordLists: List<Record>): Unit {
        try {
            val records = recordLists[0]
            if (records.isEmpty()) {
                val message = contextRef.get()?.getString(R.string.backup_error_description_no_records)!!
                listenerRef.get()?.onFail(message)
            }


            if (contextRef.get() == null) {
                return
            }
            appFilesDir = contextRef.get()?.filesDir.toString()

            this.driveService = buildDriveService(account)

            if (driveService == null) {
                return
            }


            // save images
            val imageFileNames = extractImageFileNames(records)
            val imageFileNameToDriveFileId = saveImages(imageFileNames)

            // save records
            val backupData = BackupData(records, imageFileNameToDriveFileId)
            val fileId = saveData(backupData)
            if (fileId == null) {
                val message = contextRef.get()?.getString(R.string.backup_error_description)!!
                listenerRef.get()?.onFail(message)
                return
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            throw e
        }
    }

    override fun onProgressUpdate(vararg statusArray: BackupStatus?) {
        val status = statusArray[0]!!
        Log.i(TAG, "*** In progress: (%d/%d) *** ".format(status.finishFilesCount, status.allFilesCount))
        listenerRef.get()?.onProgress(status)
    }

    override fun onPostExecute(result: Unit?) {
        Log.i(TAG, "*** Finished! *** ")
        listenerRef.get()?.onProgress(BackupStatus(BackupStatus.BACKUP_STATUS_CODE_COMPLETE))
    }

    private fun buildDriveService(account: Account): Drive? {
        if (contextRef.get() == null) {
            return null
        }

        val credential = GoogleAccountCredential.usingOAuth2(contextRef.get(), Collections.singleton(DriveScopes.DRIVE_APPDATA))
        credential.setSelectedAccount(this.account)

        return Drive.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory(), credential)
                .setApplicationName("BeforeAndAfter")
                .build()
    }

    private fun saveData(data: BackupData): String? {
        publishProgress(BackupStatus(BackupStatus.BACKUP_STATUS_CODE_SAVING_RECORDS))
        if (contextRef.get() == null) {
            return null
        }

        val recordsJson = Gson().toJson(data)
        Log.d(TAG, "recordsJson: ${recordsJson.toString()}")
        PrintWriter(OutputStreamWriter(contextRef.get()?.openFileOutput(FILE_NAME, Context.MODE_PRIVATE), "UTF-8")).use {
            it.append(recordsJson)
            it.close()
        }

        var fileMetadata = File()
        fileMetadata.setName(FILE_NAME)
        fileMetadata.setParents(Collections.singletonList(DRIVE_SPACE))
        val filePath = java.io.File(fileNameToFilePath(FILE_NAME))
        val mediaContent = FileContent("application/json", filePath)
        val file = driveService?.files()?.create(fileMetadata, mediaContent)?.setFields("id")?.execute()
        Log.i(TAG, "File ID: " + file?.id)
        return file?.id
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

    private fun saveImages(imageFileNames: List<String>): Map<String, String> {
        val imageFileIds = hashMapOf<String, String>()
        val size = imageFileNames.size
        for ((index, imageFileName) in imageFileNames.withIndex()) {
            publishProgress(BackupStatus(BackupStatus.BACKUP_STATUS_CODE_SAVING_IMAGES, index + 1, size))
            var fileMetadata = File()
//            val fileName = filePathToFileName(imageFilePath)
            fileMetadata.setName(imageFileName)
            fileMetadata.setParents(Collections.singletonList("appDataFolder"))
            val filePath = java.io.File(fileNameToFilePath(imageFileName))
            val mediaContent = FileContent("image/jpg", filePath)
            val file = driveService?.files()?.create(fileMetadata, mediaContent)?.setFields("id")?.execute()
            Log.i(TAG, "Image File ID: " + file?.id)
            if (file == null) {
                continue
            }
            imageFileIds[imageFileName] = file.id
        }
        return imageFileIds
    }

    private fun fileNameToFilePath(fileName: String): String {
        return "%s/%s".format(appFilesDir, fileName)
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
        fun onFail(message: String)
    }

}