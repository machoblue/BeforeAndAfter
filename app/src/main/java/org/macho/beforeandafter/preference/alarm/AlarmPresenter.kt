package org.macho.beforeandafter.preference.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import org.macho.beforeandafter.shared.di.ActivityScoped
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

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val alarmIntent = Intent(context, AlarmBroadcastReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        if (!isAlarmEnabled) {
            if (alarmIntent != null && alarmManager != null) {
                alarmManager.cancel(alarmIntent)
            }

            view?.back()
            return
        }

        // Set the alarm to start at approximately 2:00 p.m.
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, alarmHourOfDay)
            set(Calendar.MINUTE, alarmMinute)
        }

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmManager?.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )

        view?.back()
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
}