package org.macho.beforeandafter.preference.restore

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.util.showIfNeeded

class RestoreDialog: DialogFragment() {

    private var interstitialAd: InterstitialAd? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        AdUtil.initializeMobileAds(context!!)

        interstitialAd = AdUtil.instantiateAndLoadInterstitialAd(context!!)

        return AlertDialog.Builder(activity).setTitle(R.string.restore_dialog_title)
                .setMessage(R.string.restore_dialog_message)
                .setPositiveButton(R.string.ok) { dialogInterface, i ->
                    val action = RestoreDialogDirections.actionRestoreDialogToRestoreFragment()
                    findNavController().navigate(action)
                }
                .setNegativeButton(R.string.cancel) { dialogInterface, i ->
                    interstitialAd?.showIfNeeded(context!!)
                }
                .create()
    }
}