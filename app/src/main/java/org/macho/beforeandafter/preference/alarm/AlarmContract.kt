package org.macho.beforeandafter.preference.alarm

import org.macho.beforeandafter.shared.BaseContract
import java.time.LocalTime

interface AlarmContract {
    interface View: BaseContract.View<Presenter> {
        fun updateView(isAlarmEnabled: Boolean, hour: Int, minute: Int)
        fun back()
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun save(isAlarmEnabled: Boolean, hour: Int, minute: Int)
    }
}