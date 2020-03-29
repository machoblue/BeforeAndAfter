package org.macho.beforeandafter.preference.restore

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.InterstitialAd
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.data.restoreimage.RestoreImageRepository
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.showIfNeeded
import javax.inject.Inject

@ActivityScoped
class RestoreResumeDialog @Inject constructor(): DialogFragment() {

    @Inject
    lateinit var restoreImageRepository: RestoreImageRepository

    private var interstitialAd: InterstitialAd? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        AdUtil.initializeMobileAds(context!!)

        interstitialAd = AdUtil.instantiateAndLoadInterstitialAd(context!!)

        return AlertDialog.Builder(activity).setTitle(R.string.restore_resume_dialog_title)
                .setMessage(R.string.restore_resume_dialog_message)
                .setPositiveButton(R.string.restore_resume_dialog_ok) { dialogInterface, i ->
                    val action = RestoreResumeDialogDirections.actionRestoreResumeDialogToRestoreFragment()
                    findNavController().navigate(action)
                }
                .setNegativeButton(R.string.cancel) { dialogInterface, i ->
                    interstitialAd?.showIfNeeded(context!!)
                }
                .setNeutralButton(R.string.restore_resume_dialog_neutral) {dialogInterface, i ->
                    restoreImageRepository.deleteAll {
                        val action = RestoreResumeDialogDirections.actionRestoreResumeDialogToRestoreFragment()
                        findNavController().navigate(action)
                    }
                }
                .create()
    }
}