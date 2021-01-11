package org.macho.beforeandafter.preference.cameratimer

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil

class CameraTimerSettingDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.camera_timer_setting_dialog_frag, null)
        val cameraTimerSecondsPicker = view.findViewById<NumberPicker>(R.id.cameraTimerSecondsPicker).also {
            it.maxValue = 60
            it.minValue = 1
            it.value = SharedPreferencesUtil.getInt(requireContext(), SharedPreferencesUtil.Key.CAMERA_TIMER_SECONDS, 5)
        }
        return AlertDialog.Builder(requireActivity())
                .setTitle(R.string.camera_timer_title)
                .setView(view)
                .setPositiveButton(R.string.common_save) { _, i ->
                    SharedPreferencesUtil.setInt(requireContext(), SharedPreferencesUtil.Key.CAMERA_TIMER_SECONDS, cameraTimerSecondsPicker.value)
                    Toast.makeText(requireContext(), R.string.toast_saved, Toast.LENGTH_LONG).show()
                }
                .setNegativeButton(R.string.cancel) { dialogInterface, i ->
                    // do nothing
                }
                .create()
    }
}