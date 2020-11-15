package org.macho.beforeandafter.dashboard.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import org.macho.beforeandafter.R
import kotlin.math.max
import kotlin.math.min

class ProgressView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        const val progressBarMarginLeft = 36f
        const val progressBarMarginRight = 36f
        const val labelFontSize = 36f
    }

    private var progressBarBackgroundPaint = Paint().also {
        it.color = ContextCompat.getColor(context, R.color.ultra_light_gray)
        it.style = Paint.Style.FILL
    }

    private var currentPaint = Paint().also {
        it.color = ContextCompat.getColor(context, R.color.colorPrimaryLight)
        it.style = Paint.Style.FILL
    }

    private var bestPaint = Paint().also {
        it.color = ContextCompat.getColor(context, R.color.light_gray)
        it.style = Paint.Style.FILL
    }

    private var scalePaint = Paint().also {
        it.color = ContextCompat.getColor(context, R.color.white)
        it.strokeWidth = 6f
        it.style = Paint.Style.STROKE
    }

    private val labelPaint = Paint().also {
        it.color = Color.GRAY
        it.style = Paint.Style.FILL
        it.isAntiAlias = true
        it.textSize = labelFontSize
    }

    private var firstValue: Float = 0f
    private var currentValue: Float = 0f
    private var bestValue: Float = 0f
    private var goalValue: Float = 0f

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        val canvas = canvas ?: return

        val progressBarMinX = progressBarMarginLeft
        val progressBarMaxX = width - progressBarMarginRight
        val progressBarMinY = 0f
        val progressBarMaxY = height / 2f

        val progressBarWidth = progressBarMaxX - progressBarMinX

        canvas.drawRect(Rect(progressBarMinX.toInt(), progressBarMinY.toInt(), progressBarMaxX.toInt(), progressBarMaxY.toInt()), progressBarBackgroundPaint)

        if (goalValue != 0f && firstValue != 0f) {
            val bestX = progressBarMinX + progressBarWidth * min(max((bestValue - firstValue) / (goalValue - firstValue + 0.001f), 0f), 1f)
            canvas.drawRect(Rect(progressBarMinX.toInt(), progressBarMinY.toInt(), bestX.toInt(), progressBarMaxY.toInt()), bestPaint)

            val currentX = progressBarMinX + progressBarWidth * min(max((currentValue - firstValue) / (goalValue - firstValue + 0.001f), 0f), 1f)
            canvas.drawRect(Rect(progressBarMinX.toInt(), progressBarMinY.toInt(), currentX.toInt(), progressBarMaxY.toInt()), currentPaint)
        }

        val progressBarWidthQuarter = progressBarWidth / 4
        for (i in 1 until 4) {
            val x = progressBarMinX + progressBarWidthQuarter * i
            canvas.drawLine(x, progressBarMinY, x, progressBarMaxY, scalePaint)
        }

        val textY = progressBarMaxY + labelPaint.textSize
        for (i in 0..4 step(2)) {
            val text = "${25 * i}%"
            val x = progressBarMinX + progressBarWidthQuarter * i - labelPaint.textSize * text.length * 0.33f
            canvas.drawText(text, x, textY, labelPaint)
        }
    }

    fun update(firstValue: Float, currentValue: Float, bestValue: Float, goalValue: Float) {
        this.firstValue = firstValue
        this.currentValue = currentValue
        this.bestValue = bestValue
        this.goalValue = goalValue
        invalidate()
    }
}