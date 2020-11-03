package org.macho.beforeandafter.main

import android.content.Context
import org.macho.beforeandafter.shared.data.record.RecordRepository
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.Analytics
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import java.util.*
import javax.inject.Inject

@ActivityScoped
class MainPresenter @Inject constructor(): MainContract.Presenter {

    var view: MainContract.View? = null

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var recordRepository: RecordRepository

    lateinit var analytics: Analytics

    override fun handleRecordSavedEvent() {
        val isAlarmEnabled = SharedPreferencesUtil.getBoolean(context, SharedPreferencesUtil.Key.ALARM_ENABLED)
        val neverDisplayAlarmSettingDialog = SharedPreferencesUtil.getBoolean(context, SharedPreferencesUtil.Key.NEVER_DISPLAY_ALARM_SETTING_DIALOG)
        if (!(isAlarmEnabled || neverDisplayAlarmSettingDialog)) {
            view?.showAlarmSettingDialog()
            return
        }

        val lastSurveyDialogDate = SharedPreferencesUtil.getLong(context, SharedPreferencesUtil.Key.LAST_SURVEY_DIALOG_TIME)
        if (Date().time - lastSurveyDialogDate > 1000L * 60 * 60 * 24 * 90) {
            recordRepository.getRecords { records ->
                val sortedRecords = records.sortedBy { - it.date }
                if (records.size > 9 // 10記録以上
                    && sortedRecords.last().weight - sortedRecords.first().weight >= 1 // 最初から1kgやせた
                    && sortedRecords.first().weight < sortedRecords[1].weight) // 前回よりやせた
                {
                    view?.showSurveyDialog()
                    analytics.logEvent(Analytics.Event.SURVEY_DIALOG_APPEAR)
                }
            }
        }
    }

    override fun takeView(view: MainContract.View) {
        this.view = view
        this.analytics = Analytics(context)
    }

    override fun dropView() {
        this.view = null
    }
}