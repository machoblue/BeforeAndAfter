package org.macho.beforeandafter.preference.backup

import android.content.Intent
import org.macho.beforeandafter.shared.BaseContract

interface BackupContract {
    interface View: BaseContract.View<Presenter> {
        fun finish()
        fun setBackupStatusMessageTitle(title: String)
        fun setBackupStatusMessageDescription(description: String)
        fun startActivityForResult(intent: Intent?, requestCode: Int)
        fun setProgress(value: Int)
        fun setFinishButtonEnabled(enabled: Boolean)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun backup()
        fun result(requestCode: Int, resultCode: Int, data: Intent)
    }
}