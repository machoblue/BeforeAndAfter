package org.macho.beforeandafter

import kotlinx.android.synthetic.main.activity_pin.*
import org.macho.beforeandafter.shared.base.BasePinActivity
import org.macho.beforeandafter.shared.util.LogUtil
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil

open class PinAuthActivity: BasePinActivity() {

    override fun completeInput() {
        auth()
    }

    private fun auth() {
        val pin = SharedPreferencesUtil.getString(this, SharedPreferencesUtil.Key.PIN)
        LogUtil.d(this, "validpin:${pin}")
        if (pin.equals(hiddenEditText.text.toString())) {
            pinMessage.text = getString(R.string.pin_auth_message_ok)
            setResult(RESULT_OK)
            finish()
        } else {
            pinMessage.text = getString(R.string.pin_auth_message_ng)
            clear()
        }
    }
}