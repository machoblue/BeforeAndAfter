package org.macho.beforeandafter.preference

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.DialogFragment
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.util.showIfNeeded


class UseStandardCameraDialog: androidx.fragment.app.DialogFragment() {
    companion object {
        fun newInstance(activity: Activity): androidx.fragment.app.DialogFragment {
            return UseStandardCameraDialog()
        }
    }

    private lateinit var layout: LinearLayout
    private lateinit var checkBox: CheckBox
    private var interstitialAd: InterstitialAd? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        AdUtil.initializeMobileAds(context!!)

        interstitialAd = AdUtil.instantiateAndLoadInterstitialAd(context!!)

        layout = LinearLayout(context!!)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layout.orientation = LinearLayout.HORIZONTAL
        layout.layoutParams = params

        var text = TextView(context!!)
        text.text = "        " // TODO:　なんとかする
        layout.addView(text)

        checkBox = CheckBox(context!!)
        checkBox.setText(R.string.use_standard_camera)
        layout.addView(checkBox)

        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val useStandardCamera = preferences.getBoolean("USE_STANDARD_CAMERA", false)
        checkBox.setChecked(useStandardCamera)

        return AlertDialog.Builder(activity).setTitle(R.string.use_standard_camera)
                .setMessage(R.string.use_standard_camera_message)
                .setView(layout)
                .setPositiveButton(R.string.ok) { dialogInterface, i ->
                    preferences.edit().putBoolean("USE_STANDARD_CAMERA", checkBox.isChecked).commit()
                    interstitialAd?.showIfNeeded(context!!)
                }
                .setNegativeButton(R.string.cancel) { dialogInterface, i ->
                    interstitialAd?.showIfNeeded(context!!)
                }
                .create()


    }
}