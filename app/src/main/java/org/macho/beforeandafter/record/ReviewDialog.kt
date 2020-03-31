package org.macho.beforeandafter.record

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.navigation.fragment.findNavController
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.util.Analytics
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil

class ReviewDialog: androidx.fragment.app.DialogFragment() {

    lateinit var analytics: Analytics

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        analytics = Analytics(context!!)

        return AlertDialog.Builder(activity)
                .setTitle(R.string.review)
                .setView(R.layout.review_dialog_frag)
                .setPositiveButton(R.string.review_ok) { _, i ->
                    val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.review_url)))
                    startActivity(intent)
                    SharedPreferencesUtil.setBoolean(context!!, SharedPreferencesUtil.Key.STORE_REVIEW_PROMPT_COMPLETED, true)
                    analytics.logEvent(Analytics.Event.STORE_REVIEW_DIALOG_OPEN_STORE)
                }
                .setNegativeButton(R.string.cancel) { _, i ->
                    analytics.logEvent(Analytics.Event.STORE_REVIEW_DIALOG_CANCEL)
                }
                .setNeutralButton(R.string.review_neutral) { _, i ->
                    SharedPreferencesUtil.setBoolean(context!!, SharedPreferencesUtil.Key.STORE_REVIEW_PROMPT_COMPLETED, true)
                    analytics.logEvent(Analytics.Event.STORE_REVIEW_DIALOG_HIDE)
                }
                .create()
    }

    override fun onStart() {
        super.onStart()

        dialog?.findViewById<Button>(R.id.bugReportButton)?.setOnClickListener {
            val action = ReviewDialogDirections.actionReviewDialogToBugReportFragment2()
            findNavController().navigate(action)
        }

        analytics.logEvent(Analytics.Event.STORE_REVIEW_DIALOG_APPEAR)
    }
}