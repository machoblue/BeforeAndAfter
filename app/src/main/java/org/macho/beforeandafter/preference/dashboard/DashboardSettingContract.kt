package org.macho.beforeandafter.preference.dashboard

import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.BaseContract
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil

enum class DashboardCardType(val sharedPreferencesKey: SharedPreferencesUtil.Key, val labelStringKey: Int) {
    WEIGHT_SUMMARY(SharedPreferencesUtil.Key.HIDE_WEIGHT_SUMMARY, R.string.weight_summary_title),
    WEIGHT_PROGRESS(SharedPreferencesUtil.Key.HIDE_WEIGHT_PROGRESS, R.string.progress_title),
    BMI(SharedPreferencesUtil.Key.HIDE_BMI, R.string.bmi_title);
}

interface DashboardSettingContract: BaseContract {
    interface View: BaseContract.View<Presenter> {
        fun initializeSelection(selectedIndices: List<Int>)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun select(selectedIndices: List<Int>)
    }
}