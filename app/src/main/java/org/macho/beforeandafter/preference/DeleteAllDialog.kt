package org.macho.beforeandafter.preference

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import com.google.android.gms.ads.InterstitialAd
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.data.record.RecordDao
import org.macho.beforeandafter.shared.data.record.RecordDaoImpl
import org.macho.beforeandafter.shared.util.showIfNeeded
import java.io.File

class DeleteAllDialog: androidx.fragment.app.DialogFragment() {
    companion object {
        fun newInstance(activity: Activity): androidx.fragment.app.DialogFragment {
            return DeleteAllDialog()
        }
    }

    private var interstitialAd: InterstitialAd? = null

    private val recordDao: RecordDao = RecordDaoImpl() // TODO: take from Dagger

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        AdUtil.initializeMobileAds(context!!)
        interstitialAd = AdUtil.instantiateAndLoadInterstitialAd(context!!)

        return AlertDialog.Builder(activity).setTitle(R.string.delete_all_title)
                .setMessage(R.string.delete_all_confirmation_message)
                .setPositiveButton(R.string.ok) { dialogInterface, i ->
                    recordDao.deleteAll()
                    val fileArray = File(context!!.filesDir.toString()).listFiles()
                    if (fileArray != null) {
                        for (file in fileArray) {
                            file.delete()
                        }
                    }
                    interstitialAd?.showIfNeeded(context!!)
                }
                .setNegativeButton(R.string.cancel) { dialogInterface, i ->
                    interstitialAd?.showIfNeeded(context!!)
                }
                .create()
    }
}