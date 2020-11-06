package org.macho.beforeandafter.dashboard

import org.macho.beforeandafter.shared.BaseContract
import org.macho.beforeandafter.shared.data.record.Record

interface DashboardContract: BaseContract {
    interface View: BaseContract.View<Presenter> {
        fun updateDashboard(firstRecord: Record?, bestRecord: Record?, latestRecord: Record?, goalWeight: Float, currentBMI: Float)
    }
    interface Presenter: BaseContract.Presenter<View> {
        fun reloadDashboard()
    }
}