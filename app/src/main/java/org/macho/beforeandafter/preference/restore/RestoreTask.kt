package org.macho.beforeandafter.preference.restore

import android.accounts.Account
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
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


class RestoreTask(context: Context, val account: Account, listener: RestoreTaskListener): AsyncTask<Void, RestoreTask.RestoreStatus, List<Record>?>() {
    companion object {
        const val TAG = "RestoreTask"
    }

    private val contextRef: WeakReference<Context>
    private val listenerRef: WeakReference<RestoreTaskListener>

    private lateinit var driveService: Drive

    init {
        contextRef = WeakReference(context)
        listenerRef = WeakReference(listener)
    }

    override fun doInBackground(vararg p0: Void?): List<Record>? {
        try {
            this.driveService = buildDriveService() ?: let {
                it.cancel(true)
                return@doInBackground null
            }

            publishProgress(RestoreStatus(RestoreStatus.RESTORE_STATUS_CODE_FETCHING_RECORDS))

            val backupData: BackupData = fetchLatestBackupFileId()?.let { fileId ->
                fetchBackupData(fileId) ?: let {
                    it.cancel(true)
                    listenerRef.get()?.onFail(R.string.restore_error_file_format_invalid)
                    return@doInBackground null
                }
            } ?: let {
                return@doInBackground emptyList() // latestFileIdがない場合、ただBackupしてないだけ。emptyListを返し、backupを促す。
            }

            backupData.imageFileNameToDriveFileId.entries.forEachIndexed { index, entry ->
                if (isCancelled) return@doInBackground null

                publishProgress(RestoreStatus(RestoreStatus.RESTORE_STATUS_CODE_FETCHING_IMAGES, index, backupData.imageFileNameToDriveFileId.size))

                val (fileName, fileId) = entry
                fetchImage(fileName, fileId)
            }

            return backupData.records

        } catch (e: UserRecoverableAuthIOException) {
            Log.w(TAG, "doInBackground.catch UserRecoverableException:${e::class.java}", e)
            listenerRef.get()?.onRecoverableAuthErrorOccured(e)
            cancel(true)
            return null

        } catch (e: IOException) {
            Log.e(TAG, "doInBackground.catch IOException:${e::class.java}", e)
            throw e

        } catch (e: Exception) {
            Log.e(TAG, "doInBackground.catch Exception:${e::class.java}", e)
            throw e
        }
    }

    override fun onProgressUpdate(vararg values: RestoreStatus?) {
        values.firstOrNull()?.let { status ->
            listenerRef.get()?.onProgress(status)
        }
    }

    override fun onPostExecute(result: List<Record>?) {
        listenerRef.get()?.onProgress(RestoreStatus(RestoreStatus.RESTORE_STATUS_CODE_COMPLETE))
        if (result != null) {
            listenerRef.get()?.onComplete(result)
        }
    }

    override fun onCancelled() {
        Log.w(TAG, "RestoreTask has been cancelled.")
    }

    override fun onCancelled(result: List<Record>?) {
        Log.w(TAG, "RestoreTask has been cancelled. :${result}")
    }

    private fun buildDriveService(): Drive? {
        return contextRef.get()?.let { context ->
            GoogleAccountCredential.usingOAuth2(context, Collections.singleton(DriveScopes.DRIVE_APPDATA)).let { credential ->
                credential.selectedAccount = account
                Drive.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory(), credential)
                    .setApplicationName("BeforeAndAfter")
                    .build()
            }
        }
    }

    private fun fetchBackupData(fileId: String): BackupData? {
        return contextRef.get()?.let { context ->
            BufferedOutputStream(context.openFileOutput("temp/${BackupTask.FILE_NAME}", Context.MODE_PRIVATE)).use { bos ->
                driveService.files().get(fileId).executeMediaAndDownloadTo(bos)
            }

            BufferedReader(InputStreamReader(context.openFileInput("temp/${BackupTask.FILE_NAME}"))).use { br ->
                br.readText().let { json ->
                    Gson().fromJson<BackupData>(json, BackupData::class.java)
                }
            }
        }
    }

    private fun fetchLatestBackupFileId(): String? {
        var fileIdToCreatedTime: Pair<String, DateTime>? = null

        val filesListRequest = driveService.files().list()
                .setSpaces("appDataFolder")
                .setFields("nextPageToken, files(id, name, createdTime)")
                .setPageSize(100)

        do {
            val fileList = filesListRequest.execute()
            for (file in fileList.files) {
                Log.i(TAG, "fileName:${file.name}, createdTime: ${file.createdTime}")
                if (file.name.equals(BackupTask.FILE_NAME)
                    && file.createdTime.value > fileIdToCreatedTime?.second?.value ?: 0)
                {
                    fileIdToCreatedTime = file.id to file.createdTime
                }
            }
            filesListRequest.pageToken = fileList.nextPageToken

        } while (fileList.nextPageToken != null && !fileList.nextPageToken.isEmpty())

        Log.i(TAG, "*** ${fileIdToCreatedTime}")
        return fileIdToCreatedTime?.first
    }

    private fun fetchImage(fileName: String, fileId: String) {
        contextRef.get()?.let { context ->
            BufferedOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE)).use {
                driveService.files().get(fileId).executeMediaAndDownloadTo(it)
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
        fun onRecoverableAuthErrorOccured(e: UserRecoverableAuthIOException)
    }
}
