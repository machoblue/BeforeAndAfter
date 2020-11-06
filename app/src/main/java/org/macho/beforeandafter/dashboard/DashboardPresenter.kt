package org.macho.beforeandafter.dashboard

import android.content.Context
import org.macho.beforeandafter.shared.data.record.RecordRepository
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import javax.inject.Inject

@ActivityScoped
class DashboardPresenter @Inject constructor(): DashboardContract.Presenter {

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var recordRepository: RecordRepository

    private var view: DashboardContract.View? = null

    override fun reloadDashboard() {
        reloadRecords()
    }

    override fun takeView(view: DashboardContract.View) {
        this.view = view

        reloadRecords()
    }

    override fun dropView() {
        this.view = null
    }

    // MARK: - Private
    private fun reloadRecords() {
        recordRepository.getRecords { records ->
            view?.toggleEmptyView(records.isEmpty())

            val recordsSortedByDate = records.filterNot { it.weight == 0f }.sortedBy { it.date }
            val firstRecord = recordsSortedByDate.firstOrNull()
            val latestRecord = recordsSortedByDate.lastOrNull()
            val bestRecord = records.filterNot { it.weight == 0f }.minBy { it.weight }
            val goalWeight = SharedPreferencesUtil.getFloat(context, SharedPreferencesUtil.Key.GOAL_WEIGHT)
            val showWeightSummary = !SharedPreferencesUtil.getBoolean(context, SharedPreferencesUtil.Key.HIDE_WEIGHT_SUMMARY)
            view?.updateWeightSummary(showWeightSummary, firstRecord?.weight, bestRecord?.weight, latestRecord?.weight, goalWeight)
        }
    }
}