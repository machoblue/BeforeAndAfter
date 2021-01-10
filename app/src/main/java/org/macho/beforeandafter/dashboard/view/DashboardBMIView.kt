package org.macho.beforeandafter.dashboard.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.dashboard_bmi_view.view.*
import org.macho.beforeandafter.R
import kotlin.math.roundToInt

class DashboardBMIView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr)  {
    init {
        LayoutInflater.from(context).inflate(R.layout.dashboard_bmi_view, this, true)
    }

    fun update(showSetHeightButton: Boolean, bmi: Float?, bmiClass: String?, idealWeight: Float?, weightUnit: String, onSetHeightButtonClicked: () -> Unit) {
        setHeightButton.visibility = if (showSetHeightButton) View.VISIBLE else View.GONE
        bmiTextView.text = bmi?.let {
            val roundedValue =  (it * 10).roundToInt() / 10f
            String.format("%.1f", roundedValue)
        } ?: "--.-"
        bmiView.update(bmi ?: 0f)
        bmiClassTextView.text = String.format("( %s )", bmiClass ?: "--")
        val idealWeightString = idealWeight?.let {
            val roundedValue = (it * 10).roundToInt() / 10f
            String.format("%.1f", roundedValue)
        } ?: "--.--"
        idealWeightTextView.text = "$idealWeightString $weightUnit"
        setHeightButton.setOnClickListener {
            onSetHeightButtonClicked()
        }
    }
}