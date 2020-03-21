package org.macho.beforeandafter.record

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.navigation.fragment.findNavController
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
                .setView(R.layout.review_dialog_frag)
                .setPositiveButton(R.string.review_ok) { _, i ->
                    val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.review_url)))
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

    override fun onStart() {
        super.onStart()

        dialog?.findViewById<Button>(R.id.bugReportButton)?.setOnClickListener {
            val action = ReviewDialogDirections.actionReviewDialogToBugReportFragment2()
            findNavController().navigate(action)
        }
    }
}