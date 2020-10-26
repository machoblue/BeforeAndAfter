package org.macho.beforeandafter.preference.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import java.util.*

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            configureAlarm(context)
        }
    }

    private fun configureAlarm(context: Context) {
        val isAlarmEnabled = SharedPreferencesUtil.getBoolean(context, SharedPreferencesUtil.Key.ALARM_ENABLED)
        val alarmHourOfDay = SharedPreferencesUtil.getInt(context, SharedPreferencesUtil.Key.ALARM_HOUR_OF_DAY)
        val alarmMinute = SharedPreferencesUtil.getInt(context, SharedPreferencesUtil.Key.ALARM_MINUTE)

        if (!isAlarmEnabled) {
            return
        }

        val requestCode = 0
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val alarmIntent = Intent(context, AlarmBroadcastReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, requestCode, intent, 0)
        } ?: return

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, alarmHourOfDay)
            set(Calendar.MINUTE, alarmMinute)
        }

        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmIntent
        )
    }
}