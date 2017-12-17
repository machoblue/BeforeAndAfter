package org.macho.beforeandafter.preference;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import net.nend.android.NendAdInterstitial;

import org.macho.beforeandafter.R;
import org.macho.beforeandafter.RecordDao;

import java.io.File;

/**
 * Created by yuukimatsushima on 2017/10/09.
 */

public class DeleteAllDialog extends DialogFragment {
    public static DialogFragment newInstance(Activity activity) {
        NendAdInterstitial.loadAd(activity, "2e022cf05260b47b52bb803de578742b38422bf9", 824867);
        return new DeleteAllDialog();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity()).setTitle(R.string.delete_all_title)
                .setMessage(R.string.delete_all_confirmation_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RecordDao.getInstance().deleteAll();
                        for (File file : new File("/data/data/org.macho.beforeandafter/files").listFiles()) {
                            file.delete();
                        }
                        NendAdInterstitial.showAd(getActivity());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NendAdInterstitial.showAd(getActivity());
                    }
                })
                .create();
    }
}
