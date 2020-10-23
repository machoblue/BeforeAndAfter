package org.macho.beforeandafter.preference.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import org.macho.beforeandafter.shared.di.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class AlarmFragment @Inject constructor(): DaggerFragment(), AlarmContract.View {
    @Inject
    override lateinit var presenter: AlarmContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    // MARK: - AlarmContract.View
    override fun updateView() {
    }

    override fun back() {
    }
}