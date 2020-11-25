package org.macho.beforeandafter.preference.editgoal

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import java.util.*
import javax.inject.Inject

@ActivityScoped
class EditGoalPresenter @Inject constructor(): EditGoalContract.Presenter {

    var view: EditGoalContract.View? = null
    private lateinit var preferences: SharedPreferences

    @Inject
    lateinit var context: Context

    override fun takeView(view: EditGoalContract.View) {
        this.view = view

        preferences = PreferenceManager.getDefaultSharedPreferences(context)

        view.setWeightGoalText(preferences.getFloat("GOAL_WEIGHT", 50f).toString())
        view.setRateGoalText(preferences.getFloat("GOAL_RATE", 20f).toString())

        val isCustom = SharedPreferencesUtil.getBoolean(context, SharedPreferencesUtil.Key.CUSTOMIZE_START_TIME)
        val startTime = SharedPreferencesUtil.getLong(context, SharedPreferencesUtil.Key.START_TIME)
        view.updateStartTime(isCustom, if (startTime == 0L) Date() else Date(startTime))
    }

    override fun dropView() {
        view = null
    }

    override fun saveGoal(weightGoalText: String, rateGoalText: String, isCustom: Boolean, startTime: Date) {
        preferences.edit().putFloat("GOAL_WEIGHT", weightGoalText.toFloatOrNull() ?: 0f).apply()
        preferences.edit().putFloat("GOAL_RATE", rateGoalText.toFloatOrNull() ?: 0f).apply()
        SharedPreferencesUtil.setBoolean(context, SharedPreferencesUtil.Key.CUSTOMIZE_START_TIME, isCustom)
        SharedPreferencesUtil.setLong(context, SharedPreferencesUtil.Key.START_TIME, startTime.time)
    }

    override fun back() {
        view?.finish()
    }
}