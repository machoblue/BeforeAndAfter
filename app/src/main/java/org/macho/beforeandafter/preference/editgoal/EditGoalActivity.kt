package org.macho.beforeandafter.preference.editgoal

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import org.macho.beforeandafter.R

class EditGoalActivity: AppCompatActivity() {
    private lateinit var presenter: EditGoalContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_goal_activity)

        val fragment = EditGoalFragment()
        supportFragmentManager.beginTransaction().add(R.id.container, fragment).commit()

        presenter = EditGoalPresenter(fragment, PreferenceManager.getDefaultSharedPreferences(applicationContext))
    }

}