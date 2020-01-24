package org.macho.beforeandafter.preference.editgoal

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.InterstitialAd
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.edit_goal_fragment.*
import kotlinx.android.synthetic.main.edit_goal_fragment.adView
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.showIfNeeded
import javax.inject.Inject

@ActivityScoped
class EditGoalFragment @Inject constructor(): DaggerFragment(), EditGoalContract.View {

    @Inject
    override lateinit var presenter: EditGoalContract.Presenter

    private var interstitialAd: InterstitialAd? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.edit_goal_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AdUtil.initializeMobileAds(context!!)
        AdUtil.loadBannerAd(adView, context!!)
        adLayout.visibility = if (AdUtil.isBannerAdHidden(context!!)) View.GONE else View.VISIBLE


        interstitialAd = AdUtil.instantiateAndLoadInterstitialAd(context!!)

        setHasOptionsMenu(true); // for save button on navBar
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        presenter.dropView()
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
        interstitialAd?.showIfNeeded(context!!)
        findNavController().popBackStack()
    }
}