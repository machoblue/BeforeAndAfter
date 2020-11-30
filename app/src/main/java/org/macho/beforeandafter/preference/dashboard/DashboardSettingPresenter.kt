package org.macho.beforeandafter.preference.dashboard

import android.content.Context
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.extensions.getBoolean
import org.macho.beforeandafter.shared.util.LogUtil
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import javax.inject.Inject

class DashboardSettingPresenter @Inject constructor(): DashboardSettingContract.Presenter {

    private var view: DashboardSettingContract.View? = null

    @Inject
    lateinit var context: Context

    private var items = mutableListOf<DashboardSettingItem>()

    override fun select(selectedIndices: List<Int>) {
        for ((i, item) in items.withIndex()) {
            val isHidden = !selectedIndices.contains(i)
            SharedPreferencesUtil.setBoolean(context, item.sharedPreferencesKey, isHidden)
        }
    }

    override fun takeView(view: DashboardSettingContract.View) {
        this.view = view

        populateItems()
        initializeSelection()
    }

    override fun dropView() {
        this.view = null
    }

    private fun populateItems() {
        items.clear()
        items.addAll(listOf(
            DashboardSettingItem(SharedPreferencesUtil.Key.HIDE_WEIGHT_PROGRESS, R.string.progress_title, false),
            DashboardSettingItem(SharedPreferencesUtil.Key.HIDE_WEIGHT_TENDENCY, R.string.weight_tendency_title, false),
            DashboardSettingItem(SharedPreferencesUtil.Key.HIDE_BMI, R.string.bmi_title, false),
            DashboardSettingItem(SharedPreferencesUtil.Key.HIDE_BODY_FAT_PROGRESS, R.string.body_fat_progress_title, true),
            DashboardSettingItem(SharedPreferencesUtil.Key.HIDE_BODY_FAT_TENDENCY, R.string.body_fat_tendency_title, true)
        ))

        if (context.getBoolean(R.bool.is_dashboard_photo_visible)) {
            items.addAll(listOf(
                DashboardSettingItem(SharedPreferencesUtil.Key.HIDE_FRONT_PHOTO_SUMMARY_BY_WEIGHT, R.string.front_photo_summary_by_weight_title, false),
                DashboardSettingItem(SharedPreferencesUtil.Key.HIDE_SIDE_PHOTO_SUMMARY_BY_WEIGHT, R.string.side_photo_summary_by_weight_title, false),
                DashboardSettingItem(SharedPreferencesUtil.Key.HIDE_OTHER1_PHOTO_SUMMARY_BY_WEIGHT, R.string.other1_photo_summary_by_weight_title, true),
                DashboardSettingItem(SharedPreferencesUtil.Key.HIDE_OTHER2_PHOTO_SUMMARY_BY_WEIGHT, R.string.other2_photo_summary_by_weight_title, true),
                DashboardSettingItem(SharedPreferencesUtil.Key.HIDE_OTHER3_PHOTO_SUMMARY_BY_WEIGHT, R.string.other3_photo_summary_by_weight_title, true),
                DashboardSettingItem(SharedPreferencesUtil.Key.HIDE_FRONT_PHOTO_SUMMARY_BY_BODY_FAT, R.string.front_photo_summary_by_rate_title, true),
                DashboardSettingItem(SharedPreferencesUtil.Key.HIDE_SIDE_PHOTO_SUMMARY_BY_BODY_FAT, R.string.side_photo_summary_by_rate_title, true),
                DashboardSettingItem(SharedPreferencesUtil.Key.HIDE_OTHER1_PHOTO_SUMMARY_BY_BODY_FAT, R.string.other1_photo_summary_by_rate_title, true),
                DashboardSettingItem(SharedPreferencesUtil.Key.HIDE_OTHER2_PHOTO_SUMMARY_BY_BODY_FAT, R.string.other2_photo_summary_by_rate_title, true),
                DashboardSettingItem(SharedPreferencesUtil.Key.HIDE_OTHER3_PHOTO_SUMMARY_BY_BODY_FAT, R.string.other3_photo_summary_by_rate_title, true)
            ))
        }

        view?.populateItems(items)
    }

    private fun initializeSelection() {
        val selectedIndices = items
                .withIndex()
                .filter { (index, item) ->
                    val isHidden = SharedPreferencesUtil.getBoolean(context, item.sharedPreferencesKey, item.defaultIsHidden)
                    return@filter !isHidden
                }
                .map { (index, _) -> index }

        view?.initializeSelection(selectedIndices)
    }
}