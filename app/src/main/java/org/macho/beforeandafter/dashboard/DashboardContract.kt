package org.macho.beforeandafter.dashboard

import org.macho.beforeandafter.dashboard.view.PhotoData
import org.macho.beforeandafter.shared.BaseContract

interface DashboardContract: BaseContract {
    interface View: BaseContract.View<Presenter> {
        fun toggleEmptyView(show: Boolean)
        fun updateWeightSummary(show: Boolean, firstWeight: Float?, bestWeight: Float?, latestWeight: Float?, goalWeight: Float?)
        fun updateWeightProgress(show: Boolean, elapsedDay: Int, firstWeight: Float?, bestWeight: Float?, latestWeight: Float?, goalWeight: Float?)
        fun updateBMI(show: Boolean, showSetHeightButton: Boolean, bmi: Float?, bmiClass: String?, idealWeight: Float?)
        fun updateFrontPhotoSummaryByWeight(show: Boolean, firstPhotoData: PhotoData?, bestPhotoData: PhotoData?, latestPhotoData: PhotoData?)
        fun stopRefreshingIfNeeded()
    }
    interface Presenter: BaseContract.Presenter<View> {
        fun reloadDashboard()
    }
}