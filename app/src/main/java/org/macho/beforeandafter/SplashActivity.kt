package org.macho.beforeandafter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent


class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}