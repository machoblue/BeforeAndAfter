package org.macho.beforeandafter.preference.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.Analytics
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import java.util.*
import javax.inject.Inject

@ActivityScoped
class AlarmPresenter @Inject constructor(): AlarmContract.Presenter {

    @Inject
    lateinit var context: Context

    var view: AlarmContract.View? = null

    var isAlarmEnabled = false
    var alarmHourOfDay = 0
    var alarmMinute = 0

    override fun udpateIsAlarmEnabled(isAlarmEnabled: Boolean) {
        this.isAlarmEnabled = isAlarmEnabled
        view?.updateView(this.isAlarmEnabled, this.alarmHourOfDay, this.alarmMinute)
    }

    override fun updateAlarmTime(hourOfDay: Int, minute: Int) {
        this.alarmHourOfDay = hourOfDay
        this.alarmMinute = minute
        view?.updateView(this.isAlarmEnabled, this.alarmHourOfDay, this.alarmMinute)
    }

    override fun save() {
        SharedPreferencesUtil.setBoolean(context, SharedPreferencesUtil.Key.ALARM_ENABLED, isAlarmEnabled)
        SharedPreferencesUtil.setInt(context, SharedPreferencesUtil.Key.ALARM_HOUR_OF_DAY, alarmHourOfDay)
        SharedPreferencesUtil.setInt(context, SharedPreferencesUtil.Key.ALARM_MINUTE, alarmMinute)

        configureAlarm()
        configureBootReceiver(isAlarmEnabled)
        view?.back()
        Toast.makeText(context, context.getString(R.string.toast_saved), Toast.LENGTH_LONG).show()
    }

    override fun takeView(view: AlarmContract.View) {
        this.view = view

        isAlarmEnabled = SharedPreferencesUtil.getBoolean(context, SharedPreferencesUtil.Key.ALARM_ENABLED)
        alarmHourOfDay = SharedPreferencesUtil.getInt(context, SharedPreferencesUtil.Key.ALARM_HOUR_OF_DAY)
        alarmMinute = SharedPreferencesUtil.getInt(context, SharedPreferencesUtil.Key.ALARM_MINUTE)
        view.updateView(isAlarmEnabled, alarmHourOfDay, alarmMinute)
    }

    override fun dropView() {
        view = null
    }

    private fun configureAlarm() {
        val requestCode = 0
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val alarmIntent = Intent(context, AlarmBroadcastReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, requestCode, intent, 0)
        } ?: return

        alarmManager.cancel(alarmIntent)

        if (!isAlarmEnabled) {
            return
        }

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, alarmHourOfDay)
            set(Calendar.MINUTE, alarmMinute)

            if (timeInMillis < System.currentTimeMillis()) {
                timeInMillis += 1000 * 60 * 60 * 24
            }
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )

        Analytics(context).logEvent(Analytics.Event.ALARM_ENABLE)
    }

    private fun configureBootReceiver(enabled: Boolean) {
        val receiver = ComponentName(context, BootReceiver::class.java)

        context.packageManager.setComponentEnabledSetting(
            receiver,
            if (enabled) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}