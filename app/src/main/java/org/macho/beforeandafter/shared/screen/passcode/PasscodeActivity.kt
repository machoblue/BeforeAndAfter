package org.macho.beforeandafter.shared.screen.passcode

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.passcode_act.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil

class PasscodeActivity: AppCompatActivity(), View.OnClickListener {

    companion object {
        const val length = 4
    }

    var typedPasscode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.passcode_act)

        buttonCancel.visibility = View.INVISIBLE

        button1.setOnClickListener(this)
        button2.setOnClickListener(this)
        button3.setOnClickListener(this)
        button4.setOnClickListener(this)
        button5.setOnClickListener(this)
        button6.setOnClickListener(this)
        button7.setOnClickListener(this)
        button8.setOnClickListener(this)
        button9.setOnClickListener(this)
        button0.setOnClickListener(this)
        buttonCancel.setOnClickListener(this)
        buttonBackspace.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (view == null) {
            return
        }

        if (typedPasscode.length >= 4) {
            return
        }

        when (view) {
            buttonCancel -> {
//                cancel()
                // do nothing
                return
            }
            buttonBackspace -> {
                if (typedPasscode.isEmpty()) {
                    return
                }
                backspace()
                return
            }
            button1 -> input("1")
            button2 -> input("2")
            button3 -> input("3")
            button4 -> input("4")
            button5 -> input("5")
            button6 -> input("6")
            button7 -> input("7")
            button8 -> input("8")
            button9 -> input("9")
            button0 -> input("0")
        }

        if (typedPasscode.length == 4) {
            auth()
        }
    }

    private fun cancel() {
        finish()
    }

    private fun input(num: String) {
        typedPasscode = typedPasscode.plus(num)
        updateText()
    }

    private fun backspace() {
        typedPasscode = typedPasscode.removeRange(typedPasscode.length - 1, typedPasscode.length)
        updateText()
    }

    private fun updateText() {
        this.passcode.text = "${"●".repeat(typedPasscode.length)}${"○".repeat(length - typedPasscode.length)}"
    }

    private fun auth() {
        val passcode = SharedPreferencesUtil.getString(this, SharedPreferencesUtil.Key.PASSCODE)
        if (passcode.equals(typedPasscode)) {
            passcodeMessage.text = getString(R.string.passcode_auth_message_ok)
            setResult(RESULT_OK)
            finish()
        } else {
            passcodeMessage.text = getString(R.string.passcode_auth_message_ng)
            clear()
        }
    }

    private fun clear() {
        typedPasscode = ""
        updateText()
    }

}