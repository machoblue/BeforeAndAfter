package org.macho.beforeandafter.preference.backup

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

class BackupDialog: DialogFragment() {

    private lateinit var interstitialAd: InterstitialAd

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        MobileAds.initialize(activity, getString(R.string.admob_app_id))

        interstitialAd = InterstitialAd(activity)
        AdUtil.loadInterstitialAd(interstitialAd, context!!)

        return AlertDialog.Builder(activity).setTitle(R.string.backup_dialog_title)
                .setMessage(R.string.backup_dialog_message)
                .setPositiveButton(R.string.ok) { dialogInterface, i ->
                    val action = BackupDialogDirections.actionBackupDialog4ToBackupFragment()
                    findNavController().navigate(action)
                }
                .setNegativeButton(R.string.cancel) { dialogInterface, i ->
                    AdUtil.show(interstitialAd)
                }
                .create()
    }
}