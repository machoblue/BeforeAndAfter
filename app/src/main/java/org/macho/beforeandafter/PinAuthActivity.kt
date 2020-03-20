package org.macho.beforeandafter

import android.os.Bundle
import android.view.View.INVISIBLE
import kotlinx.android.synthetic.main.activity_pin.*
import org.macho.beforeandafter.shared.base.BasePinActivity
import org.macho.beforeandafter.shared.util.LogUtil
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil

open class PinAuthActivity: BasePinActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backButton?.visibility = INVISIBLE
    }

    override fun completeInput(text: String) {
        auth(text)
    }

    private fun auth(text: String) {
        val pin = SharedPreferencesUtil.getString(this, SharedPreferencesUtil.Key.PIN)
        LogUtil.d(this, "validPIN:${pin}")
        if (pin.equals(text)) {
            pinMessage.text = getString(R.string.pin_auth_message_ok)
            setResult(RESULT_OK)
            finish()
        } else {
            pinMessage.text = getString(R.string.pin_auth_message_ng)
            clear()
        }
    }

    override fun back() {
        // do nothing
    }
}