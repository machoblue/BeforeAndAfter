package org.macho.beforeandafter.shared.extensions

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import org.macho.beforeandafter.shared.util.LogUtil

fun Fragment.hideKeyboardIfNeeded() {
    val focusedView = activity!!.currentFocus
    if (focusedView != null) {
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(focusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    } else {
        LogUtil.d(this, "currentFocus is null")
    }
}