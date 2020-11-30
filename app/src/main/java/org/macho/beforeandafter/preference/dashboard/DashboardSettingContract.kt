package org.macho.beforeandafter.preference.dashboard

import org.macho.beforeandafter.shared.BaseContract
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil

class DashboardSettingItem(val sharedPreferencesKey: SharedPreferencesUtil.Key, val labelStringKey: Int, val defaultIsHidden: Boolean)

interface DashboardSettingContract: BaseContract {
    interface View: BaseContract.View<Presenter> {
        fun populateItems(items: List<DashboardSettingItem>)
        fun initializeSelection(selectedIndices: List<Int>)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun select(selectedIndices: List<Int>)
    }
}