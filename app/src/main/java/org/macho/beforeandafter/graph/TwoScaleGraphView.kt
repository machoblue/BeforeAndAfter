package org.macho.beforeandafter.graph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import org.macho.beforeandafter.shared.data.Record
import org.macho.beforeandafter.shared.util.LogUtil
import kotlin.math.ceil
import kotlin.math.floor

class TwoScaleGraphView: View {
    companion object {
        const val AXIS_WIDTH = 2f
        const val LINE_WIDTH = 1f
    }

    var oX = 0f
    var oY = 0f
    var maxX = 0f
    var maxY = 0f

    constructor(context: Context): super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
    }

    var records: List<Record> = mutableListOf()

    val axisPaint = Paint().also {
        it.color = Color.BLACK
        it.strokeWidth = AXIS_WIDTH
//        it.isAntiAlias = false
        it.style = Paint.Style.STROKE
    }

    val linePaint = Paint().also {
        it.color = Color.GRAY
        it.strokeWidth = LINE_WIDTH
        it.style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        LogUtil.d(this, "draw")

        if (canvas == null) return

        LogUtil.d(this, "width : ${this.width}")
        LogUtil.d(this, "height: ${this.height}")

        oX = 0f
        oY = height * 0.95f
        maxX = width.toFloat()
        maxY = height * 0.05f

        drawAxis(canvas)
        drawHorizontalLines(canvas)
    }

    private fun drawAxis(canvas: Canvas) {
        val yAxis1X = oX + AXIS_WIDTH / 2
        canvas.drawLine(yAxis1X, oY, yAxis1X, maxY, axisPaint) // yAxis1
        val yAxis2X = maxX - AXIS_WIDTH / 2
        canvas.drawLine(yAxis2X, oY, yAxis2X, maxY, axisPaint) // yAxis1
        canvas.drawLine(oX, oY, maxX, oY, axisPaint) // xAxis
    }

    private fun drawHorizontalLines(canvas: Canvas) {
        val sortedByWeight = records.sortedBy { it.weight }
        val weightMin = sortedByWeight.firstOrNull()?.weight ?: 0f
        val weightMax = sortedByWeight.lastOrNull()?.weight ?: 0f
        val sortedByRate = records.sortedBy { it.rate }
        val rateMin = sortedByRate.lastOrNull()?.rate ?: 0f
        val rateMax = sortedByRate.lastOrNull()?.rate ?: 0f

        val weightMinFloor = floor(weightMin)
        val weightMaxCeil = ceil(weightMax)
        val weightRange = weightMaxCeil - weightMinFloor
        val rateMinFloor = floor(rateMin)
        val rateMaxCeil = ceil(rateMax)
        val rateRange = rateMaxCeil - rateMinFloor

        if (weightRange > rateRange) {
            val unitHeight = (oY - maxY) / weightRange
            for (i in (weightMinFloor.toInt() + 1)..weightMaxCeil.toInt()) {
                val lineY = oY - unitHeight * (i - weightMinFloor.toInt())
//                canvas.drawLine(oX, maxX, lineY, lineY, linePaint)
                canvas.drawLine(oX, lineY, maxX, lineY, linePaint)
            }

        } else {

        }
    }
}