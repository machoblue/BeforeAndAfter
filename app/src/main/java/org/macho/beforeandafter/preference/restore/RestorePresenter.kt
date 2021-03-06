package org.macho.beforeandafter.preference.restore

import android.accounts.Account
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.services.drive.DriveScopes
import org.macho.beforeandafter.R
import org.macho.beforeandafter.preference.backup.BackupPresenter
import org.macho.beforeandafter.shared.data.record.RecordRepository
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.Analytics
import javax.inject.Inject

@ActivityScoped
class RestorePresenter @Inject constructor(val recordRepository: RecordRepository): RestoreContract.Presenter, RestoreTask.RestoreTaskListener {
    companion object {
        const val TAG = "RestorePresenter"
        const val RC_SIGN_IN = 9001
        const val RC_RECOVERABLE = 9002
    }
    var view: RestoreContract.View? = null

    @Inject
    lateinit var context: Context

    private var restoreTask: RestoreTask? = null

    lateinit var googleSignInClient: GoogleSignInClient
    var account: Account? = null

    lateinit var analytics: Analytics

    override fun restore() {
        analytics = Analytics(context)
        analytics.logEvent(Analytics.Event.RESTORE_START)

        val account: Account? = GoogleSignIn.getLastSignedInAccount(context)?.account;
        if (account != null) {
            this.account = account
            restoreRecords()
            return
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)

        val signInIntent = googleSignInClient.getSignInIntent()
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
                Log.w(TAG, "*** RestorePresenter.result.when.RC_RECOVERABLE ***")
                restoreRecords()
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

            restoreRecords()

        } catch (e: ApiException) {
            // Clear the local account
            account = null
            throw RuntimeException(e)
        }

    }

    private fun restoreRecords() {
        restoreTask = RestoreTask(context, this.account!!, this)
        restoreTask?.execute()
    }

    override fun takeView(view: RestoreContract.View) {
        this.view = view
        view.setFinishButtonEnabled(false)
    }

    override fun dropView() {
        this.view = null
    }

    override fun cancelRestore() {
        restoreTask?.cancel(true)
    }


    // MARK: BackupTask.BackupTaskListener
    override fun onProgress(status: RestoreTask.RestoreStatus) {
        Log.d("RestorePresetner", "*** onProgress ***")
        when (status.statusCode) {
            RestoreTask.RestoreStatus.RESTORE_STATUS_CODE_FETCHING_RECORDS -> {
                val title = context.getString(R.string.backup_status_message_title_format).format(context.getString(R.string.restore_status_message_title_searching_file), 1, 3)
                val description = ""
                val progress = 10
                view?.setBackupStatusMessageTitle(title)
                view?.setBackupStatusMessageDescription(description)
                view?.setProgress(progress)
            }
            RestoreTask.RestoreStatus.RESTORE_STATUS_CODE_FETCHING_RECORDS -> {
                val title = context.getString(R.string.backup_status_message_title_format).format(context.getString(R.string.restore_status_message_title_fetching_records), 2, 3)
                val description = ""
                val progress = 20
                view?.setBackupStatusMessageTitle(title)
                view?.setBackupStatusMessageDescription(description)
                view?.setProgress(progress)
            }
            RestoreTask.RestoreStatus.RESTORE_STATUS_CODE_FETCHING_IMAGES -> {
                val title = context.getString(R.string.backup_status_message_title_format).format(context.getString(R.string.restore_status_message_title_fetching_images), 3, 3)
                val description = context.getString(R.string.restore_status_message_description_format).format(status.finishFilesCount, status.allFilesCount)
                val progress = 20 + ((status.finishFilesCount.toFloat() / status.allFilesCount) * 70).toInt()
                view?.setBackupStatusMessageTitle(title)
                view?.setBackupStatusMessageDescription(description)
                view?.setProgress(progress)
            }
            RestoreTask.RestoreStatus.RESTORE_STATUS_CODE_COMPLETE -> {
                val title = context.getString(R.string.restore_status_message_complete)
                val description = ""
                val progress = 100
                view?.setBackupStatusMessageTitle(title)
                view?.setBackupStatusMessageDescription(description)
                view?.setProgress(progress)
                view?.setFinishButtonEnabled(true)
            }
            RestoreTask.RestoreStatus.RESTORE_STATUS_CODE_ERROR_RECOVERABLE -> {
                view?.startActivityForResult(restoreTask!!.recoverableAuthIOException!!.intent, RC_RECOVERABLE)
                analytics.logEvent(Analytics.Event.RESTORE_RECOVERABLE_ERROR)
            }
            RestoreTask.RestoreStatus.RESTORE_STATUS_CODE_ERROR_DRIVE_CONNECTION_FAILED -> {
                view?.showAlert(context.getString(R.string.restore_error_title), context.getString(R.string.backup_error_drive_connection_error))
            }
            RestoreTask.RestoreStatus.RESTORE_STATUS_CODE_ERROR_BACKUPFILE_FORMAT_INVALID -> {
                view?.showAlert(context.getString(R.string.restore_error_title), context.getString(R.string.restore_error_file_format_invalid))
            }
        }
    }

    override fun onComplete() {
        val failCount = restoreTask?.failCount ?: 0
        if (failCount == 0) {
            analytics.logEvent(Analytics.Event.RESTORE_FINISH_SUCCESS)

        } else {
            val bundle = Bundle();
            bundle.putInt("fail_count", failCount);
            analytics.logEvent(Analytics.Event.RESTORE_FINISH_PARTIAL_FAIL, bundle)
            view?.showAlert(context.getString(R.string.restore_error_title), String.format(context.getString(R.string.restore_error_message_cannot_restore_all_photo), failCount))
        }
    }
}