package org.macho.beforeandafter.record.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Date

class PictureSaver(val context: Context, val data: ByteArray): Runnable {
    val outputDir: File = context.applicationContext.filesDir

    companion object {
        const val FILE_NAME_TEMPLATE = "image-%1\$tF-%1\$tH-%1\$tM-%1\$tS-%1\$tL.jpg"
    }

    override fun run() {
        val fileName = FILE_NAME_TEMPLATE.format(Date())
        val outputFile = File(outputDir, fileName)
        BufferedOutputStream(FileOutputStream(outputFile)).use {
            it.write(data)
            val intent = Intent()
            intent.putExtra("PATH", outputFile.toString())
            (context as Activity).setResult(Activity.RESULT_OK, intent)
            context.finish()
        }
    }
}