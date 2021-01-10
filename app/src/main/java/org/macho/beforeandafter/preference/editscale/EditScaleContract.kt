package org.macho.beforeandafter.preference.editscale

import org.macho.beforeandafter.shared.BaseContract

interface EditScaleContract: BaseContract {
    interface View: BaseContract.View<Presenter> {
        fun updateViews(weightUnitIndex: Int, heightUnitIndex: Int)
    }
    interface Presenter: BaseContract.Presenter<View> {
        fun save(weightUnitIndex: Int, heightUnitIndex: Int)
    }
}