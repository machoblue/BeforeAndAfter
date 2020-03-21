package org.macho.beforeandafter.record

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil

class ReviewDialog: androidx.fragment.app.DialogFragment() {
    companion object {
        fun newInstance(): androidx.fragment.app.DialogFragment {
            return ReviewDialog()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setTitle(R.string.review)
                .setMessage(R.string.review_message)
                .setPositiveButton(R.string.review_ok) { _, i ->
                    val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=org.macho.beforeandafter&hl=ja"))
                    startActivity(intent)
                    SharedPreferencesUtil.setBoolean(context!!, SharedPreferencesUtil.Key.STORE_REVIEW_PROMPT_COMPLETED, true)
                }
                .setNegativeButton(R.string.cancel) { _, i ->
                    // do nothing
                }
                .setNeutralButton(R.string.review_neutral) { _, i ->
                    SharedPreferencesUtil.setBoolean(context!!, SharedPreferencesUtil.Key.STORE_REVIEW_PROMPT_COMPLETED, true)
                }
                .create()
    }
}