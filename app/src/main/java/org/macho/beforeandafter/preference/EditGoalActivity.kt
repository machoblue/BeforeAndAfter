package org.macho.beforeandafter.preference

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import org.macho.beforeandafter.AdUtil
import org.macho.beforeandafter.R

/**
 * 目標体重・目標体脂肪率を編集する画面です。
 */
class EditGoalActivity: AppCompatActivity() {
    private lateinit var goalWeight: EditText
    private lateinit var goalRate: EditText
    private lateinit var save: Button
    private lateinit var cancel: Button

    private lateinit var adView: AdView

    private lateinit var interstitialAd: InterstitialAd


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_goal)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        goalWeight = findViewById(R.id.goal_weight)
        goalWeight.setText(preferences.getFloat("GOAL_WEIGHT", 50f).toString())

        goalRate = findViewById(R.id.goal_rate)
        goalRate.setText(preferences.getFloat("GOAL_RATE", 20f).toString())

        save = findViewById(R.id.save)
        save.setOnClickListener { view ->
            preferences.edit().putFloat("GOAL_WEIGHT", goalWeight.text.toString().toFloat()).commit()
            preferences.edit().putFloat("GOAL_RATE", goalRate.text.toString().toFloat()).commit()

            AdUtil.show(interstitialAd)

            finish()
        }

        cancel = findViewById(R.id.cancel)
        cancel.setOnClickListener { view ->
            AdUtil.show(interstitialAd)

            finish()
        }

        MobileAds.initialize(this, getString(R.string.admob_app_id))

        adView = findViewById(R.id.adView)
        AdUtil.loadBannerAd(adView, applicationContext)

        interstitialAd = InterstitialAd(this)
        AdUtil.loadInterstitialAd(interstitialAd, applicationContext)
    }
}