package org.macho.beforeandafter.shared.view.ratingdialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.RatingBar
import androidx.fragment.app.FragmentManager
import dagger.android.support.DaggerAppCompatDialogFragment
import org.macho.beforeandafter.R
import javax.inject.Inject

class RatingDialog @Inject constructor(): DaggerAppCompatDialogFragment() {
    interface RatingDialogListener {
        fun onRated(rate: Int)
        fun onClose()
    }

    private var ratingDialogListener: RatingDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.rating_dialog_frag, null)
        val dialog = AlertDialog.Builder(activity)
                .setView(view)
                .setNegativeButton(R.string.cancel) { _, i ->
                    dismiss()
                    ratingDialogListener?.onClose()
                }
                .create()

        dialog.setOnShowListener { dialogInterface ->
            dialog.findViewById<RatingBar>(R.id.ratingBar).setOnRatingBarChangeListener { ratingBar, rating, isFromUser ->
                if (!isFromUser) {
                    return@setOnRatingBarChangeListener
                }

                dismiss()
                ratingDialogListener?.onRated(rating.toInt())
            }
        }

        return dialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as? RatingDialogListener)?.let {
            this.ratingDialogListener = it
        }
    }

    fun show(fragmentManager: FragmentManager) {
        this.show(fragmentManager, null)
    }
}