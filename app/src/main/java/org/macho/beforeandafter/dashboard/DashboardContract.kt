package org.macho.beforeandafter.dashboard

import org.macho.beforeandafter.shared.BaseContract
import org.macho.beforeandafter.shared.data.record.Record

interface DashboardContract: BaseContract {
    interface View: BaseContract.View<Presenter> {
        fun toggleEmptyView(show: Boolean)
        fun updateWeightSummary(show: Boolean, firstWeight: Float?, bestWeight: Float?, latestWeight: Float?, goalWeight: Float?)
    }
    interface Presenter: BaseContract.Presenter<View> {
        fun reloadDashboard()
    }
}