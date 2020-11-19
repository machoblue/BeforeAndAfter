package org.macho.beforeandafter.dashboard.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.dashboard_bmi_view.view.*
import org.macho.beforeandafter.R

class DashboardBMIView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr)  {
    init {
        LayoutInflater.from(context).inflate(R.layout.dashboard_bmi_view, this, true)
    }

    fun update(showSetHeightButton: Boolean, bmi: Float?, bmiClass: String?, idealWeight: Float?, onSetHeightButtonClicked: () -> Unit) {
        setHeightButton.visibility = if (showSetHeightButton) View.VISIBLE else View.GONE
        bmiTextView.text = bmi?.let { String.format("%.1f", it) } ?: "--.-"
        bmiView.update(bmi ?: 0f)
        bmiClassTextView.text = String.format("( %s )", bmiClass ?: "--")
        val idealWeightString = idealWeight?.let { String.format("%.1f", it) } ?: "--.--"
        idealWeightTextView.text = String.format("%s kg", idealWeightString)
        setHeightButton.setOnClickListener {
            onSetHeightButtonClicked()
        }
    }
}