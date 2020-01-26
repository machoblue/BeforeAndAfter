package org.macho.beforeandafter.graph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import org.macho.beforeandafter.shared.data.Record
import org.macho.beforeandafter.shared.util.LogUtil
import java.util.*
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

    var unitHeight = 0f
    var maxValues = FloatArray(2)
    var minValues = FloatArray(2)

    var dateFrom = Date()
    var dateTo = Date()
    var unitTime = 0L

    constructor(context: Context): super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
    }

    var records: List<Record> = mutableListOf()

    private val axisPaint = Paint().also {
        it.color = Color.BLACK
        it.strokeWidth = AXIS_WIDTH
//        it.isAntiAlias = false
        it.style = Paint.Style.STROKE
    }

    private val linePaint = Paint().also {
        it.color = Color.GRAY
        it.strokeWidth = LINE_WIDTH
        it.style = Paint.Style.STROKE
    }

    private val yAxisCategory1LabelPaint = Paint().also {
        it.color = Color.GRAY
        it.strokeWidth = LINE_WIDTH
        it.style = Paint.Style.FILL_AND_STROKE
        it.textSize = 40f
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
        drawYAxisLabel(canvas)
        drawVerticalLines(canvas)
    }

    private fun drawAxis(canvas: Canvas) {
        val yAxis1X = oX + AXIS_WIDTH / 2
        canvas.drawLine(yAxis1X, oY, yAxis1X, maxY, axisPaint) // yAxis1
        val yAxis2X = maxX - AXIS_WIDTH / 2
        canvas.drawLine(yAxis2X, oY, yAxis2X, maxY, axisPaint) // yAxis1
        canvas.drawLine(oX, oY, maxX, oY, axisPaint) // xAxis
    }

    private fun drawHorizontalLines(canvas: Canvas) {
        val ranges = FloatArray(2)
        val minFloors = FloatArray(2)
        val maxCeils = FloatArray(2)

        val margin = 1f

        val sortedByCategory1 = records.sortedBy { it.weight }
        minFloors[0] = floor(sortedByCategory1.firstOrNull()?.weight ?: 0f) - margin
        maxCeils[0] = ceil(sortedByCategory1.lastOrNull()?.weight ?: 0f) + margin
        ranges[0] = maxCeils[0] - minFloors[0]

        val sortedByCategory2 = records.sortedBy { it.rate }
        minFloors[1] = floor(sortedByCategory2.firstOrNull()?.rate ?: 0f) - margin
        maxCeils[1] = ceil(sortedByCategory2.lastOrNull()?.rate ?: 0f) + margin
        ranges[1] = maxCeils[1] - minFloors[1]

        val isCategory1Base = ranges[0] > ranges[1]
        val index = if (isCategory1Base) 0 else 1
        unitHeight = (oY - maxY) / ranges[index]
        val from = minFloors[index].toInt()
        val to = maxCeils[index].toInt()
        for (i in (from + 1)..to) {
            val lineY = oY - unitHeight * (i - from)
            canvas.drawLine(oX, lineY, maxX, lineY, linePaint)
        }

        minValues[index] = minFloors[index]
        maxValues[index] = maxCeils[index]

        val index2 = if (isCategory1Base) 1 else 0
        minValues[index2] = minFloors[index2] - ((ranges[index] - ranges[index2]) / 2).toInt()
        maxValues[index2] = minValues[index2] + ranges[index]
    }

    private fun drawYAxisLabel(canvas: Canvas) {
        drawYAxisLabelCategory1(canvas)
        drawYAxisLabelCategory2(canvas)
    }

    private fun drawYAxisLabelCategory1(canvas: Canvas) {
        val from = minValues[0].toInt()
        val to = maxValues[0].toInt()
        for (i in (from + 1)..to) {
            val y = oY - unitHeight * (i - from) + (yAxisCategory1LabelPaint.textSize / 2)
            canvas.drawText(i.toString(), 10f /* offset */, y, yAxisCategory1LabelPaint)
        }
    }

    private fun drawYAxisLabelCategory2(canvas: Canvas) {
        val from = minValues[1].toInt()
        val to = maxValues[1].toInt()
        for (i in (from + 1)..to) {
            val x = maxX - yAxisCategory1LabelPaint.textSize - 20 // offset
            val y = oY - unitHeight * (i - from) + (yAxisCategory1LabelPaint.textSize / 2)
            canvas.drawText(i.toString(), x, y, yAxisCategory1LabelPaint)
        }
    }

    private fun drawVerticalLines(canvas: Canvas) {
        val count = ((dateTo.time - dateFrom.time) / unitTime).toInt()
        val unitWidth = (maxX - oX) / count
        for (i in 1..count) {
            val x = oX + unitWidth * i
            canvas.drawLine(x, oY, x, maxY, linePaint)
        }

    }

}