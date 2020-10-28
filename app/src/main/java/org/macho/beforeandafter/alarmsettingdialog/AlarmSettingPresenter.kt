package org.macho.beforeandafter.alarmsettingdialog

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import org.macho.beforeandafter.R
import org.macho.beforeandafter.preference.alarm.AlarmBroadcastReceiver
import org.macho.beforeandafter.preference.alarm.BootReceiver
import org.macho.beforeandafter.shared.util.Analytics
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import java.util.*
import javax.inject.Inject

class AlarmSettingPresenter @Inject constructor(): AlarmSettingContract.Presenter {

    @Inject
    lateinit var context: Context

    var view: AlarmSettingContract.View? = null

    override fun save(hourOfDay: Int, minute: Int) {
        SharedPreferencesUtil.setBoolean(context, SharedPreferencesUtil.Key.ALARM_ENABLED, true)
        SharedPreferencesUtil.setInt(context, SharedPreferencesUtil.Key.ALARM_HOUR_OF_DAY, hourOfDay)
        SharedPreferencesUtil.setInt(context, SharedPreferencesUtil.Key.ALARM_MINUTE, minute)

        configureAlarm(hourOfDay, minute)
        configureBootReceiver(true)
        Toast.makeText(context, context.getString(R.string.toast_saved), Toast.LENGTH_LONG).show()
    }

    override fun neverDisplay() {
        SharedPreferencesUtil.setBoolean(context, SharedPreferencesUtil.Key.NEVER_DISPLAY_ALARM_SETTING_DIALOG, true)
    }

    override fun takeView(view: AlarmSettingContract.View) {
        this.view = view
        Analytics(context).logEvent(Analytics.Event.ALARM_DIALOG_APPEAR)
    }

    override fun dropView() {
        this.view = null
    }

    private fun configureAlarm(hourOfDay: Int, minute: Int) {
        val requestCode = 0
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val alarmIntent = Intent(context, AlarmBroadcastReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, requestCode, intent, 0)
        } ?: return

        alarmManager.cancel(alarmIntent)

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)

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