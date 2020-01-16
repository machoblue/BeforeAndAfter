package org.macho.beforeandafter.preference.backup

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.util.LogUtil
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import java.util.*

class RewardDialog: DialogFragment() {
    private lateinit var rewardedAd: RewardedAd

    private var haveWatchedAd = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        LogUtil.d(this, "onCreateDialog")

        val dialog = AlertDialog.Builder(activity).setTitle(R.string.backup_dialog_title)
                .setMessage(R.string.backup_dialog_message)
                .setPositiveButton(R.string.backup_dialog_ok_button_loading, null)
                .setNegativeButton(R.string.cancel, null)
                .create()

        rewardedAd = RewardedAd(activity, activity!!.getString(R.string.admob_unit_id_rewarded))
        rewardedAd.loadAd(AdRequest.Builder().build(), object: RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                LogUtil.d(this, "onRewardAdLoaded")
                if (dialog.isShowing) {
                    enablePositiveButton(dialog)
                }
            }
            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                // do nothing
            }
        })

        dialog.setOnShowListener {
            LogUtil.d(this, "onShow")
            val dialog = it as AlertDialog
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.isEnabled = false

            if (rewardedAd.isLoaded) {
                enablePositiveButton(dialog)
            }
        }

        return dialog
    }

    private fun enablePositiveButton(dialog: AlertDialog) {
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.isEnabled = true
        positiveButton.text = activity?.getString(R.string.backup_dialog_ok_button)
        positiveButton.setOnClickListener {
            rewardedAd.show(activity, object: RewardedAdCallback() {
                override fun onUserEarnedReward(reward: RewardItem) {
                    SharedPreferencesUtil.setLong(activity!!, SharedPreferencesUtil.Key.LATEST_WATCH_REWARDED_AD, Date().time)
                    haveWatchedAd = true
                }

                override fun onRewardedAdClosed() {
                    Log.i("backupDialog", "### onRewardedAdClosed")
                    if (haveWatchedAd) {
                        val action = RewardDialogDirections.actionRewardDialog2ToBackupFragment()
                        findNavController().navigate(action)
                    } else {
                        findNavController().popBackStack()
                    }
                }
            })
        }
    }
}