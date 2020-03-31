package org.macho.beforeandafter.shared.util

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics

class Analytics(val context: Context) {
    enum class Event {
        BACKUP_START,
        BACKUP_RECOVERABLE_ERROR,
        BACKUP_FINISH,
        RESTORE_START,
        RESTORE_RECOVERABLE_ERROR,
        RESTORE_FINISH

//        companion object {
//            val BACKUP_START = "backup_start"
//            val BACKUP_FINISH = "backup_finish"
//            val RESTORE_START = "restore_start"
//            val RESTORE_FINISH = "restore_finish"
//        }
    }

    val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun logEvent(event: Event) {
        LogUtil.i(this, event.name + event.name.toLowerCase())
        firebaseAnalytics.logEvent(event.name.toLowerCase(), null)
    }
}