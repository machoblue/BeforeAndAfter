package org.macho.beforeandafter.preference.editgoal

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.edit_goal_fragment.*
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.R

class EditGoalFragment: Fragment(), EditGoalContract.View {

    override lateinit var presenter: EditGoalContract.Presenter

    private lateinit var interstitialAd: InterstitialAd

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.edit_goal_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MobileAds.initialize(context, getString(R.string.admob_app_id))
        AdUtil.loadBannerAd(adView, context!!)

        interstitialAd = InterstitialAd(context)
        AdUtil.loadInterstitialAd(interstitialAd, context!!)

        cancel.setOnClickListener { view ->
            AdUtil.show(interstitialAd)

            presenter.back()
        }

        save.setOnClickListener { view ->
            // TODO: Guard
            val weightGoalText = goalWeight.text.toString()
            val rateGoalText = goalRate.text.toString()
            presenter.saveGoal(weightGoalText, rateGoalText)

            AdUtil.show(interstitialAd)

            presenter.back()
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun setWeightGoalText(weightGoalText: String) {
        goalWeight.setText(weightGoalText)
    }

    override fun setRateGoalText(rateGoalText: String) {
        goalRate.setText(rateGoalText)
    }

    override fun finish() {
        activity?.finish()
    }
}