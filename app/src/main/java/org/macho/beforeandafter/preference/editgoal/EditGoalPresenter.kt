package org.macho.beforeandafter.preference.editgoal

import android.content.SharedPreferences

class EditGoalPresenter(val view: EditGoalContract.View, val preferences: SharedPreferences): EditGoalContract.Presenter {

    init {
        view.presenter = this
    }

    override fun start() {
        view.setWeightGoalText(preferences.getFloat("GOAL_WEIGHT", 50f).toString())
        view.setRateGoalText(preferences.getFloat("GOAL_RATE", 20f).toString())
    }

    override fun saveGoal(weightGoalText: String, rateGoalText: String) {
        preferences.edit().putFloat("GOAL_WEIGHT", weightGoalText.toString().toFloat()).commit()
        preferences.edit().putFloat("GOAL_RATE", rateGoalText.toString().toFloat()).commit()
    }

    override fun back() {
        view.finish()
    }
}