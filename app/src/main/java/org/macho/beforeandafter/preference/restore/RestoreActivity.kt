package org.macho.beforeandafter.preference.restore

import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import org.macho.beforeandafter.R
import javax.inject.Inject

class RestoreActivity: DaggerAppCompatActivity() {

    @Inject
    lateinit var fragment: RestoreFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restore_act)

        supportFragmentManager.beginTransaction().add(R.id.container, fragment).commit()
    }
}