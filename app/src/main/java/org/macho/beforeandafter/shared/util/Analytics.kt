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
        RESTORE_FINISH,

        STORE_REVIEW_DIALOG_APPEAR,
        STORE_REVIEW_DIALOG_CANCEL,
        STORE_REVIEW_DIALOG_HIDE,
        STORE_REVIEW_DIALOG_OPEN_STORE,
    }

    val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun logEvent(event: Event) {
        firebaseAnalytics.logEvent(event.name.toLowerCase(), null)
    }
}