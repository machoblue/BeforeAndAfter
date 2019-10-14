package org.macho.beforeandafter.preference.restore

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
import org.macho.beforeandafter.preference.backup.BackupPresenter
import org.macho.beforeandafter.record.Record
import org.macho.beforeandafter.shared.data.RecordRepository
import org.macho.beforeandafter.shared.di.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class RestorePresenter @Inject constructor(val recordRepository: RecordRepository): RestoreContract.Presenter, RestoreTask.RestoreTaskListener {
    companion object {
        const val TAG = "RestorePresenter"
        const val RC_SIGN_IN = 9001
    }
    var view: RestoreContract.View? = null

    @Inject
    lateinit var context: Context

    private var restoreTask: RestoreTask? = null

    lateinit var googleSignInClient: GoogleSignInClient
    var account: Account? = null

    override fun restore() {
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
        var title = ""
        var description = ""
        var progress = 0
        when (status.statusCode) {
            RestoreTask.RestoreStatus.RESTORE_STATUS_CODE_FETCHING_RECORDS -> {
                title = context.getString(R.string.backup_status_message_title_format).format(context.getString(R.string.restore_status_message_title_fetching_records), 1, 2)
                progress = 10
            }
            RestoreTask.RestoreStatus.RESTORE_STATUS_CODE_FETCHING_IMAGES -> {
                title = context.getString(R.string.backup_status_message_title_format).format(context.getString(R.string.restore_status_message_title_fetching_images), 2, 2)
                description = context.getString(R.string.restore_status_message_description_format).format(status.finishFilesCount, status.allFilesCount)
                progress = 10 + ((status.finishFilesCount.toFloat() / status.allFilesCount) * 80).toInt()
            }
            RestoreTask.RestoreStatus.RESTORE_STATUS_CODE_COMPLETE -> {
                title = context.getString(R.string.restore_status_message_complete)
                progress = 100
                view?.setFinishButtonEnabled(true)
            }
        }
        view?.setBackupStatusMessageTitle(title)
        view?.setBackupStatusMessageDescription(description)
        view?.setProgress(progress)
    }

    override fun onComplete(records: List<Record>) {
        if (records.isEmpty()) {
            view?.showAlert(context.getString(R.string.restore_error_no_data_title), context.getString(R.string.restore_error_no_data_description))
            return
        }
        records.forEach { restoredRecord ->
            recordRepository.getRecord(restoredRecord.date) { existingRecord ->
                if (existingRecord == null) {
                    recordRepository.register(restoredRecord)

                } else {
                    recordRepository.update(existingRecord)
                }
            }
        }
    }

    override fun onFail(resourceId: Int) {
        view?.showAlert(context.getString(R.string.restore_error_title), context.getString(resourceId))
    }

}