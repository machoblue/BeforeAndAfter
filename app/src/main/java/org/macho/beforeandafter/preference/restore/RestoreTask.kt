package org.macho.beforeandafter.preference.restore

import android.accounts.Account
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.gson.Gson
import org.macho.beforeandafter.preference.backup.BackupData
import org.macho.beforeandafter.preference.backup.BackupTask
import org.macho.beforeandafter.preference.backup.DriveUtil
import org.macho.beforeandafter.shared.data.record.RecordDaoImpl
import org.macho.beforeandafter.shared.data.restoreimage.RestoreImage
import org.macho.beforeandafter.shared.data.restoreimage.RestoreImageDaoImpl
import java.io.*
import java.lang.ref.WeakReference


class RestoreTask(context: Context, val account: Account, listener: RestoreTaskListener): AsyncTask<Void, RestoreTask.RestoreStatus, Unit>() {
    companion object {
        const val TAG = "RestoreTask"
        const val TEMP_FILE = "temp_backup.json"
    }

    private val contextRef: WeakReference<Context> = WeakReference(context)
    private val listenerRef: WeakReference<RestoreTaskListener> = WeakReference(listener)

    private lateinit var driveService: Drive

    var recoverableAuthIOException: UserRecoverableAuthIOException? = null
    var failCount = 0

    override fun doInBackground(vararg p0: Void?): Unit {
        try {
            this.driveService = contextRef.get()?.let { context ->
                DriveUtil.buildDriveService(context, account)
            } ?: let {
                it.cancel(true)
                publishProgress(RestoreStatus(RestoreStatus.RESTORE_STATUS_CODE_ERROR_DRIVE_CONNECTION_FAILED))
                return
            }

            publishProgress(RestoreStatus(RestoreStatus.RESTORE_STATUS_CODE_FINDING_FILE))

            val backupData: BackupData = fetchLatestBackupFileId()?.let { fileId ->
                publishProgress(RestoreStatus(RestoreStatus.RESTORE_STATUS_CODE_FETCHING_RECORDS))
                fetchBackupData(fileId) ?: let {
                    it.cancel(true)
                    publishProgress(RestoreStatus(RestoreStatus.RESTORE_STATUS_CODE_ERROR_BACKUPFILE_FORMAT_INVALID))
                    return
                }
            } ?: let {
                return // latestFileIdがない場合、ただBackupしてないだけ。emptyListを返し、backupを促す。
            }

            backupData.records.forEach {
                RecordDaoImpl().update(it)
            }

            val existingRestoreImages = RestoreImageDaoImpl().findAll()
            backupData.imageFileNameToDriveFileId.map { RestoreImage(it.key, it.value) }.forEach {
                if (!existingRestoreImages.contains(it)) {
                    RestoreImageDaoImpl().insertOrUpdate(it)
                }
            }


            RestoreImageDaoImpl().findAll()
                .filter { it.status != RestoreImage.Status.COMPLETE }
                .sortedBy { it.status }
                .forEachIndexed { index, restoreImage ->
                    if (isCancelled) return
                    publishProgress(RestoreStatus(RestoreStatus.RESTORE_STATUS_CODE_FETCHING_IMAGES, index, backupData.imageFileNameToDriveFileId.size))
                    RestoreImageDaoImpl().insertOrUpdate(RestoreImage(restoreImage.imageFileName, restoreImage.driveFileId, RestoreImage.Status.PROCESSING))
                    try {
                        fetchImage(restoreImage.imageFileName, restoreImage.driveFileId)
                        RestoreImageDaoImpl().delete(restoreImage.imageFileName)

                    } catch (e: Exception) {
                        Log.e(TAG, "doInBackground.catch Exception:${e::class.java}", e)
                        Crashlytics.logException(e)
                        failCount++
                    }
                }

        } catch (e: UserRecoverableAuthIOException) {
            Log.w(TAG, "doInBackground.catch UserRecoverableException:${e::class.java}", e)
            this.recoverableAuthIOException = e
            publishProgress(RestoreStatus(RestoreStatus.RESTORE_STATUS_CODE_ERROR_RECOVERABLE))
            cancel(true)
            return

        } catch (e: Exception) {
            Log.e(TAG, "doInBackground.catch Exception:${e::class.java}", e)
            Crashlytics.logException(e)
            throw e
        }
    }

    override fun onProgressUpdate(vararg values: RestoreStatus?) {
        values.firstOrNull()?.let { status ->
            listenerRef.get()?.onProgress(status)
        }
    }

    override fun onPostExecute(result: Unit) {
        listenerRef.get()?.onProgress(RestoreStatus(RestoreStatus.RESTORE_STATUS_CODE_COMPLETE))
        listenerRef.get()?.onComplete()
    }

    override fun onCancelled() {
        Log.w(TAG, "RestoreTask has been cancelled.")
    }

    override fun onCancelled(result: Unit?) {
        Log.w(TAG, "RestoreTask has been cancelled.")
    }

    private fun fetchBackupData(fileId: String): BackupData? {
        return contextRef.get()?.let { context ->
            BufferedOutputStream(context.openFileOutput(TEMP_FILE, Context.MODE_PRIVATE)).use { bos ->
                driveService.files().get(fileId).executeMediaAndDownloadTo(bos)
            }

            BufferedReader(InputStreamReader(context.openFileInput(TEMP_FILE))).use { br ->
                br.readText().let { json ->
                    Gson().fromJson<BackupData>(json, BackupData::class.java)
                }
            }
        }
    }

    private fun fetchLatestBackupFileId(): String? {
        val filesListRequest = driveService.files().list()
                .setSpaces(DriveUtil.DRIVE_SPACE_APPDATA)
                .setFields("nextPageToken, files(id, name, createdTime)")
                .setPageSize(100)

        val files = mutableListOf<File>()
        do {
            val fileList = filesListRequest.execute() ?: return null
            files.addAll(fileList.files)
            filesListRequest.pageToken = fileList.nextPageToken

        } while (fileList.nextPageToken != null && !fileList.nextPageToken.isEmpty())

        val latestFile = files.filter { it.name == BackupTask.FILE_NAME }.maxBy { it.createdTime.value }
        return latestFile?.id
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
            const val RESTORE_STATUS_CODE_FINDING_FILE = 0
            const val RESTORE_STATUS_CODE_FETCHING_RECORDS = 1
            const val RESTORE_STATUS_CODE_FETCHING_IMAGES = 2
            const val RESTORE_STATUS_CODE_COMPLETE = 3
            const val RESTORE_STATUS_CODE_ERROR_DRIVE_CONNECTION_FAILED = 1001
            const val RESTORE_STATUS_CODE_ERROR_BACKUPFILE_FORMAT_INVALID = 1002
            const val RESTORE_STATUS_CODE_ERROR_RECOVERABLE = 1003
        }
    }

    interface RestoreTaskListener {
        fun onProgress(status: RestoreStatus)
        fun onComplete()
    }
}
