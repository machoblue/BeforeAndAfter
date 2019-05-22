package org.macho.beforeandafter.record

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.DialogFragment
import org.macho.beforeandafter.R

class ReviewDialog: DialogFragment() {
    companion object {
        fun newInstance(): DialogFragment {
            return ReviewDialog()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setTitle(R.string.review)
                .setMessage(R.string.review_message)
                .setPositiveButton(R.string.ok) { _, i ->
                    val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=org.macho.beforeandafter&hl=ja"))
                    startActivity(intent)
                    val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
                    preferences.edit().putBoolean("REVIEWED", true).commit()
                }
                .setNegativeButton(R.string.cancel) { dialogInterface, i ->
                    val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
                    preferences.edit().putBoolean("REVIEWED", true).commit()
                }
                .create()
    }
}