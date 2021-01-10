package org.macho.beforeandafter.preference.height

import org.macho.beforeandafter.shared.BaseContract

interface HeightForm {}
class CentimeterForm(val centimeterText: String): HeightForm {}
class FeetForm(val feetText: String, val inchText: String): HeightForm {}

interface EditHeightContract: BaseContract {
    interface View: BaseContract.View<Presenter> {
        fun update(heightForm: HeightForm)
        fun finish()
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun save(heightForm: HeightForm)
    }
}