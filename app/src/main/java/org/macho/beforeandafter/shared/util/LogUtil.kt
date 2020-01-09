package org.macho.beforeandafter.shared.util

import android.util.Log

object LogUtil {
    public fun i(obj: Any? = null, message: String) {
        Log.i(createTag(obj), "*** $message")
    }

    public fun w(obj: Any? = null, message: String) {
        Log.w(createTag(obj), "*** $message")
    }

    public fun w(obj: Any? = null, message: String, t: Throwable) {
        Log.w(createTag(obj), "*** $message", t)
    }

    public fun d(obj: Any? = null, message: String) {
        Log.d(createTag(obj), "*** $message")
    }

    public fun e(obj: Any? = null, message: String, t: Throwable) {
        Log.e(createTag(obj), "*** $message", t)
    }

    private fun createTag(obj: Any?): String {
        return obj?.let {
            it::class.java.simpleName
        } ?: let {
            "NO_TAG"
        }
    }
}