package org.macho.beforeandafter.shared.data.record

import android.content.Context
import java.io.File
import java.util.*

data class Record(var date: Long = Date().time,
             var weight: Float = 0f,
             var rate: Float = 0f,
             var frontImagePath: String? = null,
             var sideImagePath: String? = null,
             var otherImagePath1: String? = null,
             var otherImagePath2: String? = null,
             var otherImagePath3: String? = null,
             var memo: String = "") {

    fun frontImageFile(context: Context): File? {
        return frontImagePath?.let {
            File(context.filesDir, it)
        }
    }

    fun sideImageFile(context: Context): File? {
        return sideImagePath?.let {
            File(context.filesDir, it)
        }
    }

    fun otherImageFile1(context: Context): File? {
        return otherImagePath1?.let {
            File(context.filesDir, it)
        }
    }

    fun otherImageFile2(context: Context): File? {
        return otherImagePath2?.let {
            File(context.filesDir, it)
        }
    }

    fun otherImageFile3(context: Context): File? {
        return otherImagePath3?.let {
            File(context.filesDir, it)
        }
    }
}