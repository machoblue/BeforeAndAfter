package org.macho.beforeandafter.preference.height

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.edit_height_fragment.*
import kotlinx.android.synthetic.main.edit_height_fragment.adLayout
import kotlinx.android.synthetic.main.edit_height_fragment.adView
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.extensions.hideKeyboardIfNeeded
import org.macho.beforeandafter.shared.util.AdUtil
import java.lang.RuntimeException
import javax.inject.Inject

class EditHeightFragment @Inject constructor(): DaggerFragment(), EditHeightContract.View {

    @Inject
    override lateinit var presenter: EditHeightContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.edit_height_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AdUtil.initializeMobileAds(requireContext())
        AdUtil.loadBannerAd(adView, requireContext())
        adLayout.visibility = if (AdUtil.isBannerAdHidden(requireContext())) View.GONE else View.VISIBLE

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
                val heightForm = if (heightTextInputLayout.visibility == View.VISIBLE) {
                    CentimeterForm(heightEditText.text.toString())
                } else if (feetLinearLayout.visibility == View.VISIBLE) {
                    FeetForm(feetEditText.text.toString(), inchEditText.text.toString())
                } else {
                    throw  RuntimeException("This line shouldn't be reached.")
                }
                presenter.save(heightForm)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // MARK: - EditHeightContract.View

    override fun update(heightForm: HeightForm) {
        when (heightForm) {
            is CentimeterForm -> {
                heightTextInputLayout.visibility = View.VISIBLE
                feetLinearLayout.visibility = View.GONE
                heightEditText.setText(heightForm.centimeterText)
            }

            is FeetForm -> {
                heightTextInputLayout.visibility = View.GONE
                feetLinearLayout.visibility = View.VISIBLE
                feetEditText.setText(heightForm.feetText)
                inchEditText.setText(heightForm.inchText)
            }

            else -> {
                throw RuntimeException("This line shouldn't be reached.")
            }
        }
    }

    override fun finish() {
        findNavController().popBackStack()
    }
}