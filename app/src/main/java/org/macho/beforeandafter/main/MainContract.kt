package org.macho.beforeandafter.main

import org.macho.beforeandafter.shared.BaseContract

interface MainContract: BaseContract {
    interface View: BaseContract.View<Presenter> {
        fun showAlarmSettingDialog()
        fun showSurveyDialog()
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun handleRecordSavedEvent()
    }
}