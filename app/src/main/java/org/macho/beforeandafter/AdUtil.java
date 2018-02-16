package org.macho.beforeandafter;

import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by yuukimatsushima on 2018/02/16.
 */

public class AdUtil {
    public static void show(InterstitialAd ad) {
        if (ad.isLoaded()) {
            ad.show();
        } else {
            System.out.println("The interstitial wasn't loaded yet.");
        }
    }
}
