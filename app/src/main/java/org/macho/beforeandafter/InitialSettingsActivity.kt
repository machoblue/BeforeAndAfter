package org.macho.beforeandafter

import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import org.macho.beforeandafter.preference.editscale.EditScaleFragment
import javax.inject.Inject

class InitialSettingsActivity @Inject constructor(): DaggerAppCompatActivity(), EditScaleFragment.EditScaleFragmentListener {
    @Inject
    lateinit var editScaleFragment: EditScaleFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.initial_settings_act)

        supportActionBar?.setTitle(R.string.initial_settings_title)

        supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, editScaleFragment).commit()
    }

    override fun onComplete() {
        setResult(RESULT_OK)
        finish()
    }
}