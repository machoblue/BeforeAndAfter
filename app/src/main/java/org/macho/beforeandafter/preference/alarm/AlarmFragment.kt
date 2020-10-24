package org.macho.beforeandafter.preference.alarm

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.alarm_frag.*
import kotlinx.android.synthetic.main.edit_goal_fragment.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.di.ActivityScoped
import java.text.DateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
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
                val time = dateFormat.parse(alarmTimeButton.text.toString()) ?: return false
                val calendar = Calendar.getInstance().also {
                    it.time = time
                }
                presenter.save(alarmSwitch.isChecked, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
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
    }

    override fun back() {
        findNavController().popBackStack()
    }
}