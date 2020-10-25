package org.macho.beforeandafter.preference.alarm

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.macho.beforeandafter.MainActivity
import org.macho.beforeandafter.R

class AlarmBroadcastReceiver(): BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context!!, context!!.getString(R.string.channel_id))
//                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context!!.getString(R.string.notification_title))
                .setContentText(context!!.getString(R.string.notification_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(R.string.notification_id, builder.build())
        }
    }
}