package org.macho.beforeandafter.preference.editgoal

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import org.macho.beforeandafter.shared.util.WeightScale
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

@ActivityScoped
class EditGoalPresenter @Inject constructor(): EditGoalContract.Presenter {

    var view: EditGoalContract.View? = null
    private lateinit var preferences: SharedPreferences

    @Inject
    lateinit var context: Context

    private lateinit var weightScale: WeightScale

    override fun takeView(view: EditGoalContract.View) {
        this.view = view

        this.weightScale = WeightScale(context)

        preferences = PreferenceManager.getDefaultSharedPreferences(context)

        val goalWeightInKg = preferences.getFloat("GOAL_WEIGHT", 0f)
        val goalWeightText = if (goalWeightInKg != 0f) {

            val goalWeight = weightScale.convertFromKg(goalWeightInKg)
            val roundedGoalWeight = (goalWeight * 100).roundToInt() / 100f
            String.format("%.2f", roundedGoalWeight)
        } else {
            ""
        }
        view.setWeightGoalText(goalWeightText, weightScale.weightUnitText)

        val goalRate = preferences.getFloat("GOAL_RATE", 0f)
        if (goalRate != 0f) {
            val roundedGoalRate = (goalRate * 100).roundToInt() / 100f
            val goalRateText = String.format("%.2f", roundedGoalRate)
            view.setRateGoalText(goalRateText)
        }

        val isCustom = SharedPreferencesUtil.getBoolean(context, SharedPreferencesUtil.Key.CUSTOMIZE_START_TIME)
        val startTime = SharedPreferencesUtil.getLong(context, SharedPreferencesUtil.Key.START_TIME)
        view.updateStartTime(isCustom, if (startTime == 0L) Date() else Date(startTime))
    }

    override fun dropView() {
        view = null
    }

    override fun saveGoal(weightGoalText: String, rateGoalText: String, isCustom: Boolean, startTime: Date) {
        val weightGoalInKg = weightGoalText.toFloatOrNull()?.let {
            weightScale.convertToKg(it)
        } ?: 0f
        preferences.edit().putFloat("GOAL_WEIGHT", weightGoalInKg).apply()

        val rateGoal = rateGoalText.toFloatOrNull() ?: 0f
        preferences.edit().putFloat("GOAL_RATE", rateGoal).apply()

        SharedPreferencesUtil.setBoolean(context, SharedPreferencesUtil.Key.CUSTOMIZE_START_TIME, isCustom)
        SharedPreferencesUtil.setLong(context, SharedPreferencesUtil.Key.START_TIME, startTime.time)
    }

    override fun back() {
        view?.finish()
    }
}