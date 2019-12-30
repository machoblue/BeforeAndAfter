package org.macho.beforeandafter.preference.restore

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.restore_frag.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.data.RecordRepository
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.shared.view.AlertDialog
import javax.inject.Inject

@ActivityScoped
class RestoreFragment @Inject constructor(): DaggerFragment(), RestoreContract.View {
    @Inject
    override lateinit var presenter: RestoreContract.Presenter

    @Inject
    lateinit var recordRepository: RecordRepository

    private lateinit var interstitialAd: InterstitialAd

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.restore_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.takeView(this)
        presenter.restore()

        progressBar.max = 100

        setHasOptionsMenu(true)

        MobileAds.initialize(context, getString(R.string.admob_app_id))

        AdUtil.loadBannerAd(adView, context!!)

        interstitialAd = InterstitialAd(context)
        AdUtil.loadInterstitialAd(interstitialAd, context!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) {
            return
        }
        presenter.result(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.dropView()
    }

    override fun finish() {
        AdUtil.show(interstitialAd)
        findNavController().popBackStack()
    }

    override fun setBackupStatusMessageTitle(title: String) {
        activity?.runOnUiThread {
            backupStatusMessageTitle.setText(title)
        }
    }

    override fun setBackupStatusMessageDescription(description: String) {
        activity?.runOnUiThread {
            backupStatusMessageDescription.setText(description)
        }
    }

    override fun setProgress(value: Int) {
        Log.i("RestoreFragment", "*** setProgress ***")
        activity?.runOnUiThread {
            progressBar.setProgress(value)
        }
    }

    override fun showAlert(title: String, description: String) {
        activity?.runOnUiThread {
            AlertDialog.newInstance(activity!!, title, description) {
                finish()
            } .show(fragmentManager!!, null)
        }
    }

    var finishButtonActive = false
    override fun setFinishButtonEnabled(enabled: Boolean) {
        finishButtonActive = enabled
        activity?.invalidateOptionsMenu()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.getItem(0).setEnabled(finishButtonActive)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.restore_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.finish -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}