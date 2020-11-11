package org.macho.beforeandafter.dashboard

import android.content.Context
import org.macho.beforeandafter.shared.data.record.RecordRepository
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import java.time.ZonedDateTime
import java.util.*
import javax.inject.Inject
import kotlin.math.pow

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

            val showWeightProgress = !SharedPreferencesUtil.getBoolean(context, SharedPreferencesUtil.Key.HIDE_WEIGHT_PROGRESS)
            val elapsedDay: Int = if (firstRecord == null) {
                1
            } else {
                val firstCalendar = Calendar.getInstance().also {
                    it.time = Date(firstRecord.date)
                    it.set(Calendar.HOUR_OF_DAY, 0)
                    it.set(Calendar.MINUTE, 0)
                    it.set(Calendar.SECOND, 0)
                    it.set(Calendar.MILLISECOND, 0)
                }
                ((Date().time - firstCalendar.time.time) / (1000 * 60 * 60 * 24) + 1).toInt()
            }
            view?.updateWeightProgress(showWeightProgress, elapsedDay, firstRecord?.weight, bestRecord?.weight, latestRecord?.weight, goalWeight)

            val showBMI = !SharedPreferencesUtil.getBoolean(context, SharedPreferencesUtil.Key.HIDE_BMI)
            val height = SharedPreferencesUtil.getFloat(context, SharedPreferencesUtil.Key.HEIGHT)
            val showSetHeightButton = height == 0f
            val isLatestWeightBlank = (latestRecord == null) || latestRecord.weight == 0f

            if (showSetHeightButton || isLatestWeightBlank) {
                view?.updateBMI(showBMI, showSetHeightButton, null, null, null)

            } else {
                val bmi = (latestRecord!!.weight / (height / 100.0).pow(2.0)).toFloat()
                val bmiClass = context.getString(BMIClass.getBMIClass(bmi).labelRes)
                val idealWeight = ((height / 100.0).pow(2.0) * 22).toFloat()
                view?.updateBMI(showBMI, showSetHeightButton, bmi, bmiClass, idealWeight)
            }
        }
    }
}