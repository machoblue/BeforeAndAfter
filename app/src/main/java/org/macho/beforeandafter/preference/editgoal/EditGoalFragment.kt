package org.macho.beforeandafter.preference.editgoal

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.edit_goal_fragment.*
import kotlinx.android.synthetic.main.edit_goal_fragment.adView
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.di.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class EditGoalFragment @Inject constructor(): DaggerFragment(), EditGoalContract.View {

    @Inject
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

        setHasOptionsMenu(true); // for save button on navBar

        AdUtil.loadBannerAd(adView, context!!)
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.editaddrecord_menu, menu) // TODO: refactor
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                // TODO: Guard
                val weightGoalText = goalWeight.text.toString()
                val rateGoalText = goalRate.text.toString()
                presenter.saveGoal(weightGoalText, rateGoalText)

                presenter.back()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setWeightGoalText(weightGoalText: String) {
        goalWeight.setText(weightGoalText)
    }

    override fun setRateGoalText(rateGoalText: String) {
        goalRate.setText(rateGoalText)
    }

    override fun finish() {
        AdUtil.show(interstitialAd)
        findNavController().popBackStack()
    }
}