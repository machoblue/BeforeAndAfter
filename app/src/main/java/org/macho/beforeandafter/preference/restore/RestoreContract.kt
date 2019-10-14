package org.macho.beforeandafter.preference.restore

import android.content.Intent
import org.macho.beforeandafter.shared.BaseContract

interface RestoreContract {
    interface View: BaseContract.View<Presenter> {
        fun finish()
        fun setBackupStatusMessageTitle(title: String)
        fun setBackupStatusMessageDescription(description: String)
        fun startActivityForResult(intent: Intent?, requestCode: Int)
        fun setProgress(value: Int)
        fun setFinishButtonEnabled(enabled: Boolean)
        fun showAlert(title: String, description: String)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun restore()
        fun cancelRestore()
        fun result(requestCode: Int, resultCode: Int, data: Intent)
    }
}