package org.macho.beforeandafter.shared.view.commondialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import dagger.android.support.DaggerAppCompatDialogFragment
import org.macho.beforeandafter.R
import javax.inject.Inject

class CommonDialog @Inject constructor(): DaggerAppCompatDialogFragment() {
    interface CommonDialogListener {
        fun onPositiveButtonClick(requestCode: Int)
        fun onNegativeButtonClick(requestCode: Int)
    }

    companion object {
        const val REQUEST_CODE = "REQUEST_CODE"
        const val MESSAGE = "MESSAGE"
        const val POSITIVE_BUTTON_TITLE = "POSITIVE_BUTTON_TITLE"
        const val NEGATIVE_BUTTON_TITLE = "NEGATIVE_BUTTON_TITLE"
    }

    private var commonDialogListener: CommonDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val arguments = arguments ?: throw RuntimeException("arguments shouldn't be null.")
        val requestCode = arguments.getInt(REQUEST_CODE)
        val builder = AlertDialog.Builder(activity)
                .setMessage(arguments.getString(MESSAGE))
                .setPositiveButton(arguments.getString(POSITIVE_BUTTON_TITLE)) { _, i ->
                    dismiss()
                    commonDialogListener?.onPositiveButtonClick(requestCode)
                }

        arguments.getString(NEGATIVE_BUTTON_TITLE)?.let {
            builder.setNegativeButton(it) { _, i ->
                dismiss()
                commonDialogListener?.onNegativeButtonClick(requestCode)
            }
        }

        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Activityのcallbackを呼ぶ場合。
        // Fragmentにcallbackしたい場合、別のDialogFragmentを用意して、setFragmentResultで結果を返した方がいい。
        // targetFragmentはDeprecatedになったため、targetFragmentを利用した、呼び出し元Fragmentへの値渡しはできない。
        (context as? CommonDialogListener)?.let {
            this.commonDialogListener = it
        }
    }

    fun show(fragmentManager: FragmentManager, requestCode: Int, message: String, positiveButtonTitle: String? = null, negativeButtonTitle: String? = null) {
        this.arguments = Bundle().also {
            it.putInt(REQUEST_CODE, requestCode)
            it.putString(MESSAGE, message ?: "")
            it.putString(POSITIVE_BUTTON_TITLE, positiveButtonTitle)
            if (negativeButtonTitle != null) {
                it.putString(NEGATIVE_BUTTON_TITLE, negativeButtonTitle)
            }
        }
        this.show(fragmentManager, null)
    }
}