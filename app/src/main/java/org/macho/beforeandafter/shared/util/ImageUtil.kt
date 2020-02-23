package org.macho.beforeandafter.shared.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import android.widget.ImageView
import java.io.File

object ImageUtil {

    fun releaseImageView(imageView: ImageView) {
        imageView.setImageDrawable(null)
        imageView.setBackgroundDrawable(null)
    }

    fun releaseBitmap(bitmap: Bitmap) {
        bitmap.recycle()
//        bitmap = null
    }

}