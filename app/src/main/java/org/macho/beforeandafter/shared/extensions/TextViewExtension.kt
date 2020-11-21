package org.macho.beforeandafter.shared.extensions

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.widget.TextView
import androidx.core.content.ContextCompat

fun TextView.setTextColor(context: Context, id: Int) {
    setTextColor(ContextCompat.getColor(context, id))
}

fun TextView.setText(template: String, arg: String, ratio: Float) {
    val formattedText = String.format(template, arg)
    val argIndex = formattedText.indexOf(arg)
    text = SpannableString(formattedText).also {
        it.setSpan(
                RelativeSizeSpan(1 / ratio),
                argIndex + arg.length,
                formattedText.length,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}