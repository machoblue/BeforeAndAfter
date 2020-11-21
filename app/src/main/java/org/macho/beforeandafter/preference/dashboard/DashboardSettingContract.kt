package org.macho.beforeandafter.preference.dashboard

import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.BaseContract
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil

enum class DashboardCardType(val sharedPreferencesKey: SharedPreferencesUtil.Key, val labelStringKey: Int, val defaultIsHidden: Boolean) {
    WEIGHT_SUMMARY(SharedPreferencesUtil.Key.HIDE_WEIGHT_SUMMARY, R.string.weight_summary_title, false),
    WEIGHT_PROGRESS(SharedPreferencesUtil.Key.HIDE_WEIGHT_PROGRESS, R.string.progress_title, false),
    BMI(SharedPreferencesUtil.Key.HIDE_BMI, R.string.bmi_title, false),
    BODY_FAT_SUMMARY(SharedPreferencesUtil.Key.HIDE_BODY_FAT_SUMMARY, R.string.body_fat_summary_title, true),
    BODY_FAT_PROGRESS(SharedPreferencesUtil.Key.HIDE_BODY_FAT_PROGRESS, R.string.body_fat_progress_title, true),
    FRONT_PHOTO_SUMMARY_BY_WEIGHT(SharedPreferencesUtil.Key.HIDE_FRONT_PHOTO_SUMMARY_BY_WEIGHT, R.string.front_photo_summary_by_weight_title, false),
    SIDE_PHOTO_SUMMARY_BY_WEIGHT(SharedPreferencesUtil.Key.HIDE_SIDE_PHOTO_SUMMARY_BY_WEIGHT, R.string.side_photo_summary_by_weight_title, false),
    OTHER1_PHOTO_SUMMARY_BY_WEIGHT(SharedPreferencesUtil.Key.HIDE_OTHER1_PHOTO_SUMMARY_BY_WEIGHT, R.string.other1_photo_summary_by_weight_title, true),
    OTHER2_PHOTO_SUMMARY_BY_WEIGHT(SharedPreferencesUtil.Key.HIDE_OTHER2_PHOTO_SUMMARY_BY_WEIGHT, R.string.other2_photo_summary_by_weight_title, true),
    OTHER3_PHOTO_SUMMARY_BY_WEIGHT(SharedPreferencesUtil.Key.HIDE_OTHER3_PHOTO_SUMMARY_BY_WEIGHT, R.string.other3_photo_summary_by_weight_title, true),
    FRONT_PHOTO_SUMMARY_BY_BODY_FAT(SharedPreferencesUtil.Key.HIDE_FRONT_PHOTO_SUMMARY_BY_BODY_FAT, R.string.front_photo_summary_by_rate_title, true),
    SIDE_PHOTO_SUMMARY_BY_BODY_FAT(SharedPreferencesUtil.Key.HIDE_SIDE_PHOTO_SUMMARY_BY_BODY_FAT, R.string.side_photo_summary_by_rate_title, true),
    OTHER1_PHOTO_SUMMARY_BY_BODY_FAT(SharedPreferencesUtil.Key.HIDE_OTHER1_PHOTO_SUMMARY_BY_BODY_FAT, R.string.other1_photo_summary_by_rate_title, true),
    OTHER2_PHOTO_SUMMARY_BY_BODY_FAT(SharedPreferencesUtil.Key.HIDE_OTHER2_PHOTO_SUMMARY_BY_BODY_FAT, R.string.other2_photo_summary_by_rate_title, true),
    OTHER3_PHOTO_SUMMARY_BY_BODY_FAT(SharedPreferencesUtil.Key.HIDE_OTHER3_PHOTO_SUMMARY_BY_BODY_FAT, R.string.other3_photo_summary_by_rate_title, true),
}

interface DashboardSettingContract: BaseContract {
    interface View: BaseContract.View<Presenter> {
        fun initializeSelection(selectedIndices: List<Int>)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun select(selectedIndices: List<Int>)
    }
}