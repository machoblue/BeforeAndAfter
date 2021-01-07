package org.macho.beforeandafter.preference.editgoal

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.InterstitialAd
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.edit_goal_fragment.*
import kotlinx.android.synthetic.main.edit_goal_fragment.adView
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.extensions.hideKeyboardIfNeeded
import org.macho.beforeandafter.shared.extensions.setTextColor
import org.macho.beforeandafter.shared.util.showDatePickerDialog
import org.macho.beforeandafter.shared.util.showIfNeeded
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@ActivityScoped
class EditGoalFragment @Inject constructor(): DaggerFragment(), EditGoalContract.View {

    @Inject
    override lateinit var presenter: EditGoalContract.Presenter

    private var interstitialAd: InterstitialAd? = null

    private val dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.edit_goal_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        startTimeSwitch.setOnCheckedChangeListener { button, isChecked ->
            updateStartTimeButton(isChecked)
        }

        startTimeButton.setOnClickListener {
            val startTimeButton = it as? Button ?: return@setOnClickListener
            val time = dateFormat.parse(startTimeButton.text.toString()) ?: return@setOnClickListener
            showDatePickerDialog(requireContext(), time) { date ->
                startTimeButton.text = dateFormat.format(date)
            }
        }

        AdUtil.initializeMobileAds(requireContext())
        AdUtil.loadBannerAd(adView, requireContext())
        adLayout.visibility = if (AdUtil.isBannerAdHidden(requireContext())) View.GONE else View.VISIBLE

        interstitialAd = AdUtil.instantiateAndLoadInterstitialAd(requireContext())

        setHasOptionsMenu(true); // for save button on navBar
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
    }

    override fun onStop() {
        super.onStop()
        hideKeyboardIfNeeded()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.dropView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                val weightGoalText = goalWeight.text.toString()
                val rateGoalText = goalRate.text.toString()
                val startTime = dateFormat.parse(startTimeButton.text.toString()) ?: Date()
                presenter.saveGoal(weightGoalText, rateGoalText, startTimeSwitch.isChecked, startTime)

                presenter.back()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // MARK: - EditGoalContract.View

    override fun setWeightGoalText(weightGoalText: String?, weightUnit: String) {
        goalWeight.setText(weightGoalText)
        goalWeightTextInputLayout.hint = String.format(requireContext().getString(R.string.goal_weight_label), weightUnit)
    }

    override fun setRateGoalText(rateGoalText: String?) {
        goalRate.setText(rateGoalText)
    }

    override fun updateStartTime(isCustom: Boolean, startTime: Date) {
        startTimeSwitch.isChecked = isCustom
        startTimeButton.text = dateFormat.format(startTime)
        updateStartTimeButton(isCustom)
    }

    override fun finish() {
        interstitialAd?.showIfNeeded(requireContext())
        findNavController().popBackStack()
    }

    // MARK: - Private
    private fun updateStartTimeButton(isCustom: Boolean) {
        startTimeButton.isEnabled = isCustom
        startTimeButton.setTextColor(requireContext(), if (isCustom) R.color.light_blue else R.color.light_gray_text)
    }
}