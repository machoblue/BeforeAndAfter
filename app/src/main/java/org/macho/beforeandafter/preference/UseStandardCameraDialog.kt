package org.macho.beforeandafter.preference

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.DialogFragment
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import org.macho.beforeandafter.AdUtil
import org.macho.beforeandafter.R


class UseStandardCameraDialog: DialogFragment() {
    companion object {
        fun newInstance(activity: Activity): DialogFragment {
            return UseStandardCameraDialog()
        }
    }

    private lateinit var layout: LinearLayout
    private lateinit var checkBox: CheckBox
    private lateinit var interstitialAd: InterstitialAd

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        MobileAds.initialize(activity, getString(R.string.admob_app_id))

        interstitialAd = InterstitialAd(activity)
        AdUtil.loadInterstitialAd(interstitialAd, context!!)

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
                    AdUtil.show(interstitialAd)
                }
                .setNegativeButton(R.string.cancel) { dialogInterface, i ->
                    AdUtil.show(interstitialAd)
                }
                .create()


    }
}