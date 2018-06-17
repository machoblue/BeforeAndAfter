package org.macho.beforeandafter.preference;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.macho.beforeandafter.AdUtil;
import org.macho.beforeandafter.R;

/**
 * Created by yuukimatsushima on 2017/10/09.
 */

public class UseStandardCameraDialog extends DialogFragment {
    private LinearLayout layout;
    private CheckBox checkBox;
    private InterstitialAd interstitialAd;
    public static DialogFragment newInstance(Activity activity) {
        return new UseStandardCameraDialog();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MobileAds.initialize(getActivity(), getString(R.string.admob_app_id));

        interstitialAd = new InterstitialAd(getActivity());
        AdUtil.getInstance().loadInterstitialAd(interstitialAd, getContext());

        layout = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(params);
        TextView text = new TextView(getContext());
        text.setText("        ");
        checkBox = new CheckBox(getContext());
        checkBox.setText(R.string.use_standard_camera_yes);
        layout.addView(text);
        layout.addView(checkBox);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean useStandardCamera = preferences.getBoolean("USE_STANDARD_CAMERA", false);
        checkBox.setChecked(useStandardCamera);
        return new AlertDialog.Builder(getActivity()).setTitle(R.string.use_standard_camera)
                .setMessage(R.string.use_standard_camera_message)
                .setView(layout)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                       if (checkBox.isChecked()) {
                           preferences.edit().putBoolean("USE_STANDARD_CAMERA", true).commit();
                       } else {
                           preferences.edit().putBoolean("USE_STANDARD_CAMERA", false).commit();
                       }
                        AdUtil.show(interstitialAd);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AdUtil.show(interstitialAd);
                    }
                })
                .create();
    }
}
