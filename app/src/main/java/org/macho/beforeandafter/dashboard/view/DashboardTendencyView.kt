package org.macho.beforeandafter.dashboard.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.dashboard_tendency_view.view.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.extensions.setText

class DashboardTendencyView  @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.dashboard_tendency_view, this, true)
    }

    interface DashboardTendencyViewListener {
        fun onOneWeekTendencyHelpButtonClicked()
        fun onThirtyDaysTendencyHelpButtonClicked()
        fun onOneYearTendencyHelpButtonClicked()
    }

    fun update(title: String, unit: String, oneWeekTendency: Float?, thirtyDaysTendency: Float?, oneYearTendency: Float?, listener: DashboardTendencyViewListener) {
        tendencyTitle.text = title

        updateValue(unit, oneWeekTendency, oneWeekTendencyTextView, oneWeekTendencyImageView)
        updateValue(unit, thirtyDaysTendency, thirtyDaysTendencyTextView, thirtyDaysTendencyImageView)
        updateValue(unit, oneYearTendency, oneYearTendencyTextView, oneYearTendencyImageView)

        oneWeekTendencyHelpButton.setOnClickListener {
            listener.onOneWeekTendencyHelpButtonClicked()
        }

        thirtyDaysTendencyHelpButton.setOnClickListener {
            listener.onThirtyDaysTendencyHelpButtonClicked()
        }

        oneYearTendencyHelpButton.setOnClickListener {
            listener.onOneYearTendencyHelpButtonClicked()
        }

    }

    private fun updateValue(unit: String, tendency: Float?, textView: TextView, imageView: ImageView) {
        val template = "%s $unit"
        textView.setText(template, tendency?.let { (if (it > 0) "+ " else "") + String.format("%.2f", tendency) } ?: "--.--", 1.5f)

        val tintColorId: Int = tendency?.let {
            return@let when {
                it < 0f -> R.color.colorAccent
                it < 0.5 -> R.color.colorGrayText
                else -> R.color.colorPrimary
            }
        } ?: let {
            return@let R.color.colorGrayText
        }
        val tintColor = ContextCompat.getColor(context, tintColorId)
        textView.setTextColor(tintColor)
        imageView.setColorFilter(tintColor)

        val iconDrawableId: Int = tendency?.let {
            return@let when {
                it < 0f -> R.drawable.ic_trending_down_white_18dp
                it < 0.5 -> R.drawable.ic_trending_flat_white_18dp
                else -> R.drawable.ic_trending_up_white_18dp
            }
        } ?: let {
            return@let R.drawable.ic_remove_white_18dp
        }
        imageView.setImageDrawable(context.getDrawable(iconDrawableId))
    }
}