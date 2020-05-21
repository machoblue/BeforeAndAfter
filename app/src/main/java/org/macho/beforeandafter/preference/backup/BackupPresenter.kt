package org.macho.beforeandafter.preference.backup

import android.accounts.Account
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.drive.DriveScopes
import com.google.firebase.analytics.FirebaseAnalytics
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.data.record.RecordRepository
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.Analytics
import org.macho.beforeandafter.shared.util.LogUtil
import javax.inject.Inject

@ActivityScoped
class BackupPresenter @Inject constructor(val recordRepository: RecordRepository): BackupContract.Presenter, BackupTask.BackupTaskListener {
    companion object {
        const val TAG = "BackupPresenter"
        const val RC_SIGN_IN = 9001
        const val RC_RECOVERABLE = 9002
    }
    var view: BackupContract.View? = null

    @Inject
    lateinit var context: Context

    lateinit var googleSignInClient: GoogleSignInClient
    var account: Account? = null

    private var backupTask: BackupTask? = null

    lateinit var analytics: Analytics

    override fun backup() {
        analytics = Analytics(context)
        analytics.logEvent(Analytics.Event.BACKUP_START)

        val account: Account? = GoogleSignIn.getLastSignedInAccount(context)?.account;
        if (account != null) {
            this.account = account
            backupRecords()
            return
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)

        val signInIntent = googleSignInClient.signInIntent
        view?.startActivityForResult(signInIntent, BackupPresenter.RC_SIGN_IN)
    }

    override fun result(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode != Activity.RESULT_OK) {
            return // do nothing
        }

        when (requestCode) {
            RC_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
            RC_RECOVERABLE -> {
                Log.w(TAG, "*** BackpuPresenter.result.when.RC_RECOVERABLE ***")
                backupRecords()
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Store the account from the result
            this.account = account?.account

            // Asynchronously access the People API for the account
            Log.d(TAG, "handleSignInResult")

            backupRecords()

        } catch (e: ApiException) {
            // Clear the local account
            account = null
            throw RuntimeException(e)
        }

    }

    private fun backupRecords() {
        recordRepository.getRecords { records ->
            val backupTask = BackupTask(context, this.account!!, this)
            backupTask.execute(records)
        }
    }

    override fun takeView(view: BackupContract.View) {
        this.view = view
        view.setFinishButtonEnabled(false)
    }

    override fun dropView() {
        this.view = null
    }

    override fun cancelBackup() {
        backupTask?.cancel(true)
    }


    // MARK: BackupTask.BackupTaskListener
    override fun onProgress(status: BackupTask.BackupStatus) {
        Log.d("BackupPresetner", "*** onProgress ***")
        var title = ""
        var description = ""
        var progress = 0
        when (status.statusCode) {
            BackupTask.BackupStatus.BACKUP_STATUS_CODE_SAVING_IMAGES -> {
                title = context.getString(R.string.backup_status_message_title_format).format(context.getString(R.string.backup_status_message_title_saving_images), 1, 2)
                description = context.getString(R.string.backup_status_message_description_format).format(status.finishFilesCount, status.allFilesCount)
                progress = ((status.finishFilesCount.toFloat() / status.allFilesCount) * 80).toInt()
                view?.setBackupStatusMessageTitle(title)
                view?.setBackupStatusMessageDescription(description)
                view?.setProgress(progress)
            }
            BackupTask.BackupStatus.BACKUP_STATUS_CODE_SAVING_RECORDS -> {
                title = context.getString(R.string.backup_status_message_title_format).format(context.getString(R.string.backup_status_message_title_saving_records), 2, 2)
                progress = 90
                view?.setBackupStatusMessageTitle(title)
                view?.setBackupStatusMessageDescription(description)
                view?.setProgress(progress)
            }
            BackupTask.BackupStatus.BACKUP_STATUS_CODE_COMPLETE -> {
                title = context.getString(R.string.backup_status_message_complete)
                progress = 100
                view?.setFinishButtonEnabled(true)
                view?.setBackupStatusMessageTitle(title)
                view?.setBackupStatusMessageDescription(description)
                view?.setProgress(progress)
                analytics.logEvent(Analytics.Event.BACKUP_FINISH)
            }
            BackupTask.BackupStatus.BACKUP_STATUS_CODE_ERROR_NO_RECORDS -> {
                view?.showAlert(context.getString(R.string.backup_error_title), context.getString(R.string.backup_error_description_no_records))
            }
            BackupTask.BackupStatus.BACKUP_STATUS_CODE_ERROR_DRIVE_CONNECTION_FAILED -> {
                view?.showAlert(context.getString(R.string.backup_error_title), context.getString(R.string.backup_error_drive_connection_error))
            }
            BackupTask.BackupStatus.BACKUP_STATUS_CODE_ERROR_FILES_CREATE_FAILED-> {
                view?.showAlert(context.getString(R.string.backup_error_title), context.getString(R.string.backup_error_files_create_failed))
            }
            BackupTask.BackupStatus.BACKUP_STATUS_CODE_ERROR_RECOVERABLE -> {
                val intent = backupTask?.recoverableAuthIOException?.intent ?: return
                view?.startActivityForResult(intent, RC_RECOVERABLE)
                analytics.logEvent(Analytics.Event.BACKUP_RECOVERABLE_ERROR)
            }
        }
    }
}