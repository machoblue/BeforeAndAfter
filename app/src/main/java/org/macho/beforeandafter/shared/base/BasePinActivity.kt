package org.macho.beforeandafter.shared.base

import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_pin.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.extensions.addTextChangedListener
import org.macho.beforeandafter.shared.util.LogUtil

abstract class BasePinActivity: AppCompatActivity() {
    companion object {
        const val length = 4
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        hiddenEditText.requestFocus()
//        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.showSoftInput(hiddenEditText, InputMethodManager.SHOW_IMPLICIT)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        hiddenEditText.addTextChangedListener { newText ->
            LogUtil.d(this, "on:${newText}")
            updateText()

            if (newText?.length == 4) {
                completeInput()
            }
        }
    }

    private fun updateText() {
        val filledText = "●".repeat(hiddenEditText.text.length)
        val emptyText = "○".repeat(length - hiddenEditText.text.length)
        maskedPin.text = "${filledText}${emptyText}"
    }

    abstract fun completeInput()

    fun clear() {
        hiddenEditText.setText("", TextView.BufferType.NORMAL)
        updateText()
    }
}