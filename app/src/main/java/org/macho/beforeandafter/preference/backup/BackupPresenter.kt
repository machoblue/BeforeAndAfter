package org.macho.beforeandafter.preference.backup

import android.accounts.Account
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
import com.google.api.services.drive.DriveScopes
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.data.RecordRepository
import org.macho.beforeandafter.shared.di.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class BackupPresenter @Inject constructor(val recordRepository: RecordRepository): BackupContract.Presenter, BackupTask.BackupTaskListener {
    companion object {
        const val RC_SIGN_IN = 9001
    }
    var view: BackupContract.View? = null

    @Inject
    lateinit var context: Context

    lateinit var googleSignInClient: GoogleSignInClient
    var account: Account? = null

    private var backupTask: BackupTask? = null

    override fun backup() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)

        val signInIntent = googleSignInClient.getSignInIntent()
        view?.startActivityForResult(signInIntent, BackupPresenter.RC_SIGN_IN)
    }

    override fun result(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Store the account from the result
            this.account = account.account

            // Asynchronously access the People API for the account
            Log.d("BackupFragment", "handleSignInResult")

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
            }
            BackupTask.BackupStatus.BACKUP_STATUS_CODE_SAVING_RECORDS -> {
                title = context.getString(R.string.backup_status_message_title_format).format(context.getString(R.string.backup_status_message_title_saving_records), 2, 2)
                progress = 90
            }
            BackupTask.BackupStatus.BACKUP_STATUS_CODE_COMPLETE -> {
                title = context.getString(R.string.backup_status_message_complete)
                progress = 100
                view?.setFinishButtonEnabled(true)
            }
        }
        view?.setBackupStatusMessageTitle(title)
        view?.setBackupStatusMessageDescription(description)
        view?.setProgress(progress)
    }

    override fun onFail(message: String) {
        view?.showAlert(context.getString(R.string.backup_error_title), message)
    }

}