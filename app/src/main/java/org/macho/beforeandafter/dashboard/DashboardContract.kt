package org.macho.beforeandafter.dashboard

import org.macho.beforeandafter.dashboard.view.PhotoData
import org.macho.beforeandafter.shared.BaseContract

class PhotoSummary(val isVisible: Boolean, val titleStringResource: Int, val firstPhotoData: PhotoData?, val bestPhotoData: PhotoData?, val latestPhotoData: PhotoData?)

interface DashboardContract: BaseContract {
    interface View: BaseContract.View<Presenter> {
        fun toggleEmptyView(show: Boolean)
        fun updateWeightProgress(show: Boolean, elapsedDay: Int, firstWeight: Float?, bestWeight: Float?, latestWeight: Float?, goalWeight: Float?)
        fun updateWeightTendency(show: Boolean, oneWeekTendency: Float?, thirtyDaysTendency: Float?, oneYearTendency: Float?)
        fun updateBMI(show: Boolean, showSetHeightButton: Boolean, bmi: Float?, bmiClass: String?, idealWeight: Float?)
        fun updateBodyFatProgress(show: Boolean, elapsedDay: Int, firstBodyFat: Float?, bestBodyFat: Float?, latestBodyFat: Float?, goalBodyFat: Float?)
        fun updateBodyFatTendency(show: Boolean, oneWeekTendency: Float?, thirtyDaysTendency: Float?, oneYearTendency: Float?)
        fun updatePhotoSummaries(photoSummaries: List<PhotoSummary>)
        fun stopRefreshingIfNeeded()
    }
    interface Presenter: BaseContract.Presenter<View> {
        fun reloadDashboard()
    }
}