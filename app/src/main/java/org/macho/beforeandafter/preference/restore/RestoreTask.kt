package org.macho.beforeandafter.preference.restore

import android.accounts.Account
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.gson.Gson
import org.macho.beforeandafter.R
import org.macho.beforeandafter.preference.backup.BackupData
import org.macho.beforeandafter.preference.backup.BackupTask
import org.macho.beforeandafter.record.Record
import java.io.*
import java.lang.ref.WeakReference
import java.util.*


class RestoreTask(context: Context, val account: Account, listener: RestoreTask.RestoreTaskListener): AsyncTask<Void, RestoreTask.RestoreStatus, List<Record>>() {
    companion object {
        const val TAG = "RestoreTask"
    }

    private val contextRef: WeakReference<Context>
    private val listenerRef: WeakReference<RestoreTask.RestoreTaskListener>

    private var driveService: Drive? = null

    private lateinit var appFilesDir: String

    init {
        contextRef = WeakReference(context)
        listenerRef = WeakReference(listener)
    }

    override fun doInBackground(vararg p0: Void?): List<Record> {
        try {
            if (contextRef.get() == null) {
                return mutableListOf()
            }
            appFilesDir = contextRef.get()?.filesDir.toString()

            this.driveService = buildDriveService(account)

            if (driveService == null) {
                return mutableListOf()
            }

            val backupData = fetchBackupData()

            if (backupData == null) {
                listenerRef.get()?.onFail(R.string.restore_error_cannot_fetch_metadata_description)
                return mutableListOf()
            }

            fetchImageAndStoreInLocalAppFilesDir(backupData.imageFileNameToDriveFileId)

            val records = backupData.records
            return records
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            throw e
        }
    }

    override fun onProgressUpdate(vararg values: RestoreStatus?) {
        if (listenerRef.get() == null) {
            return
        }

        if (values == null || values.size == 0 || values[0] == null) {
            return
        }

        listenerRef.get()?.onProgress(values[0]!!)
    }

    override fun onPostExecute(result: List<Record>?) {
        if (listenerRef.get() == null) {
            return
        }

        listenerRef.get()?.onProgress(RestoreStatus(RestoreStatus.RESTORE_STATUS_CODE_COMPLETE))
        if (result == null) {
            listenerRef.get()?.onComplete(mutableListOf<Record>())
        } else {
            listenerRef.get()?.onComplete(result)
        }
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

    private fun fetchBackupData(): BackupData? {
        listenerRef.get()?.onProgress(RestoreStatus(RestoreStatus.RESTORE_STATUS_CODE_FETCHING_RECORDS))
        // search for backup.json
        var backupJsonFileId: Pair<String, DateTime>? = null
        val filesListRequest = driveService!!.files().list()
                .setSpaces("appDataFolder")
                .setFields("nextPageToken, files(id, name, createdTime)")
                .setPageSize(100)
        do {
            var fileList = filesListRequest.execute()
            for (file in fileList.files) {
                Log.i(TAG, "fileName:${file.name}, createdTime: ${file.createdTime}")
                if (file.name.equals(BackupTask.FILE_NAME)) {
                    if (backupJsonFileId == null || file.createdTime.value > backupJsonFileId.second.value) {
                        backupJsonFileId = file.id to file.createdTime
                    }
                }
            }
            filesListRequest.setPageToken(fileList.nextPageToken)

        } while (fileList.nextPageToken != null && !fileList.nextPageToken.isEmpty())

        if (backupJsonFileId == null) {
            return null
        }

        Log.i(TAG, "*** ${backupJsonFileId}")

        val tempFile = File("${contextRef.get()!!.filesDir}/backup.json")
        BufferedOutputStream(FileOutputStream(tempFile)).use {
            driveService!!.files().get(backupJsonFileId.first).executeMediaAndDownloadTo(it)
        }

        var json: String? = null
        BufferedReader(InputStreamReader(FileInputStream(tempFile))).use {
            json = it.readText()
        }
        Log.i(TAG, "reponse:${json}")


        // parse backup.json
        if (json == null) {
            listenerRef.get()?.onFail(R.string.restore_error_cannot_download_content_description)
        }
        val backupData = Gson().fromJson<BackupData>(json, BackupData::class.java)

        return backupData
    }

    private fun fetchImageAndStoreInLocalAppFilesDir(imageFileNameToDriveFileIds: Map<String, String>) {
        if (contextRef.get() == null) {
            return
        }

        val size = imageFileNameToDriveFileIds.size

        for ((index, imageFileNameToDriveFileId) in imageFileNameToDriveFileIds.entries.withIndex()) {
            listenerRef.get()?.onProgress(RestoreStatus(RestoreStatus.RESTORE_STATUS_CODE_FETCHING_IMAGES, index, size))
            Log.i(TAG, "fileId: ${imageFileNameToDriveFileId.value}")
            val imageFileName = imageFileNameToDriveFileId.key
            val driveFileId = imageFileNameToDriveFileId.value
            BufferedOutputStream(contextRef.get()!!.openFileOutput(imageFileName, Context.MODE_PRIVATE)).use {
                driveService!!.files().get(driveFileId).executeMediaAndDownloadTo(it)
            }
        }
    }

    class RestoreStatus(val statusCode: Int, val finishFilesCount: Int = 0, val allFilesCount: Int = 0) {
        companion object {
            const val RESTORE_STATUS_CODE_FETCHING_RECORDS = 0
            const val RESTORE_STATUS_CODE_FETCHING_IMAGES = 1
            const val RESTORE_STATUS_CODE_COMPLETE = 2
        }
    }

    interface RestoreTaskListener {
        fun onProgress(status: RestoreStatus)
        fun onComplete(records: List<Record>)
        fun onFail(resourceId: Int)
    }

}