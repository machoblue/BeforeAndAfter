package org.macho.beforeandafter.preference.editgoal

import org.macho.beforeandafter.shared.BaseContract

interface EditGoalContract {
    interface View: BaseContract.View<Presenter> {
        fun setWeightGoalText(weightGoalText: String)
        fun setRateGoalText(RateGoalText: String)
        fun finish()
    }

    interface Presenter: BaseContract.Presenter {
        fun saveGoal(weightGoalText: String, rateGoalText: String)
        fun back()
    }
}