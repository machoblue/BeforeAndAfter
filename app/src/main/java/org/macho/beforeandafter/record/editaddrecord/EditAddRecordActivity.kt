package org.macho.beforeandafter.record.editaddrecord

import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import org.macho.beforeandafter.R
import javax.inject.Inject

class EditAddRecordActivity: DaggerAppCompatActivity() {

    @Inject
    lateinit var fragment: EditAddRecordFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_add_record_act)

        supportFragmentManager.beginTransaction().add(R.id.container, fragment).commit()
    }

}