package org.macho.beforeandafter.preference

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_edit_goal.*
import org.macho.beforeandafter.shared.AdUtil
import org.macho.beforeandafter.R

/**
 * 目標体重・目標体脂肪率を編集する画面です。
 */
class EditGoalActivity: AppCompatActivity() {

    private lateinit var interstitialAd: InterstitialAd


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_goal)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        goalWeight.setText(preferences.getFloat("GOAL_WEIGHT", 50f).toString())

        goalRate.setText(preferences.getFloat("GOAL_RATE", 20f).toString())

        save.setOnClickListener { view ->
            preferences.edit().putFloat("GOAL_WEIGHT", goalWeight.text.toString().toFloat()).commit()
            preferences.edit().putFloat("GOAL_RATE", goalRate.text.toString().toFloat()).commit()

            AdUtil.show(interstitialAd)

            finish()
        }

        cancel.setOnClickListener { view ->
            AdUtil.show(interstitialAd)

            finish()
        }

        MobileAds.initialize(this, getString(R.string.admob_app_id))

        AdUtil.loadBannerAd(adView, applicationContext)

        interstitialAd = InterstitialAd(this)
        AdUtil.loadInterstitialAd(interstitialAd, applicationContext)
    }
}