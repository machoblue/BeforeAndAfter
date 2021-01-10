package org.macho.beforeandafter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import org.macho.beforeandafter.main.MainActivity
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import java.util.*


class SplashActivity: AppCompatActivity() {

    companion object {
        const val PIN_RC = 3001
        const val INITIAL_SETTINGS_RC = 3002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isInitialSettingsComplete = SharedPreferencesUtil.getBoolean(this, SharedPreferencesUtil.Key.IS_INITIAL_SETTINGS_COMPLETE)
        val isInJapan = Locale.getDefault() == Locale.JAPAN
        val isFirstLaunch  = SharedPreferencesUtil.getFloat(this, SharedPreferencesUtil.Key.LATEST_WEIGHT) == 0f
        if (!isInitialSettingsComplete && !isInJapan && isFirstLaunch) {
            showInitialSettings()
            return
        }

        SharedPreferencesUtil.setBoolean(this, SharedPreferencesUtil.Key.IS_INITIAL_SETTINGS_COMPLETE, true)

        val showPin = SharedPreferencesUtil.getString(this, SharedPreferencesUtil.Key.PIN).isNotEmpty()
        if (showPin) {
            showPIN()
            return
        }

        showHome()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) {
            return
        }

        when (requestCode) {
            PIN_RC -> {
                showHome()
            }

            INITIAL_SETTINGS_RC -> {
                SharedPreferencesUtil.setBoolean(this, SharedPreferencesUtil.Key.IS_INITIAL_SETTINGS_COMPLETE, true)
                showHome()
            }

            else -> {}
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

    private fun showInitialSettings() {
        Intent(this, InitialSettingsActivity::class.java).also {
            startActivityForResult(it, INITIAL_SETTINGS_RC)
        }
    }
}