package org.macho.beforeandafter.preference.alarm

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.alarm_frag.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.extensions.setTextColor
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

@ActivityScoped
class AlarmFragment @Inject constructor(): DaggerFragment(), AlarmContract.View {
    @Inject
    override lateinit var presenter: AlarmContract.Presenter

    private val dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.alarm_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alarmSwitch.setOnCheckedChangeListener { button, isChecked ->
            presenter.udpateIsAlarmEnabled(isChecked)
        }

        alarmTimeButton.setOnClickListener {
            val alarmTimeButton = it as? Button ?: return@setOnClickListener
            val time = dateFormat.parse(alarmTimeButton.text.toString()) ?: return@setOnClickListener
            showTimePickerDialog(time)
        }

        setHasOptionsMenu(true); // for save button on navBar
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.dropView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.editaddrecord_menu, menu) // TODO: refactor
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                presenter.save()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // MARK: - AlarmContract.View
    override fun updateView(isAlarmEnabled: Boolean, hour: Int, minute: Int) {
        alarmSwitch.isChecked = isAlarmEnabled
        alarmTimeButton.text = dateFormat.format(Calendar.getInstance().also {
            it.set(Calendar.HOUR_OF_DAY, hour)
            it.set(Calendar.MINUTE, minute)
        }.time)
        alarmTimeButton.isEnabled = isAlarmEnabled
        alarmTimeButton.setTextColor(context!!, if (isAlarmEnabled) R.color.light_blue else R.color.light_gray_text)
    }

    override fun back() {
        findNavController().popBackStack()
    }

    private fun showTimePickerDialog(defaultDate: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = defaultDate
        TimePickerDialog(context, {view, hourOfDay, minute ->
            presenter.updateAlarmTime(hourOfDay, minute)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }
}