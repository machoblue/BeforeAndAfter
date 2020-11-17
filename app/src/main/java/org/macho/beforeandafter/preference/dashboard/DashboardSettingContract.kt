package org.macho.beforeandafter.preference.dashboard

import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.BaseContract
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil

enum class DashboardCardType(val sharedPreferencesKey: String, val labelStringKey: Int) {
    WEIGHT_SUMMARY(SharedPreferencesUtil.Key.HIDE_WEIGHT_SUMMARY.string, R.string.weight_summary_title),
    WEIGHT_PROGRESS(SharedPreferencesUtil.Key.HIDE_WEIGHT_PROGRESS.string, R.string.progress_title),
    BMI(SharedPreferencesUtil.Key.HIDE_BMI.string, R.string.bmi_title);
}

interface DashboardSettingContract: BaseContract {
    interface View: BaseContract.View<Presenter> {
    }

    interface Presenter: BaseContract.Presenter<View> {
    }
}