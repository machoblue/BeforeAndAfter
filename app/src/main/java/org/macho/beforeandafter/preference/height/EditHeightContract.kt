package org.macho.beforeandafter.preference.height

import org.macho.beforeandafter.shared.BaseContract

interface EditHeightContract: BaseContract {
    interface View: BaseContract.View<Presenter> {
        fun update(heightText: String)
        fun finish()
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun save(heightText: String)
    }
}