package org.macho.beforeandafter.preference.pin

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_pin.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.base.BasePinActivity
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil

class PinEnableActivity: BasePinActivity() {

    private var tempPIN = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pinTitle.text = getString(R.string.pin_enable_title)
        pinMessage.text = getString(R.string.pin_enable_message)
    }

    override fun completeInput(text: String) {
        enablePINIfNeeded(text)
    }

    private fun enablePINIfNeeded(text: String) {
        if (tempPIN.isEmpty()) {
            tempPIN = text
            clear()
            pinMessage.text = getString(R.string.pin_enable_message_confirm)
        } else {
            val secondInput = text
            if (tempPIN.equals(secondInput)) {
                pinMessage.text = getString(R.string.pin_enable_message_ok)
                SharedPreferencesUtil.setString(this, SharedPreferencesUtil.Key.PIN, secondInput)
                setResult(RESULT_OK)
                finish()

            } else {
                pinMessage.text = getString(R.string.pin_enable_message_error)
                clear()
                tempPIN = ""
            }
        }
    }

}