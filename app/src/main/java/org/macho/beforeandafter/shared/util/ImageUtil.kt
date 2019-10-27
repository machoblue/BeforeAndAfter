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
    fun setOrientationModifiedImageFile(imageView: ImageView, file: File) {
        if (!file.exists()) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            return
        }

        val exifInterface = androidx.exifinterface.media.ExifInterface(file.path)
        val orientation = exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION)?.toInt()

        if (orientation == null) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            return
        }

        val imageViewWidth = imageView.width.toFloat()
        val imageViewHeight = imageView.height.toFloat()

        imageView.scaleType = ImageView.ScaleType.MATRIX

        val bitmap = BitmapFactory.decodeFile(file.path)
        imageView.setImageBitmap(bitmap)

        val bitmapWidth = bitmap.width.toFloat()
        val bitmapHeight = bitmap.height.toFloat()

        var ratio = 1f
        val matrix = Matrix()

        when(orientation) {
            androidx.exifinterface.media.ExifInterface.ORIENTATION_UNDEFINED,
            androidx.exifinterface.media.ExifInterface.ORIENTATION_FLIP_HORIZONTAL,
            androidx.exifinterface.media.ExifInterface.ORIENTATION_FLIP_VERTICAL,
            androidx.exifinterface.media.ExifInterface.ORIENTATION_TRANSPOSE,
            androidx.exifinterface.media.ExifInterface.ORIENTATION_TRANSVERSE,
            androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL -> {
                // adjust scale
                ratio = Math.max(imageViewWidth / bitmapWidth, imageViewHeight / bitmapHeight)
                matrix.postScale(ratio, ratio)

                // adjust position
                val scaledBitmapWidth = bitmapWidth * ratio
                val scaledBitmapHeight = bitmapHeight * ratio
                val diffX = imageViewWidth - (scaledBitmapWidth + imageViewWidth) / 2
                val diffY = imageViewHeight - (scaledBitmapHeight + imageViewHeight) / 2
                matrix.postTranslate(diffX, diffY)
            }
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> {
                matrix.postRotate(180f, bitmapWidth / 2, bitmapHeight / 2)

                // adjust scale
                ratio = Math.max(imageViewWidth / bitmapWidth, imageViewHeight / bitmapHeight)
                matrix.postScale(ratio, ratio)

                // adjust position
                val scaledBitmapWidth = bitmapWidth * ratio
                val scaledBitmapHeight = bitmapHeight * ratio
                val diffX = imageViewWidth - (scaledBitmapWidth + imageViewWidth) / 2
                val diffY = imageViewHeight - (scaledBitmapHeight + imageViewHeight) / 2
                matrix.postTranslate(diffX, diffY)
            }
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> {
                matrix.postRotate(90f)
                matrix.postTranslate(bitmapHeight, 0f)

                // adjust scale
                ratio = Math.max(imageViewWidth / bitmapHeight, imageViewHeight / bitmapWidth)
                matrix.postScale(ratio, ratio)

                // adjust position
                val scaledBitmapWidth = bitmapWidth * ratio
                val scaledBitmapHeight = bitmapHeight * ratio
                val diffX = imageViewWidth - (scaledBitmapHeight + imageViewWidth) / 2
                val diffY = imageViewHeight - (scaledBitmapWidth + imageViewHeight) / 2
                matrix.postTranslate(diffX, diffY)
            }
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> {
                matrix.postRotate(270f)
                matrix.postTranslate(0f, bitmapWidth)

                // adjust scale
                ratio = Math.max(imageViewWidth / bitmapHeight, imageViewHeight / bitmapWidth)
                matrix.postScale(ratio, ratio)

                // adjust position
                val scaledBitmapWidth = bitmapWidth * ratio
                val scaledBitmapHeight = bitmapHeight * ratio
                val diffX = imageViewWidth - (scaledBitmapHeight + imageViewWidth) / 2
                val diffY = imageViewHeight - (scaledBitmapWidth + imageViewHeight) / 2
                matrix.postTranslate(diffX, diffY)
            }
        }

        imageView.imageMatrix = matrix
        imageView.invalidate()
    }

    fun getOrientationModifiedBitmap(bitmap: Bitmap, file: File): Bitmap {
        val exifInterface = androidx.exifinterface.media.ExifInterface(file.path)
        val orientation = exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION)?.toInt()
        if (orientation == null) {
            return bitmap
        }
        return getOrientationModifiedBitmap(bitmap, orientation)
    }

    fun getOrientationModifiedBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        if(!(orientation == androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90
            || orientation == androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180
            || orientation == androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270)) {
            return bitmap
        }

        var matrix = Matrix()
        val bitmapWidth = bitmap.width.toFloat()
        val bitmapHeight = bitmap.height.toFloat()

        when(orientation) {
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> {
                matrix.postRotate(180f, bitmapWidth / 2, bitmapHeight / 2)
            }
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> {
                matrix.postRotate(90f)
                matrix.postTranslate(bitmapHeight, 0f)
            }
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> {
                matrix.postRotate(280f)
                matrix.postTranslate(0f, bitmapWidth)
            }
        }

        val newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        releaseBitmap(bitmap)
        return newBitmap
    }

    fun extractOrientationFromGalleryImage(context: Context, galleryUri: Uri): Int {
        var path: String? = null
        if ("media".equals(galleryUri.authority)) {
            val projection = arrayOf(MediaStore.MediaColumns.DATA)
            context.contentResolver.query(galleryUri, projection, null, null, null).use {
                if (it.moveToFirst()) {
                    path = it.getString(0)
                }
            }

        } else {
            val contentUri = MediaStore.Files.getContentUri("external")

            val column = "_data"
            val projection = arrayOf(column)

            val selection = "_id=?"

            val docId = DocumentsContract.getDocumentId(galleryUri)
            val split = docId.split(":")
            val selectionArgs = arrayOf(split[1])

            context.contentResolver.query(contentUri, projection, selection, selectionArgs, null).use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(column)
                    path = it.getString(columnIndex)
                }
            }
        }

        if (path == null) {
            return androidx.exifinterface.media.ExifInterface.ORIENTATION_UNDEFINED
        }

        val exifInterface = androidx.exifinterface.media.ExifInterface(path!!)
        return exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION)?.toInt() ?: androidx.exifinterface.media.ExifInterface.ORIENTATION_UNDEFINED
    }

    fun releaseImageView(imageView: ImageView) {
        imageView.setImageDrawable(null)
        imageView.setBackgroundDrawable(null)
    }

    fun releaseBitmap(bitmap: Bitmap) {
        bitmap.recycle()
//        bitmap = null
    }

}