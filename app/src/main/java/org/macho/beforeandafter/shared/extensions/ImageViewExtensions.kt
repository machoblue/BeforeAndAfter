package org.macho.beforeandafter.shared.extensions

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.widget.ImageView
import androidx.fragment.app.Fragment
import org.macho.beforeandafter.shared.GlideApp

fun ImageView.loadImage(fragment: Fragment, uri: Uri) {
    GlideApp.with(fragment)
            .load(uri)
            .sizeMultiplier(.4f)
            .thumbnail(.1f)
            .error(ColorDrawable(Color.LTGRAY))
            .into(this)
}