package org.macho.beforeandafter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import org.macho.beforeandafter.shared.screen.passcode.PasscodeActivity
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil


class SplashActivity: AppCompatActivity() {

    companion object {
        const val PASSCODE_RC = 3001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val showPasscode = true // SharedPreferencesUtil.getBoolean(this, SharedPreferencesUtil.Key.ENABLE_PASSCODE)

        if (showPasscode) {
            val intent = Intent(this, PasscodeActivity::class.java)
            startActivityForResult(intent, PASSCODE_RC)

        } else {
            showHome()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) {
            return
        }

        if (requestCode == PASSCODE_RC) {
            showHome()
        }
    }

    private fun showHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}