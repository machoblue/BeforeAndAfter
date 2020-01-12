package org.macho.beforeandafter.preference.pin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_pin.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.screen.pin.PinActivity2
import org.macho.beforeandafter.shared.util.LogUtil
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil

class PinEnableActivity: PinActivity2() {

    private var tempPIN = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pinTitle.text = "暗証番号の登録"
        pinMessage.text = "暗証番号を設定してください。"
    }

    override fun completeInput() {
        if (tempPIN.isEmpty()) {
            tempPIN = hiddenEditText.text.toString()
            clear()
            pinMessage.text = "確認のため、もう一度入力してください。"
        } else {
            val secondInput = hiddenEditText.text.toString()
            if (tempPIN.equals(secondInput)) {
                pinMessage.text = "OK!"
                SharedPreferencesUtil.setBoolean(this, SharedPreferencesUtil.Key.ENABLE_PASSCODE, true)
                SharedPreferencesUtil.setString(this, SharedPreferencesUtil.Key.PASSCODE, secondInput)
                setResult(RESULT_OK)
                finish()

            } else {
                pinMessage.text = "１度目と２度目が一致しません。最初から設定しなおしてください。"
                clear()
                tempPIN = ""
            }

        }
    }

}