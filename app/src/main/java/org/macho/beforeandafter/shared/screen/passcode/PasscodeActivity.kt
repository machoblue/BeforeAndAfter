package org.macho.beforeandafter.shared.screen.passcode

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.passcode_act.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.util.LogUtil

class PasscodeActivity: AppCompatActivity(), View.OnClickListener {

    var passcode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.passcode_act)
        button1.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (view == null) {
            return
        }
        when (view) {
            button1 -> {
                // TODO: remove
                LogUtil.d(this, "button1")
                setResult(RESULT_OK)
                finish()
            }
        }
    }
}