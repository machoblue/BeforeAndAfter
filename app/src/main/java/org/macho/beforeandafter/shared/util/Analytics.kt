package org.macho.beforeandafter.shared.util

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class Analytics(val context: Context) {
    enum class Event {
        BACKUP_START,
        BACKUP_SIGN_IN_SUCCESS,
        BACKUP_ALREADY_SIGNED_IN,
        BACKUP_SAVE_FIRST_PHOTO_START,
        BACKUP_SAVE_FIRST_PHOTO_FINISH,
        BACKUP_RECOVERABLE_ERROR,
        BACKUP_FINISH,

        RESTORE_START,
        RESTORE_RECOVERABLE_ERROR,
        RESTORE_FINISH_SUCCESS,
        RESTORE_FINISH_PARTIAL_FAIL,

        SURVEY_DIALOG_APPEAR,
        SURVEY_DIALOG_HELP,
        SURVEY_DIALOG_NOT_HELP,
        STORE_REVIEW_DIALOG_OPEN_STORE,
        STORE_REVIEW_DIALOG_CANCEL,
        BUG_REPORT_DIALOG_CANCEL,

        STORE_REVIEW_FRON_SETTING,

        ALARM_ENABLE,
        ALARM_FIRED,
        ALARM_DIALOG_APPEAR,

        SELECT_CUSTOME_CAMERA,
        SELECT_OTHER_APP_CAMERA,
        SELECT_GALLERY,

        TAKE_PICTURE,
        PICTURE_SAVE_COMPLETE,
        RECEIVE_CUSTOME_CAMERA_PHOTO,

        RECEIVE_OTHER_APP_PHOTO,

        RECEIVE_GALLERY_PHOTO,
    }

    val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun logEvent(event: Event, bundle: Bundle? = null) {
        LogUtil.d("Analytics", "${event.name.toLowerCase()} ${bundle}")
        firebaseAnalytics.logEvent(event.name.toLowerCase(), bundle)
    }
}