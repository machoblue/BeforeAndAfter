package org.macho.beforeandafter.graphe2

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.view.View
import android.view.ViewGroup
import java.util.*


class LineGrapheView(context: Context, val fromTime: Long, val toTime: Long, val unitX: Long, val dataList: List<Data>): View(context) {
    companion object {
        const val VIEW_TO_SCREEN_RATIO = 3 // スクロールする幅はスクリーンの3倍
    }

    private var parentHeight = 0

    private lateinit var framePaint: Paint
    private lateinit var linePaints: MutableList<Paint>
    private lateinit var pointPaints: MutableList<Paint>
    private lateinit var textPaint: Paint
    private lateinit var evenColumnPaint: Paint
    private lateinit var saturdayColumnPaint: Paint
    private lateinit var sundayColumnPaint: Paint

    private var leftYAxisMax = 0.0f
    private var leftYAxisMin = 0.0f
    private var rightYAxisMax = 0.0f
    private var rightYAxisMin = 0.0f

    init {
        setUpPaint()
        calculateYMaxAndMin()
    }

    override fun onDraw(canvas: Canvas) {
        parentHeight = (parent as ViewGroup).height

        if (unitX == 1000L * 60 * 60 * 24) {
            drawFrameForDateMode(canvas)
        } else if (unitX == 1000L * 60 * 60 * 24 * 31) {
            drawFrameForMonthMode(canvas)
        }

        drawLines(canvas)
    }

    private fun drawFrameForDateMode(canvas: Canvas) {
        var cal = Calendar.getInstance()
        cal.setTime(Date(fromTime))
        var cal1 = getCalendarYYYYMMdd000000000(cal)
        cal.add(Calendar.DAY_OF_MONTH, 1)
        var cal2 = getCalendarYYYYMMdd000000000(cal)

        val textSize = calculateValidTextSize(textPaint, "99", width.toFloat() / ((toTime.toFloat() - fromTime) / unitX), height * 0.05f)
        textPaint.textSize = textSize
        val textWidth = textPaint.measureText("99")

        var textY = height * 0.05f + height * 0.05f * 0.5f + textSize * 0.5f - 3f // 3fは誤差
        while (cal2.time.time <= toTime) {
            // 列に色をつける
            val x0 = (cal1.time.time - fromTime).toFloat() / (toTime - fromTime) * width
            val x1 = (cal2.time.time - fromTime).toFloat() / (toTime - fromTime) * width
            val y0 = height * 0.05f
            val y1 = height.toFloat()
            val dayOfWeek = cal1.get(Calendar.DAY_OF_WEEK)
            when (dayOfWeek) {
                7 -> { // sat
                    canvas.drawRect(x0, y0, x1, y1, saturdayColumnPaint)
                }
                1 -> { // sun
                    canvas.drawRect(x0, y0, x1, y1, sundayColumnPaint)
                }
                3, 5 -> { // tue, thu
                    canvas.drawRect(x0, y0, x1, y1, evenColumnPaint)
                }
            }

            val middleTime = (cal1.time.time + cal2.time.time) / 2
            val middleX = (middleTime - fromTime).toFloat() / (toTime - fromTime) * width
            val text = cal1.get(Calendar.DAY_OF_MONTH).toString()
            val textX = if (text.length == 1) middleX - (textWidth / 2 / 2) else middleX - (textWidth / 2)
            canvas.drawText(text, textX, textY, textPaint)

            cal1.add(Calendar.DAY_OF_MONTH, 1)
            cal2.add(Calendar.DAY_OF_MONTH, 1)
        }

        // 月を表示
        cal = Calendar.getInstance()
        cal.setTime(Date(fromTime))
        cal1 = getCalendarYYYYMM01000000000(cal)
        cal.add(Calendar.MONTH, 1)
        cal2 = getCalendarYYYYMM01000000000(cal)
        textY = textY - height * 0.05f
        while (cal1.time.time <= toTime) {
            // 列に色をつける
            val x0 = (cal1.time.time - fromTime).toFloat() / (toTime - fromTime) * width
            val x1 = (cal2.time.time - fromTime).toFloat() / (toTime - fromTime) * width
            val y0 = 0f
            val y1 = height * 0.05f
            if (cal1.get(Calendar.MONTH) % 2 == 0) {
                canvas.drawRect(x0, y0, x1, y1, evenColumnPaint)
            }

            // 縦線
            canvas.drawLine(x1, 0f, x1, height.toFloat(), framePaint)

            val middle = (cal1.time.time + cal2.time.time) / 2
            val middleX = (middle - fromTime).toFloat() / (toTime - fromTime) * width
            val text = (cal1.get(Calendar.MONTH) + 1).toString()
            val textX = if (text.length == 1) middleX - (textWidth / 2 / 2) else middleX - (textWidth / 2)
            canvas.drawText(text, textX, textY, textPaint)

            cal1.add(Calendar.MONTH, 1)
            cal2.add(Calendar.MONTH, 1)
        }

        canvas.drawLine(0f, 0f, width.toFloat(), 0f, framePaint)
        canvas.drawLine(0f, (parentHeight * 0.05).toFloat(), width.toFloat(), (parentHeight * 0.05).toFloat(), framePaint)
        canvas.drawLine(0f, (parentHeight * 0.10).toFloat(), width.toFloat(), (parentHeight * 0.10).toFloat(), framePaint)
        canvas.drawLine(0f, (parentHeight * 1.00).toFloat(), width.toFloat(), (parentHeight * 1.00).toFloat(), framePaint)
    }

    private fun getCalendarYYYYMMdd000000000(calendar: Calendar): Calendar {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal
    }

    private fun getCalendarYYYYMM01000000000(calendar: Calendar): Calendar {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 0)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal
    }

    private fun getCalendarYYYY0101000000000(calendar: Calendar): Calendar {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.MONTH, 0)
        cal.set(Calendar.DAY_OF_MONTH, 0)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal
    }

    private fun calculateValidTextSize(paint: Paint, text: String, width: Float, height: Float): Float {
        var textSize = 1.0f
        val tempPaint = Paint(paint)
        tempPaint.textSize = textSize
        var textWidth = tempPaint.measureText(text)
        while (textSize < height && textWidth < width) {
            textSize++
            tempPaint.textSize = textSize
            textWidth = tempPaint.measureText(text)
        }
        return --textSize
    }

    private fun drawFrameForMonthMode(canvas: Canvas) {
        var cal = Calendar.getInstance()
        cal.setTime(Date(fromTime))
        var cal1 = getCalendarYYYYMM01000000000(cal)
        cal.add(Calendar.MONTH, 1)
        var cal2 = getCalendarYYYYMM01000000000(cal)

        val textSize = calculateValidTextSize(textPaint, "99", width / (toTime - fromTime).toFloat() / (1000L * 60 * 60* 24 * 30), height * 0.05f)
        textPaint.textSize = textSize
        val textWidth = textPaint.measureText("99")

        var textY = height * 0.05f + 0.05f * height - (0.05f * height - textSize) / 2 - 5 // -5は誤差
        while (cal2.time.time <= toTime) {
            // 列に色をつける
            if (cal1.get(Calendar.MONTH) % 2 == 1) {
                val x0 = (cal1.time.time - fromTime).toFloat() / (toTime - fromTime) * width
                val x1 = (cal2.time.time - fromTime).toFloat() / (toTime - fromTime) * width
                val y0 = height * 0.05f
                val y1 = height.toFloat()
                canvas.drawRect(x0, y0, x1, y1, evenColumnPaint)
            }

            // 月を表示
            val middleTime = (cal1.time.time + cal2.time.time) / 2
            val middleX = (middleTime - fromTime).toFloat() / (toTime - fromTime) * width
            val text = (cal1.get(Calendar.MONTH) + 1).toString()
            val textX = if (text.length == 1) middleX - (textWidth / 2 / 2) else middleX - (textWidth / 2)
            canvas.drawText(text, textX, textY, textPaint)

            cal1.add(Calendar.MONTH, 1)
            cal2.add(Calendar.MONTH, 1)
        }

        // 年を表示
        cal = Calendar.getInstance()
        cal.setTime(Date(fromTime))
        cal1 = getCalendarYYYY0101000000000(cal)
        cal.add(Calendar.YEAR, 1)
        cal2 = getCalendarYYYY0101000000000(cal)
        textY = 0.05f * height - (0.05f * height - textSize) / 2 - 5 // -5は誤差
        while (cal1.time.time <= toTime) {
            val x0 = (cal1.time.time - fromTime).toFloat() / (toTime - fromTime) * width
            val x1 = (cal2.time.time - fromTime).toFloat() / (toTime - fromTime) * width
            val y0 = 0f
            val y1 = height * 0.05f

            // 列に色をつける
            val year = cal1.get(Calendar.YEAR)
            if (year % 2 == 0) {
                canvas.drawRect(x0, y0, x1, y1, evenColumnPaint)
            }

            // 縦線
            canvas.drawLine(x1, 0f, x1, height.toFloat(), framePaint)

            // 月を表示する
            val middleTime = (cal1.time.time + cal2.time.time) / 2
            val middleX = (middleTime - fromTime).toFloat() / (toTime - fromTime) * width
            val text = (cal1.get(Calendar.YEAR)).toString()
            val textX = if (text.length == 1) middleX - (textWidth / 2 / 2) else middleX - (textWidth / 2)
            canvas.drawText(text, textX, textY, textPaint)

            cal1.add(Calendar.YEAR, 1)
            cal2.add(Calendar.YEAR, 1)
        }

        // 横線
        canvas.drawLine(0f, 0f, width.toFloat(), 0f, framePaint)
        canvas.drawLine(0f, parentHeight * 0.05f, width.toFloat(), parentHeight * 0.05f, framePaint)
        canvas.drawLine(0f, parentHeight * 0.10f, width.toFloat(), parentHeight * 0.10f, framePaint)
    }

    private fun drawLines(canvas: Canvas) {
        for (i in 0 until dataList.size) {
            val data = dataList.get(i)
            if (data.isLeftYAxis) {
                if (data.poin2s == null || data.poin2s.isEmpty()) {
                    continue
                }
                var x1 = 0f
                var y1 = 0f
                var x2 = 0f
                var y2 = 0f
                val point = data.poin2s.get(0)
                x1 = (point.dateTime - fromTime).toFloat() / (toTime - fromTime) * width
                y1 = parentHeight - (point.value - leftYAxisMin) / (leftYAxisMax - leftYAxisMin) * height * 0.9f
                if (!data.isDottedLine) {
                    canvas.drawCircle(x1, y1, 10f, pointPaints.get(i))
                }

                for (j in 1 until data.poin2s.size) {
                    val point = data.poin2s.get(j)
                    x2 = (point.dateTime - fromTime).toFloat() / (toTime - fromTime) * width
                    y2 = parentHeight - (point.value - leftYAxisMin) / (leftYAxisMax - leftYAxisMin) * height * 0.9f
                    if (!data.isDottedLine) {
                        canvas.drawLine(x1, y1, x2, y2, linePaints.get(i))
                        canvas.drawCircle(x2, y2, 10f, pointPaints.get(i))
                    } else {
                        drawDottedLine(canvas, x1, y1, x2, y2, linePaints.get(i), 10f)
                    }
                    x1 = x2
                    y1 = y2
                }
            } else {
                if (data.poin2s == null || data.poin2s.isEmpty()) {
                    continue
                }
                var x1 = 0f
                var y1 = 0f
                var x2 = 0f
                var y2 = 0f
                val point = data.poin2s.get(0)
                x1 = (point.dateTime - fromTime).toFloat() / (toTime - fromTime) * width
                y1 = parentHeight - (point.value - rightYAxisMin) / (rightYAxisMax - rightYAxisMin) * height * 0.9f
                if (!data.isDottedLine) {
                    canvas.drawCircle(x1, y1, 10f, pointPaints.get(i))
                }

                for (j in 1 until data.poin2s.size) {
                    val point = data.poin2s.get(j)
                    x2 = (point.dateTime - fromTime).toFloat() / (toTime - fromTime) * width
                    y2 = parentHeight - (point.value - rightYAxisMin) / (rightYAxisMax - rightYAxisMin) * height * 0.9f
                    if (!data.isDottedLine) {
                        canvas.drawLine(x1, y1, x2, y2, linePaints.get(i))
                        canvas.drawCircle(x2, y2, 10f, pointPaints.get(i))
                    } else {
                        drawDottedLine(canvas, x1, y1, x2, y2, linePaints.get(i), 10f)
                    }
                    x1 = x2
                    y1 = y2
                }

            }
        }
    }

    private fun drawDottedLine(canvas: Canvas, x1: Float, y1: Float, x2: Float, y2: Float, paint: Paint, length: Float) {
        val r = (length / Math.sqrt(Math.pow((x2 - x1).toDouble(), 2.0) + Math.pow((y2 - y1).toDouble(), 2.0))).toFloat()
        var xa = x1
        var ya = y1
        var xb = 0f
        var yb = 0f
        var i = 0
        while ((x2 - x1) * (x2 - xa) >= 0 && (y2 - y1) * (y2 - ya) >= 0) {
            xa = x1 + (x2 - x1) * i * r
            ya = y1 + (y2 - y1) * i * r
            xb = x1 + (x2 - x1) * (i + 1) * r
            yb = y1 + (y2 - y1) * (i + 1) * r
            canvas.drawLine(xa, ya, xb, yb, paint)
            i += 2
        }
    }


    private fun setUpPaint() {
        framePaint = Paint()
        framePaint.isAntiAlias = true
        framePaint.strokeWidth = 3f
        framePaint.color = Color.rgb(128, 128, 128)
        framePaint.style = Paint.Style.STROKE

        linePaints = ArrayList()
        pointPaints = ArrayList()
        for (data in dataList) {
            val paint = Paint()
            paint.isAntiAlias = true
            paint.strokeWidth = 7.5f
            paint.color = data.color
            paint.style = Paint.Style.STROKE
            linePaints.add(paint)

            val pointPaint = Paint()
            pointPaint.isAntiAlias = true
            pointPaint.strokeWidth = 0f
            pointPaint.color = data.color
            pointPaint.style = Paint.Style.FILL
            pointPaints.add(pointPaint)
        }

        textPaint = Paint()
        textPaint.isAntiAlias = true
        textPaint.strokeWidth = 0f
        textPaint.color = Color.rgb(96, 96, 96)
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 30f // 初期値は30

        evenColumnPaint = Paint()
        evenColumnPaint.style = Paint.Style.FILL
        evenColumnPaint.color = Color.rgb(237, 237, 237) // e0e0e0 -> eeeeee

        saturdayColumnPaint = Paint()
        saturdayColumnPaint.style = Paint.Style.FILL
        saturdayColumnPaint.color = Color.rgb(227, 242, 253) // 82b1ff->FCE4EC

        sundayColumnPaint = Paint()
        sundayColumnPaint.style = Paint.Style.FILL
        sundayColumnPaint.color = Color.rgb(252, 228, 236) // ff80ab->E3F2FD
    }

    private fun calculateYMaxAndMin() {
        for (data in dataList) {
            if (data.isLeftYAxis) {
                val initValue = if (data.poin2s.isEmpty()) 0f else data.poin2s.get(0).value
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

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val displaySize = Point()
        (context as Activity).windowManager.defaultDisplay.getSize(displaySize)
        val displayWidth = displaySize.x

        setMeasuredDimension(displayWidth * VIEW_TO_SCREEN_RATIO, heightSize) // TODO:displaywidthを使わない、もっと綺麗な方法がないか調べる。
    }

}