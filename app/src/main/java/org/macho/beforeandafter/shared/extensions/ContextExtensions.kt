package org.macho.beforeandafter.shared.extensions

import android.content.Context

fun Context.getBoolean(id: Int): Boolean {
    return resources.getBoolean(id)
}
