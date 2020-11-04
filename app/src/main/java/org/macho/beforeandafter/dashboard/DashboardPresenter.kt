package org.macho.beforeandafter.dashboard

import org.macho.beforeandafter.shared.data.record.RecordRepository
import org.macho.beforeandafter.shared.di.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class DashboardPresenter @Inject constructor(): DashboardContract.Presenter {

    @Inject
    lateinit var recordRepository: RecordRepository

    private var view: DashboardContract.View? = null

    override fun reloadDashboard() {
    }

    override fun takeView(view: DashboardContract.View) {
        this.view = view
    }

    override fun dropView() {
        this.view = null
    }
}