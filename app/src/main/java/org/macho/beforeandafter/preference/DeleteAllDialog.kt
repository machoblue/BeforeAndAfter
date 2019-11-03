package org.macho.beforeandafter.preference

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.shared.BeforeAndAfterConst
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.data.RecordDao
import org.macho.beforeandafter.shared.data.RecordDaoImpl
import java.io.File

class DeleteAllDialog: androidx.fragment.app.DialogFragment() {
    companion object {
        fun newInstance(activity: Activity): androidx.fragment.app.DialogFragment {
            return DeleteAllDialog()
        }
    }

    private lateinit var interstitialAd: InterstitialAd

    private val recordDao: RecordDao = RecordDaoImpl() // TODO: take from Dagger

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        MobileAds.initialize(activity, getString(R.string.admob_app_id))

        interstitialAd = InterstitialAd(activity)
        AdUtil.loadInterstitialAd(interstitialAd, context!!)

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
                    AdUtil.show(interstitialAd)
                }
                .setNegativeButton(R.string.cancel) { dialogInterface, i ->
                    AdUtil.show(interstitialAd)
                }
                .create()
    }
}