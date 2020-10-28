package org.macho.beforeandafter.alarmsettingdialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.di.FragmentScoped
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

@FragmentScoped
class AlarmSettingDialog @Inject constructor(): DialogFragment(), AlarmSettingContract.View {

    @Inject
    override lateinit var presenter: AlarmSettingContract.Presenter

    private val dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater;
        val view = inflater.inflate(R.layout.alarm_setting_dialog_frag, null)

        val dialog = AlertDialog.Builder(requireActivity())
            .setView(view)
                .setNegativeButton(R.string.common_skip) { dialogInterface, i -> /* do nothing */ }
                .setPositiveButton(R.string.common_save) { dialogInterface, i ->
                    val alarmTimeButton = dialog?.findViewById<Button>(R.id.alarmTimeButton) ?: return@setPositiveButton
                    val timeText = alarmTimeButton.text
                    val calendar = Calendar.getInstance().also {
                        it.time = dateFormat.parse(timeText.toString()) ?: return@setPositiveButton
                    }
                    presenter.save(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
                }
                .setNeutralButton(R.string.common_never_display) { dialogInterface, i ->
                    presenter.neverDisplay()
                }
            .create()

        dialog.setOnShowListener {
            presenter.takeView(this)

            val alarmTimeButton = dialog.findViewById<Button>(R.id.alarmTimeButton)
            val calendar = Calendar.getInstance().also {
                it.set(Calendar.MINUTE, if(it.get(Calendar.MINUTE) >= 30) 30 else 0)
            }
            alarmTimeButton.text = dateFormat.format(calendar.time)
            alarmTimeButton.setOnClickListener {
                if (it !is Button) return@setOnClickListener
                val time = dateFormat.parse(it.text.toString()) ?: return@setOnClickListener
                showTimePickerDialog(time)
            }
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.dropView()
    }

    private fun showTimePickerDialog(defaultDate: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = defaultDate
        TimePickerDialog(context, { view, hourOfDay, minute ->
            val alarmTimeButton = dialog?.findViewById<Button>(R.id.alarmTimeButton) ?: return@TimePickerDialog
            alarmTimeButton.text = dateFormat.format(Calendar.getInstance().also {
                it.set(Calendar.HOUR_OF_DAY, hourOfDay)
                it.set(Calendar.MINUTE, minute)
            }.time)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }
}