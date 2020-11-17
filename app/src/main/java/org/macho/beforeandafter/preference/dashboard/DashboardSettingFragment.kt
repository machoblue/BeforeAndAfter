package org.macho.beforeandafter.preference.dashboard

import android.os.Bundle
import android.view.*
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.edit_goal_fragment.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.util.AdUtil
import javax.inject.Inject

class DashboardSettingFragment @Inject constructor(): DaggerFragment(), DashboardSettingContract.View {
    @Inject
    override lateinit var presenter: DashboardSettingContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dashboard_setting_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AdUtil.initializeMobileAds(context!!)
        AdUtil.loadBannerAd(adView, context!!)
        adLayout.visibility = if (AdUtil.isBannerAdHidden(context!!)) View.GONE else View.VISIBLE
    }
}