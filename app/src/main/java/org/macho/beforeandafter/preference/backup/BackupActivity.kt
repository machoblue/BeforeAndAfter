package org.macho.beforeandafter.preference.backup

import android.os.Bundle
import android.util.Log
import dagger.android.support.DaggerAppCompatActivity
import org.macho.beforeandafter.R
import javax.inject.Inject

class BackupActivity: DaggerAppCompatActivity() {

    @Inject
    lateinit var fragment: BackupFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.backup_act)

        supportFragmentManager.beginTransaction().add(R.id.container, fragment).commit()
    }
}