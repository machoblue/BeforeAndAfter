package org.macho.beforeandafter.preference.alarm

import android.content.Context
import org.macho.beforeandafter.shared.di.ActivityScoped
import java.time.LocalTime
import javax.inject.Inject

@ActivityScoped
class AlarmPresenter @Inject constructor(): AlarmContract.Presenter {

    @Inject
    lateinit var context: Context

    override fun save(isAlarmEnabled: Boolean, hour: Int, minute: Int) {
        TODO("Not yet implemented")
    }

    override fun takeView(view: AlarmContract.View) {
        TODO("Not yet implemented")
    }

    override fun dropView() {
        TODO("Not yet implemented")
    }
}