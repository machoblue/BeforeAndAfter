package org.macho.beforeandafter.shared.view.commondialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject

/**
 * Fragmentから開く時はこちらを使います。
 */
class CommonDialog2 @Inject constructor(): DaggerAppCompatDialogFragment()  {

    enum class ButtonType {
        POSITIVE,
        NEGATIVE
    }

    companion object {
        const val MESSAGE = "MESSAGE"
        const val POSITIVE_BUTTON_TITLE = "POSITIVE_BUTTON_TITLE"
        const val NEGATIVE_BUTTON_TITLE = "NEGATIVE_BUTTON_TITLE"

        const val REQUEST_KEY_COMMON_DIALOG2 = "REQUEST_KEY_COMMON_DIALOG2"
        const val BUNDLE_KEY_BUTTON_TYPE = "BUNDLE_KEY_BUTTON_TYPE"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val arguments = arguments ?: throw RuntimeException("arguments shouldn't be null.")
        val builder = AlertDialog.Builder(activity)
                .setMessage(arguments.getString(MESSAGE))
                .setPositiveButton(arguments.getString(POSITIVE_BUTTON_TITLE)) { _, i ->
                    dismiss()
                    setFragmentResult(REQUEST_KEY_COMMON_DIALOG2, bundleOf(BUNDLE_KEY_BUTTON_TYPE to ButtonType.POSITIVE))
                }

        arguments.getString(NEGATIVE_BUTTON_TITLE)?.let {
            builder.setNegativeButton(it) { _, i ->
                dismiss()
                setFragmentResult(REQUEST_KEY_COMMON_DIALOG2, bundleOf(BUNDLE_KEY_BUTTON_TYPE to ButtonType.NEGATIVE))
            }
        }

        return builder.create()
    }

    fun show(fragmentManager: FragmentManager, message: String, positiveButtonTitle: String? = null, negativeButtonTitle: String? = null) {
        this.arguments = Bundle().also {
            it.putString(MESSAGE, message ?: "")
            it.putString(POSITIVE_BUTTON_TITLE, positiveButtonTitle)
            if (negativeButtonTitle != null) {
                it.putString(NEGATIVE_BUTTON_TITLE, negativeButtonTitle)
            }
        }
        this.show(fragmentManager, null)
    }
}