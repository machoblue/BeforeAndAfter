package org.macho.beforeandafter.dashboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.extensions.drawText

enum class BMIClass(val labelRes: Int, val colorRes: Int, val from: Float, val toExclusive: Float) {
    UNDER_WEIGHT(R.string.bmi_class_under_weight, R.color.material_color_blue_a200, 15f, 18.5f),
    NORMAL(R.string.bmi_class_normal, R.color.material_color_green_a200, 18.5f, 25f),
    OVER_WEIGHT(R.string.bmi_class_over_weight, R.color.material_color_yellow_a200, 25f, 30f),
    OBESE(R.string.bmi_class_obese, R.color.material_color_red_a200, 30f, 45f),
}

class BMIView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        const val bmiBarMarginLeft = 36f
        const val bmiBarMarginRight = 36f
        const val classLabelFontSize = 30f
    }

    private val classLabelPaint = TextPaint().also {
        it.color = Color.WHITE
        it.isAntiAlias = true
        it.isAntiAlias = true
        it.textSize = classLabelFontSize
    }

    private val arrowPaint = Paint().also {
        it.color = Color.RED
        it.style = Paint.Style.FILL
    }

    private var bmi: Float = 24f

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        val bmiBarMinX = bmiBarMarginLeft
        val bmiBarMaxX = width - bmiBarMarginRight
        val bmiBarMinY = 0f
        val bmiBarMaxY = height / 2f

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
            canvas?.drawText(text, bmiClassMinX, bmiBarMinY, bmiClassMaxX - bmiClassMinX, classLabelPaint)

            bmiClassMinX = bmiClassMaxX
        }
    }

    fun update(bmi: Float) {
        this.bmi = bmi
        invalidate()
    }
}