package org.macho.beforeandafter.dashboard.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.dashboard_progress_view.view.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.extensions.setText
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

interface DashboardProgressViewListener {
    fun onSetGoalButtonClicked()
    fun onElapsedDayHelpButtonClicked()
    fun onAchieveExpectHelpButtonClicked()
}

class DashboardProgressView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.dashboard_progress_view, this, true)
    }

    fun update(elapsedDay: Int, firstWeight: Float?, bestWeight: Float?, latestWeight: Float?, goalWeight: Float?, showSetGoalButton: Boolean, listener: DashboardProgressViewListener) {
        elapsedDayTextView.text = String.format(context.getString(R.string.progress_day_template), elapsedDay)
        val firstWeight = firstWeight ?: 0f
        val bestWeight = bestWeight ?: 0f
        val latestWeight = latestWeight ?: 0f
        val goalWeight = goalWeight ?: 0f

        val progressInPercent = if (goalWeight == 0f) "--" else max(0f, min(100f, ((latestWeight - firstWeight) / (goalWeight - firstWeight + 0.001f) * 100))).toInt().toString()
        progressTextView.setText(context.getString(R.string.progress_template), progressInPercent, 1.5f)

        val isRecordCountOneOrIsWorseThanFirst = (goalWeight - firstWeight) * (latestWeight - firstWeight) <= 0
        val achieveExpectDays = if (goalWeight == 0f || isRecordCountOneOrIsWorseThanFirst) "--" else ceil(elapsedDay * ((goalWeight - latestWeight) / (latestWeight - firstWeight + 0.001))).toInt().toString()
        weightAchieveExpectTextView.text = String.format(context.getString(R.string.progress_achieve_expect), achieveExpectDays)

        progressView.update(firstWeight ?: 0.0f, latestWeight ?: 0.0f, bestWeight ?: 0.0f, goalWeight ?: 0.0f)

        setGoalButton.visibility = if (showSetGoalButton) View.VISIBLE else View.GONE
        setGoalButton.setOnClickListener {
            listener.onSetGoalButtonClicked()
        }

        elapsedDayHelpButton.setOnClickListener {
            listener.onElapsedDayHelpButtonClicked()
        }

        weightArchiveExpectHelpButton.setOnClickListener {
            listener.onAchieveExpectHelpButtonClicked()
        }
    }
}