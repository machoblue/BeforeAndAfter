package org.macho.beforeandafter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.net.MalformedURLException;
import java.net.URL;

public class AdUtil {
    private static final String PRIVACY_URL = "https://sites.google.com/site/jiantamudaiettoriji/";
    private static final String CAN_FORWARD_PERSONALIZED_AD_REQUEST = "CAN_FORWARD_PERSONALIZED_AD_REQUEST";
    private ConsentForm form;
    private static AdUtil instance;

    public static AdUtil getInstance() {
        if (instance == null) {
            instance = new AdUtil();
        } else {
            instance.form = null; // 初期化
        }
        return instance;
    }

    public static boolean isInEEA(Context context) {
        boolean isEEA = ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown();
        System.out.println("*** AdUtil.isInEEA:" + isEEA + " ***");
        return isEEA;
    }

    public void requestConsentInfoUpdateIfNeed(final Context context) {
        if (isInEEA(context)) {
            return;
        }

        String[] publisherIds = {context.getString(R.string.publisher_id)};

        ConsentInformation consentInformation = ConsentInformation.getInstance(context);
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                // User's consent status successfully updated.
                switch (consentStatus) {
                    case PERSONALIZED:
                    case NON_PERSONALIZED:
                        break;
                    case UNKNOWN:
                        showConsentForm(context);
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
                System.out.println("*** MainActivity.onFailedToUpdateConsentInfo ***");
                throw new RuntimeException(errorDescription);
            }
        });
    }

    public void showConsentForm(final Context context) {
        URL privacyUrl = null;
        try {
            privacyUrl = new URL(PRIVACY_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        form = new ConsentForm.Builder(context, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        System.out.println("*** AdUtil.onContentFormLoaded *** ");
                        form.show();
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                    }

                    @Override
                    public void onConsentFormClosed(
                            ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        // Consent form was closed.
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                        switch (consentStatus) {
                            case PERSONALIZED:
                                preferences.edit().putBoolean(CAN_FORWARD_PERSONALIZED_AD_REQUEST, true).commit();
//                                boolean canForwardPersonalizedAdRequest = preferences.getBoolean(CAN_FORWARD_PERSONALIZED_AD_REQUEST, false);
                                break;
                            case NON_PERSONALIZED:
                                preferences.edit().putBoolean(CAN_FORWARD_PERSONALIZED_AD_REQUEST, false).commit();
                                break;
                            case UNKNOWN:
                                throw new RuntimeException();
                        }
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        // Consent form error.
                        System.out.println("*** AdUtil.onConsentFormError ***");
                        throw new RuntimeException(errorDescription);
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
//                .withAdFreeOption()
                .build();
        System.out.println("*** AdUtil.onConsentInfoUpdate *** ");
    }

    public void loadBannerAd(final AdView adView, final Context context) {
        if (isInEEA(context) && PreferenceManager.getDefaultSharedPreferences(context).getBoolean(CAN_FORWARD_PERSONALIZED_AD_REQUEST, true)) {
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();
            adView.loadAd(adRequest);
        } else {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
    }

    public void loadInterstitialAd(final InterstitialAd interstitialAd , final Context context) {
        if (isInEEA(context) && PreferenceManager.getDefaultSharedPreferences(context).getBoolean(CAN_FORWARD_PERSONALIZED_AD_REQUEST, true)) {
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            AdRequest adRequest2 = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();
            interstitialAd.setAdUnitId(context.getString(R.string.admob_unit_id_interstitial));
            interstitialAd.loadAd(adRequest2);
        } else {
            AdRequest adRequest = new AdRequest.Builder().build();
            interstitialAd.setAdUnitId(context.getString(R.string.admob_unit_id_interstitial));
            interstitialAd.loadAd(adRequest);
        }
    }

    public static void show(InterstitialAd interstitialAd) {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }
}

