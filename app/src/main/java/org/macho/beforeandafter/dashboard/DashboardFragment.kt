package org.macho.beforeandafter.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.data.record.Record
import org.macho.beforeandafter.shared.di.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class DashboardFragment @Inject constructor(): DaggerFragment(), DashboardContract.View {

    @Inject
    override lateinit var presenter: DashboardContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dashboard_frag, container, false)
    }

    // MARK: - DashboardContract.View
    override fun updateDashboard(firstRecord: Record, bestRecord: Record, latestRecord: Record, goalWeight: Float, currentBMI: Float) {
    }
}