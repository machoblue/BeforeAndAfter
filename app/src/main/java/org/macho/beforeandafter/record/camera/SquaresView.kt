package org.macho.beforeandafter.record.camera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

/**
 * 撮影時のマス目を表示するためのViewです。
 */
class SquaresView : View {
    companion object {
        const val HORIZONTAL_SQUARE_NUM = 5
        const val VERTICAL_SQUARE_NUM = 5
    }

    var paint: Paint = Paint().also {
        it.color = Color.argb(100, 200, 200, 200)
        it.style =  Paint.Style.STROKE
        it.strokeWidth = 3f
    }

    constructor(context: Context): super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
    }

    override fun onDraw(canvas: Canvas) {
        val squareWidth = width / HORIZONTAL_SQUARE_NUM
        val squareHeight = height / VERTICAL_SQUARE_NUM

        for (i in 0 until VERTICAL_SQUARE_NUM) {
            val y = y + (squareHeight * (i + 1))
            canvas.drawLine(x, y, x + width, y, paint)
        }

        for (i in 0 until HORIZONTAL_SQUARE_NUM) {
            val x = x + (squareWidth * (i + 1))
            canvas.drawLine(x, y, x, y + height, paint)
        }
    }
}