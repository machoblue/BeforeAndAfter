package org.macho.beforeandafter.preference.alarm

import org.macho.beforeandafter.shared.BaseContract

interface AlarmContract {
    interface View: BaseContract.View<Presenter> {
        fun updateView()
        fun back()
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun save()
    }
}