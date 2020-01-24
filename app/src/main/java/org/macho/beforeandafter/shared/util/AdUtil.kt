package org.macho.beforeandafter.shared.util

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import com.google.ads.consent.*
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import org.macho.beforeandafter.R
import java.net.URL

object AdUtil {
    const val PRIVACY_URL = "https://sites.google.com/site/jiantamudaiettoriji/"
    const val CAN_FORWARD_PERSONALIZED_AD_REQUEST = "CAN_FORWARD_PERSONALIZED_AD_REQUEST"

    private var form: ConsentForm? = null

    fun isInEEA(context: Context): Boolean {
        return ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown()
    }

    fun requestConsentInfoUpdateIfNeed(context: Context) {
        if (isInEEA(context)) {
            return
        }

        val publisherIds = arrayOf(context.getString(R.string.publisher_id))

        val consentInformation = ConsentInformation.getInstance(context)
        consentInformation.requestConsentInfoUpdate(publisherIds, object: ConsentInfoUpdateListener {
            override fun onConsentInfoUpdated(consentStatus: ConsentStatus?) {
                when (consentStatus) {
                    ConsentStatus.PERSONALIZED -> {
                        // do nothing
                    }
                    ConsentStatus.NON_PERSONALIZED -> {
                        // do nothing
                    }
                    ConsentStatus.UNKNOWN -> {
                        showConsentForm(context)
                    }
                }
            }

            override fun onFailedToUpdateConsentInfo(reason: String?) {
//                throw RuntimeException(reason)
            }
        })

    }

    fun showConsentForm(context: Context) {
        val privacyUrl = URL(PRIVACY_URL)

        form = ConsentForm.Builder(context, privacyUrl).withListener(object: ConsentFormListener() {
            override fun onConsentFormLoaded() {
                super.onConsentFormLoaded()
                form?.show()
            }

            override fun onConsentFormOpened() {
                super.onConsentFormOpened()
                // Consent form was displayed.
            }

            override fun onConsentFormClosed(consentStatus: ConsentStatus?, userPrefersAdFree: Boolean?) {
                super.onConsentFormClosed(consentStatus, userPrefersAdFree)
                val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                when (consentStatus) {
                    ConsentStatus.PERSONALIZED -> {
                        preferences.edit().putBoolean(CAN_FORWARD_PERSONALIZED_AD_REQUEST, true).commit()
                    }
                    ConsentStatus.NON_PERSONALIZED -> {
                        preferences.edit().putBoolean(CAN_FORWARD_PERSONALIZED_AD_REQUEST, false).commit()
                    }
                    ConsentStatus.UNKNOWN -> {
                        throw RuntimeException()
                    }

                }
            }

            override fun onConsentFormError(reason: String?) {
                super.onConsentFormError(reason)
                throw RuntimeException(reason)
            }

        })
        .withPersonalizedAdsOption()
        .withNonPersonalizedAdsOption()
        .build()
    }

    fun isBannerAdHidden(context: Context): Boolean {
        return context.resources.getBoolean(R.bool.hide_banner_ad)
    }

    fun isInterstitialAdHidden(context: Context): Boolean {
        return context.resources.getBoolean(R.bool.hide_interstitial_ad)
    }

    fun isRewardAdHidden(context: Context): Boolean {
        return context.resources.getBoolean(R.bool.hide_reward_ad)
    }

    fun initializeMobileAds(context: Context) {
        if (isBannerAdHidden(context) && isInterstitialAdHidden(context)) return
        MobileAds.initialize(context, context.getString(R.string.admob_app_id))
    }

    fun loadBannerAd(adView: AdView, context: Context) {
        if (isBannerAdHidden(context)) return
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val canForwardPersonalizedAdRequest = preferences.getBoolean(CAN_FORWARD_PERSONALIZED_AD_REQUEST, true)
        if (isInEEA(context) && canForwardPersonalizedAdRequest) {
            val extras = Bundle()
            extras.putString("npa", "1")
            val adRequest = AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()
            adView.loadAd(adRequest)
        } else {
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }
    }

    fun instantiateAndLoadInterstitialAd(context: Context): InterstitialAd? {
        if (isInterstitialAdHidden(context)) return null
        val interstitialAd = InterstitialAd(context)
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val canForwardPersonalizedAdRequest = preferences.getBoolean(CAN_FORWARD_PERSONALIZED_AD_REQUEST, true)
        var adRequest: AdRequest? = null
        if (isInEEA(context) && canForwardPersonalizedAdRequest) {
            val extras = Bundle()
            extras.putString("npa", "1")
            adRequest = AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()
        } else {
            adRequest = AdRequest.Builder().build()
        }
        interstitialAd.setAdUnitId(context.getString(R.string.admob_unit_id_interstitial))
        interstitialAd.loadAd(adRequest!!)
        return interstitialAd
    }

}

fun InterstitialAd.showIfNeeded(context: Context) {
    if (AdUtil.isInterstitialAdHidden(context)) return
    if (isLoaded) {
        show()
    } else {
        Log.d("TAG", "The interstitial wasn't loaded yet.")
    }
}
