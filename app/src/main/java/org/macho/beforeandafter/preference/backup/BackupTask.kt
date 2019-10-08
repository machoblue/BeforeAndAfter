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
import org.macho.beforeandafter.record.Record
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.lang.ref.WeakReference
import java.util.*


class BackupTask(context: Context, val account: Account, listener: BackupTaskListener): AsyncTask<List<Record>, BackupTask.BackupStatus, Unit>() {
    companion object {
        const val TAG = "BackupTask"
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
        if (contextRef.get() == null) {
            return
        }
        appFilesDir = contextRef.get()?.filesDir.toString()

        this.driveService = buildDriveService(account)

        if (driveService == null) {
            return
        }

        val records = recordLists[0]

        saveData(records)

        val imageFilePaths = extractImageFilePaths(records)
        saveImages(imageFilePaths)
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

    private fun saveData(records: List<Record>) {
        publishProgress(BackupStatus(BackupStatus.BACKUP_STATUS_CODE_SAVING_RECORDS))
        if (contextRef.get() == null) {
            return
        }

        val recordsJson = Gson().toJson(records)
        Log.d(TAG, "recordsJson: ${recordsJson.toString()}")
        val fileName = "records.json"
        PrintWriter(OutputStreamWriter(contextRef.get()?.openFileOutput(fileName, Context.MODE_PRIVATE), "UTF-8")).use {
            it.append(recordsJson)
            it.close()
        }

        var fileMetadata = File()
        fileMetadata.setName("records.json")
        fileMetadata.setParents(Collections.singletonList("appDataFolder"))
        val filePath = java.io.File(fileNameToFilePath(fileName))
        val mediaContent = FileContent("application/json", filePath)
        val file = driveService?.files()?.create(fileMetadata, mediaContent)?.setFields("id")?.execute()
        Log.i(TAG, "File ID: " + file?.id)
    }

    private fun extractImageFilePaths(records: List<Record>): List<String> {
        var imageFilePaths = mutableListOf<String>()
        for (record in records) {
            val frontImageFileName = record.frontImagePath
            if (frontImageFileName != null && java.io.File(fileNameToFilePath(frontImageFileName)).exists()) {
                imageFilePaths.add(fileNameToFilePath(frontImageFileName))
            }
            val sideImageFileName = record.sideImagePath
            if (sideImageFileName != null && java.io.File(fileNameToFilePath(sideImageFileName)).exists()) {
                imageFilePaths.add(fileNameToFilePath(sideImageFileName))
            }
        }
        return imageFilePaths
    }

    private fun saveImages(imageFilePaths: List<String>) {
        for ((index, imageFilePath) in imageFilePaths.withIndex()) {
            publishProgress(BackupStatus(BackupStatus.BACKUP_STATUS_CODE_SAVING_IMAGES, index + 1, imageFilePaths.size))
            var fileMetadata = File()
            val fileName = filePathToFileName(imageFilePath)
            fileMetadata.setName(fileName)
            fileMetadata.setParents(Collections.singletonList("appDataFolder"))
            val filePath = java.io.File(imageFilePath)
            val mediaContent = FileContent("image/jpg", filePath)
        }
    }

    private fun fileNameToFilePath(fileName: String): String {
        return "%s/%s".format(appFilesDir, fileName)
    }

    private fun filePathToFileName(filePath: String): String {
        return filePath.replace(appFilesDir + "/", "")
    }

    class BackupStatus(val statusCode: Int, val finishFilesCount: Int = 0, val allFilesCount: Int = 0) {
        companion object {
            const val BACKUP_STATUS_CODE_SAVING_RECORDS = 0
            const val BACKUP_STATUS_CODE_SAVING_IMAGES = 1
            const val BACKUP_STATUS_CODE_COMPLETE = 2
        }
    }

    interface BackupTaskListener {
        fun onProgress(status: BackupStatus)
    }

}