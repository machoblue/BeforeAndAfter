package org.macho.beforeandafter.preference.dashboard

import android.content.Context
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import javax.inject.Inject

class DashboardSettingPresenter @Inject constructor(): DashboardSettingContract.Presenter {

    private var view: DashboardSettingContract.View? = null

    @Inject
    lateinit var context: Context

    override fun select(selectedIndices: List<Int>) {
        for ((i, dashboardType) in DashboardCardType.values().withIndex()) {
            val isHidden = !selectedIndices.contains(i)
            SharedPreferencesUtil.setBoolean(context, dashboardType.sharedPreferencesKey, isHidden)
        }
    }

    override fun takeView(view: DashboardSettingContract.View) {
        this.view = view

        initializeSelection()
    }

    override fun dropView() {
        this.view = null
    }

    private fun initializeSelection() {
        val selectedIndices = DashboardCardType.values()
                .withIndex()
                .filter { (index, type) ->
                    val isHidden = SharedPreferencesUtil.getBoolean(context, type.sharedPreferencesKey, type.defaultIsHidden)
                    return@filter !isHidden
                }
                .map { (index, type) -> index }

        view?.initializeSelection(selectedIndices)
    }
}