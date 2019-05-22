package org.macho.beforeandafter.graphe2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

/**
 * 折れ線グラフの目盛りを表示するためのViewです。
 */
class ScaleView(context: Context?, val dataList: List<Data>): View(context) {
    companion object {
        const val TEXT_SIZE = 50f
    }

    private var leftYAxisMax = 0.0f
    private var leftYAxisMin = 0.0f
    private var rightYAxisMax = 0.0f
    private var rightYAxisMin = 0.0f

    private lateinit var leftYAxisScalePaint: Paint
    private lateinit var leftYAxisTextPaint: Paint
    private lateinit var rightYAxisScalePaint: Paint
    private lateinit var rightYAxisTextPaint: Paint
    private lateinit var centerLinePaint: Paint

    init {
        setUpPaint()
        calculateYMaxAndMin()
    }

    override fun onDraw(canvas: Canvas?) {
        drawLeftXAxis(canvas)
        drawRightXAxis(canvas)
        drawCenterLine(canvas)
    }

    private fun setUpPaint() {
        rightYAxisScalePaint = Paint()
        rightYAxisScalePaint.isAntiAlias = true
        rightYAxisScalePaint.style = Paint.Style.STROKE
        rightYAxisScalePaint.color = Color.argb(128, 129, 199, 132)
        rightYAxisScalePaint.strokeWidth = 5f

        rightYAxisTextPaint = Paint()
        rightYAxisTextPaint.isAntiAlias = true
        rightYAxisTextPaint.style = Paint.Style.FILL
        rightYAxisTextPaint.color = Color.argb(128, 129, 199, 132)
        rightYAxisTextPaint.textSize = TEXT_SIZE

        leftYAxisScalePaint = Paint()
        leftYAxisScalePaint.isAntiAlias = true
        leftYAxisScalePaint.style = Paint.Style.STROKE
        leftYAxisScalePaint.color = Color.argb(128, 229, 83, 80)
        leftYAxisScalePaint.strokeWidth = 5f

        leftYAxisTextPaint = Paint()
        leftYAxisTextPaint.isAntiAlias = true
        leftYAxisTextPaint.style = Paint.Style.FILL
        leftYAxisTextPaint.color = Color.argb(128, 229, 83, 80)
        leftYAxisTextPaint.textSize = TEXT_SIZE

        centerLinePaint = Paint()
        centerLinePaint.style = Paint.Style.STROKE
        centerLinePaint.color = Color.argb(128, 48, 48, 48)
        centerLinePaint.strokeWidth = 2.5f
    }

    private fun calculateYMaxAndMin() {
        for (data in dataList) {
            if (data.isLeftYAxis) {
                val initValue = if (data.poin2s.isEmpty()) 0f else data.poin2s.get(0).value // 初期値として先頭の値を取得する
                leftYAxisMin = if (leftYAxisMin == 0f) initValue else leftYAxisMin
                leftYAxisMax = if (leftYAxisMax == 0f) initValue else leftYAxisMax
                for (point in data.poin2s) {
                    val value = point.value
                    if (value < leftYAxisMin) {
                        leftYAxisMin = value
                    } else if (leftYAxisMax < value) {
                        leftYAxisMax = value
                    }
                }
            } else {
                val initValue = if (data.poin2s.isEmpty()) 0f else data.poin2s.get(0).value
                rightYAxisMin = if (rightYAxisMin == 0f) initValue else rightYAxisMin
                rightYAxisMax = if (rightYAxisMax == 0f) initValue else rightYAxisMax
                for (point in data.poin2s) {
                    val value = point.value
                    if (value < rightYAxisMin) {
                        rightYAxisMin = value
                    } else if (rightYAxisMax < value) {
                        rightYAxisMax = value
                    }
                }
            }
        }
        leftYAxisMin -= 1
        leftYAxisMax += 1
        rightYAxisMin -= 1
        rightYAxisMax += 1
    }

    private fun drawLeftXAxis(canvas: Canvas?) {
        var value = 0f
        while (value < leftYAxisMin + 1) {
            value ++
        }
        while (value <= leftYAxisMax - 1) {
            val y = (1f - (value - leftYAxisMin) / (leftYAxisMax - leftYAxisMin)) * height * 0.9f + height * 0.1f
            canvas?.drawLine(0f, y, 25f, y, leftYAxisScalePaint)
            canvas?.drawText(value.toString(), 30f, y + TEXT_SIZE / 2 - 5, leftYAxisTextPaint) // -5は調整
            value ++
        }
    }

    private fun drawRightXAxis(canvas: Canvas?) {
        var value = 0f
        while (value < rightYAxisMin + 1) {
            value ++
        }
        while (value <= rightYAxisMax - 1) {
            val y = (1f - (value - rightYAxisMin) / (rightYAxisMax - rightYAxisMin)) * height * 0.9f + height * 0.1f
            canvas?.drawLine((width - 25).toFloat(), y, width.toFloat(), y, rightYAxisScalePaint)
            canvas?.drawText(value.toString(), width - 25 - 5 - rightYAxisTextPaint.measureText(value.toString()), y + TEXT_SIZE / 2 - 5, rightYAxisTextPaint)
            value ++
        }
    }

    private fun drawCenterLine(canvas: Canvas?) {
        val x = width * 0.5f
        canvas?.drawLine(x, 0f, x, height.toFloat(), centerLinePaint)
    }


}