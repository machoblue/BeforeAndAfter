package org.macho.beforeandafter.dashboard

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.extensions.drawText
import kotlin.math.max
import kotlin.math.min

enum class BMIClass(val labelRes: Int, val colorRes: Int, val from: Float, val toExclusive: Float) {
    UNDER_WEIGHT(R.string.bmi_class_under_weight, R.color.material_color_blue_a400, 15f, 18.5f),
    NORMAL(R.string.bmi_class_normal, R.color.material_color_green_a400, 18.5f, 25f),
    OVER_WEIGHT(R.string.bmi_class_over_weight, R.color.material_color_yellow_a400, 25f, 30f),
    OBESE(R.string.bmi_class_obese, R.color.material_color_red_a400, 30f, 45f),
}

class BMIView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        const val bmiBarMarginLeft = 36f
        const val bmiBarMarginRight = 36f
        const val classLabelFontSize = 30f
        const val arrowWidth = 36f
        const val arrowHeightWidthRatio = 1.25f
    }

    private val classLabelPaint = TextPaint().also {
        it.color = Color.WHITE
        it.isAntiAlias = true
        it.textSize = classLabelFontSize
    }

    private val arrowFillPaint = Paint().also {
        it.color = Color.RED
        it.isAntiAlias = true
        it.style = Paint.Style.FILL
    }

    private val arrowStrokePaint = Paint().also {
        it.color = Color.WHITE
        it.isAntiAlias = true
        it.style = Paint.Style.STROKE
        it.strokeWidth = 5f
    }

    private var bmi: Float = 0f

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        val bmiBarMinX = bmiBarMarginLeft
        val bmiBarMaxX = width - bmiBarMarginRight
        val bmiBarMinY = height / 3f
        val bmiBarMaxY = height.toFloat()

        val bmiBarWidth = width - (bmiBarMarginLeft + bmiBarMarginRight)

        val bmiBarMinValue = BMIClass.values().first().from
        val bmiBarMaxValue = BMIClass.values().last().toExclusive

        var bmiClassMinX = bmiBarMinX
        for (bmiClass in BMIClass.values()) {
            val bmiClassWidth = bmiBarWidth * ((bmiClass.toExclusive - bmiClass.from) / (bmiBarMaxValue - bmiBarMinValue))
            val bmiClassMaxX = bmiClassMinX + bmiClassWidth
            val paint = Paint().also {
                it.style = Paint.Style.FILL
                it.color = ContextCompat.getColor(context, bmiClass.colorRes)
            }
            canvas?.drawRect(bmiClassMinX, bmiBarMinY, bmiClassMaxX, bmiBarMaxY, paint)

            val text = context.getString(bmiClass.labelRes)
            canvas?.drawText(text, RectF(bmiClassMinX, bmiBarMinY, bmiClassMaxX, bmiBarMaxY), classLabelPaint)

            bmiClassMinX = bmiClassMaxX
        }

        if (bmi <= 0f) {
            return
        }

        val cappedBmi = max(min(bmi, bmiBarMaxValue), bmiBarMinValue)
        val arrowCenterX = bmiBarMinX + bmiBarWidth * ((cappedBmi - bmiBarMinValue) / (bmiBarMaxValue - bmiBarMinValue))
        val arrowMinX = arrowCenterX - (arrowWidth / 2)
        val arrowMaxX = arrowCenterX + (arrowWidth / 2)
        val arrowMinY = 0f
        val arrowMaxY = arrowMinY + arrowWidth * arrowHeightWidthRatio
        val arrowPath = Path().also {
            it.moveTo(arrowMinX, arrowMinY)
            it.lineTo(arrowCenterX, arrowMaxY)
            it.lineTo(arrowMaxX, arrowMinY)
            it.lineTo(arrowMinX, arrowMinY)
        }
        canvas?.drawPath(arrowPath, arrowFillPaint)
        canvas?.drawPath(arrowPath, arrowStrokePaint)
    }

    fun update(bmi: Float) {
        this.bmi = bmi
        invalidate()
    }
}