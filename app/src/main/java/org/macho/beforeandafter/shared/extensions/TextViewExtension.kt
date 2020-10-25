package org.macho.beforeandafter.shared.extensions

import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat

fun TextView.setTextColor(context: Context, id: Int) {
    setTextColor(ContextCompat.getColor(context, id))
}