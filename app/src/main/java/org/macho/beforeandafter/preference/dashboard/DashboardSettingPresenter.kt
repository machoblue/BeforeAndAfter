package org.macho.beforeandafter.preference.dashboard

import javax.inject.Inject

class DashboardSettingPresenter @Inject constructor(): DashboardSettingContract.Presenter {

    private var view: DashboardSettingContract.View? = null

    override fun takeView(view: DashboardSettingContract.View) {
        this.view = view
    }

    override fun dropView() {
        this.view = null
    }
}