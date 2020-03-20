package org.macho.beforeandafter.shared.base

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_pin.*
import android.view.Gravity
import android.widget.GridLayout
import android.util.DisplayMetrics
import androidx.core.content.ContextCompat
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.util.LogUtil
import kotlin.math.min

abstract class BasePinActivity: AppCompatActivity() {
    companion object {
        const val length = 4
    }

    var text: String = ""
    var backButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        val dMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dMetrics)

        val screenWidth = dMetrics.widthPixels
        val screenHeight = dMetrics.heightPixels

        val statusBarHeight = 50
        val buttonWidthRatio = 1f
        val buttonMarginRatio = 0.5f
        val gridLayoutWeight = 0.7f
        val buttonMaxWidth = 300f
        val buttonWidth = min(
                            buttonMaxWidth,
                            min(
                            screenWidth / (buttonMarginRatio * 4 + buttonWidthRatio * 3),
                            ((screenHeight - statusBarHeight) * gridLayoutWeight) / (buttonMarginRatio * 5 + buttonWidthRatio * 4)))
        val spaceWidth = buttonWidth * (buttonMarginRatio / buttonWidthRatio)

        for (i in 1..12) {
            val button = Button(this).also {
                it.text = i.toString()
                it.text = when(i) {
                    in 1..9 -> i.toString()
                    10 -> "←"
                    11 -> "0"
                    12 -> "BS"
                    else -> null
                }
                it.layoutParams = GridLayout.LayoutParams().also { param ->
                    param.width = buttonWidth.toInt()
                    param.height = buttonWidth.toInt()
                    param.rightMargin = (spaceWidth / 2).toInt()
                    param.topMargin = (spaceWidth / 2).toInt()
                    param.leftMargin = (spaceWidth / 2).toInt()
                    param.bottomMargin = (spaceWidth / 2).toInt()
                    param.setGravity(Gravity.CENTER)
                    val rowIndex = (i - 1) / 3
                    param.rowSpec = GridLayout.spec(rowIndex)
                    val columnIndex = (i - 1) % 3
                    param.columnSpec = GridLayout.spec(columnIndex)
                }
                it.background = ContextCompat.getDrawable(this, R.drawable.circle_button)
                it.tag = i
                it.setOnClickListener { view ->
                    LogUtil.i(this, view.tag.toString())
                    when(val tag = view.tag as Int) {
                        in 1..9 -> {
                            if (text.length >= 4) return@setOnClickListener
                            text += tag.toString()
                        }
                        10 -> {
                            back()
                            return@setOnClickListener
                        }
                        11 -> {
                            if (text.length >= 4) return@setOnClickListener
                            text += "0"
                        }
                        12 -> {
                            if (text.length == 0) return@setOnClickListener
                            text = text.substring(0, text.length - 1)
                        }
                    }

                    updateText(text)
                    if (text.length == 4) {
                        completeInput(text)
                    }
                }
            }
            numberButtons.addView(button)
            if (i == 10) {
                this.backButton = button
            }

            val padding = (spaceWidth / 2).toInt()
            numberButtons.setPadding(padding, 0, padding, padding * 2)
        }
    }

    private fun updateText(text: String) {
        val filledText = "● ".repeat(text.length)
        val emptyText = "○ ".repeat(length - text.length)
        maskedPin.text = "${filledText}${emptyText}"
    }

    abstract fun completeInput(text: String)

    abstract fun back()

    fun clear() {
        text = ""
        updateText("")
    }
}