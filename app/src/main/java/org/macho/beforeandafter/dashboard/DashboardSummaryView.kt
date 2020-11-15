package org.macho.beforeandafter.dashboard

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.extensions.setText

class DashboardSummaryView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr)  {
    // FrameLayout > ConstraintLayout > ...になる。
    // ConstraintLayoutだけにしたいが、layout.xmlでcustom viewを実現しようとすると、どうしてもViewGroupが二重になってしまう
    // 最も軽いFrameLayoutにした。
    init {
        LayoutInflater.from(context).inflate(R.layout.dashboard_summary_view, this, true)
    }

    fun update(latestValue: Float?, firstValue: Float?, bestValue: Float?, goalValue: Float?, showSetGoalButton: Boolean, onSetGoalButtonClick: (() -> Unit)? = null) {
        val blankText = "--.--"
        val weightTemplate = "%s kg"
        findViewById<TextView>(R.id.currentWeightTextView).setText(weightTemplate, latestValue?.toString() ?: blankText, 1.5f)
        findViewById<TextView>(R.id.firstWeightTextView).setText(weightTemplate, firstValue?.toString() ?: blankText, 1.5f)
        findViewById<TextView>(R.id.bestWeightTextView).setText(weightTemplate, bestValue?.toString() ?: blankText, 1.5f)
        findViewById<TextView>(R.id.goalWeightTextView).setText(weightTemplate, goalValue?.toString() ?: blankText, 1.5f)
        val setGoalButton = findViewById<Button>(R.id.setGoalButton)
        setGoalButton.visibility = if (showSetGoalButton) View.VISIBLE else View.INVISIBLE
        setGoalButton.setOnClickListener {
            onSetGoalButtonClick?.invoke()
        }
    }
}

