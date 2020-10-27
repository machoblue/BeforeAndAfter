package org.macho.beforeandafter.alarmsettingdialog

import org.macho.beforeandafter.shared.BaseContract

interface AlarmSettingContract {
    interface View: BaseContract.View<Presenter> {
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun save(hourOfDay: Int, minute: Int)
        fun neverDisplay()
    }
}