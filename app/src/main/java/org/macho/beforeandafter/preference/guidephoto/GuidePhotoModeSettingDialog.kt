package org.macho.beforeandafter.preference.guidephoto

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil

class GuidePhotoModeSettingDialog: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
                .setTitle(R.string.guide_photo_mode_setting_title)
                .setItems(arrayOf(
                        requireContext().getString(R.string.guide_photo_mode_first),
                        requireContext().getString(R.string.guide_photo_mode_latest)
                )) { _, i ->
                    SharedPreferencesUtil.setInt(requireContext(), SharedPreferencesUtil.Key.GUIDE_PHOTO_MODE, i)
                    Toast.makeText(requireContext(), R.string.toast_saved, Toast.LENGTH_LONG).show()
                }
                .create()
    }
}