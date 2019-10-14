package org.macho.beforeandafter.shared.view

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import org.macho.beforeandafter.R

class AlertDialog: DialogFragment() {

    private lateinit var title: String
    private lateinit var message: String
    private var onComplete: (() -> Unit)? = null

    companion object {
        fun newInstance(activity: Activity, title: String, message: String, onComplete: (() -> Unit)? = null): DialogFragment {
            val alertDialog = AlertDialog()
            alertDialog.title = title
            alertDialog.message = message
            alertDialog.onComplete = onComplete
            return alertDialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(activity).setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok) { dialogInterface, i ->
                    onComplete?.invoke()
                }
                .create()
    }
}