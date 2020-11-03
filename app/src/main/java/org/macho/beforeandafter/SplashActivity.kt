package org.macho.beforeandafter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import org.macho.beforeandafter.main.MainActivity
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil


class SplashActivity: AppCompatActivity() {

    companion object {
        const val PIN_RC = 3001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val skipPin = SharedPreferencesUtil.getString(this, SharedPreferencesUtil.Key.PIN).isEmpty()

        if (skipPin) {
            showHome()

        } else {
            showPIN()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) {
            return
        }

        if (requestCode == PIN_RC) {
            showHome()
        }
    }

    private fun showHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showPIN() {
        val intent = Intent(this, PinAuthActivity::class.java)
        startActivityForResult(intent, PIN_RC)
    }

}