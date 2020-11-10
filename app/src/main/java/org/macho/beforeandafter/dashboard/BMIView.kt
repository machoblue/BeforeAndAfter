package org.macho.beforeandafter.dashboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import org.macho.beforeandafter.R

enum class BMIClass(val labelRes: Int, val colorRes: Int, from: Float, toExclusive: Float) {
    UNDER_WEIGHT(R.string.bmi_class_under_weight, R.color.material_color_blue_a200, 15f, 18.5f),
    NORMAL(R.string.bmi_class_under_weight, R.color.material_color_green_a200, 18.5f, 25f),
    OVER_WEIGHT(R.string.bmi_class_under_weight, R.color.material_color_yellow_a200, 25f, 30f),
    OBESE(R.string.bmi_class_under_weight, R.color.material_color_red_a200, 30f, 45f),
}

class BMIView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        const val progressBarMarginLeft = 36f
        const val progressBarMarginRight = 36f
        const val classLabelFontSize = 24f
    }

    private val classLabelPaint = Paint().also {
        it.color = Color.WHITE
        it.style = Paint.Style.FILL
        it.isAntiAlias = true
        it.textSize = classLabelFontSize
    }

    private val underWeightBarPaint = Paint().also {
        it.color = ContextCompat.getColor(context, BMIClass.UNDER_WEIGHT.colorRes)
        it.style = Paint.Style.FILL
    }

    private val normalBarPaint = Paint().also {
        it.color = ContextCompat.getColor(context, BMIClass.NORMAL.colorRes)
        it.style = Paint.Style.FILL
    }

    private val overWeightBarPaint = Paint().also {
        it.color = ContextCompat.getColor(context, BMIClass.OVER_WEIGHT.colorRes)
        it.style = Paint.Style.FILL
    }

    private val obeseBarPaint = Paint().also {
        it.color = ContextCompat.getColor(context, BMIClass.OBESE.colorRes)
        it.style = Paint.Style.FILL
    }

    private val arrowPaint = Paint().also {
        it.color = Color.RED
        it.style = Paint.Style.FILL
    }

    private var bmi: Float = 0f

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
    }

    fun update(bmi: Float) {
        this.bmi = bmi
        invalidate()
    }
}