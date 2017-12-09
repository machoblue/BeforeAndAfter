package org.macho.beforeandafter.preference;

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

import org.macho.beforeandafter.R;

/**
 * Created by yuukimatsushima on 2017/10/09.
 */

public class UseOldGrapheFragmentDialog extends DialogFragment {
    private LinearLayout layout;
    private CheckBox checkBox;
    public static DialogFragment newInstance() {
        return new UseOldGrapheFragmentDialog();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        layout = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(params);
        TextView text = new TextView(getContext());
        text.setText("        ");
        checkBox = new CheckBox(getContext());
        checkBox.setText(R.string.use_old_graphe_ui_yes);
        layout.addView(text);
        layout.addView(checkBox);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean useOldGrapheFragment = preferences.getBoolean("USE_OLD_GRAPHE_FRAGMENT", false);
        checkBox.setChecked(useOldGrapheFragment);
        return new AlertDialog.Builder(getActivity()).setTitle(R.string.delete_all_title)
                .setMessage(R.string.delete_all_confirmation_message)
                .setView(layout)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                       if (checkBox.isChecked()) {
                           preferences.edit().putBoolean("USE_OLD_GRAPHE_FRAGMENT", true).commit();
                       } else {
                           preferences.edit().putBoolean("USE_OLD_GRAPHE_FRAGMENT", false).commit();
                       }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();
    }
}
