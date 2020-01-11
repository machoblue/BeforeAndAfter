package org.macho.beforeandafter.shared.screen.pin

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_pin.*
import kotlinx.android.synthetic.main.passcode_act.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.screen.passcode.PasscodeActivity.Companion.length
import org.macho.beforeandafter.shared.util.LogUtil
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil

class PinActivity2: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        hiddenEditText.requestFocus()
//        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.showSoftInput(hiddenEditText, InputMethodManager.SHOW_IMPLICIT)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        hiddenEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                LogUtil.d(this, "on:${s}")
                updateText()

                if (s?.length == 4) {
                    auth()
                }
            }
        })
    }


//    private fun input(num: String) {
//        typedPasscode = typedPasscode.plus(num)
//        updateText()
//    }

//    private fun backspace() {
//        typedPasscode = typedPasscode.removeRange(typedPasscode.length - 1, typedPasscode.length)
//        updateText()
//    }

    private fun updateText() {
        maskedPin.text = "${"●".repeat(hiddenEditText.text.length)}${"○".repeat(length - hiddenEditText.text.length)}"
    }

    private fun auth() {
        val passcode = SharedPreferencesUtil.getString(this, SharedPreferencesUtil.Key.PASSCODE)
        LogUtil.d(this, "validpin:${passcode}")
        if (passcode.equals(hiddenEditText.text.toString())) {
            pinMessage.text = getString(R.string.passcode_auth_message_ok)
            setResult(RESULT_OK)
            finish()
        } else {
            pinMessage.text = getString(R.string.passcode_auth_message_ng)
            clear()
        }
    }

    private fun clear() {
//        hiddenEditText.text = ""
        hiddenEditText.setText("", TextView.BufferType.NORMAL)
        updateText()
    }
}