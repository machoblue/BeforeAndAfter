package org.macho.beforeandafter.preference.editgoal

import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import org.macho.beforeandafter.R
import javax.inject.Inject

class EditGoalActivity: DaggerAppCompatActivity() {

    @Inject
    lateinit var fragment: EditGoalFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_goal_activity)

        supportFragmentManager.beginTransaction().add(R.id.container, fragment).commit()
    }

}