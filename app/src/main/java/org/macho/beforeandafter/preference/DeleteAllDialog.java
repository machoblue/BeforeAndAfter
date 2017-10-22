package org.macho.beforeandafter.preference;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.macho.beforeandafter.R;
import org.macho.beforeandafter.RecordDao;

import java.io.File;

/**
 * Created by yuukimatsushima on 2017/10/09.
 */

public class DeleteAllDialog extends DialogFragment {
    public static DialogFragment newInstance() {
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
