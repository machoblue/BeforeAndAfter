package org.macho.beforeandafter.preference.editgoal

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import org.macho.beforeandafter.shared.di.ActivityScoped
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
    }

    override fun dropView() {
        view = null
    }

    override fun saveGoal(weightGoalText: String, rateGoalText: String) {
        preferences.edit().putFloat("GOAL_WEIGHT", weightGoalText.toString().toFloat()).commit()
        preferences.edit().putFloat("GOAL_RATE", rateGoalText.toString().toFloat()).commit()
    }

    override fun back() {
        view?.finish()
    }
}