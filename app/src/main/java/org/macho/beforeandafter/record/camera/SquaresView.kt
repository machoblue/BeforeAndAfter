package org.macho.beforeandafter.record.camera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup

/**
 * 撮影時のマス目を表示するためのViewです。
 */
class SquaresView(context: Context?) : View(context) {
    var paint: Paint

    init {
        paint = Paint()
        paint.color = Color.argb(100, 200, 200, 200)
        paint.style =  Paint.Style.STROKE
        paint.strokeWidth = 3f
    }

    companion object {
        const val HORIZONTAL_SQUARE_NUM = 5
        const val VERTICAL_SQUARE_NUM = 5
    }

    override fun onDraw(canvas: Canvas) {
        val parent = parent as ViewGroup

        val squareWidth = parent.width / HORIZONTAL_SQUARE_NUM
        val squareHeight = parent.height / VERTICAL_SQUARE_NUM

        for (i in 0 until VERTICAL_SQUARE_NUM) {
            val y = parent.y + (squareHeight * (i + 1))
            canvas.drawLine(parent.x, y, parent.x + parent.width, y, paint)
        }

        for (i in 0 until HORIZONTAL_SQUARE_NUM) {
            val x = parent.x + (squareWidth * (i + 1))
            canvas.drawLine(x, parent.y, x, parent.y + parent.height, paint)
        }
    }
}