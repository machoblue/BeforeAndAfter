package org.macho.beforeandafter.preference.backup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.InterstitialAd
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.backup_frag.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.data.RecordRepository
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.shared.util.showIfNeeded
import org.macho.beforeandafter.shared.view.AlertDialog
import javax.inject.Inject


@ActivityScoped
class BackupFragment @Inject constructor(): DaggerFragment(), BackupContract.View {
    @Inject
    override lateinit var presenter: BackupContract.Presenter

    @Inject
    lateinit var recordRepository: RecordRepository

    private var interstitialAd: InterstitialAd? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.backup_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cancelButton.setOnClickListener {
            presenter.cancelBackup()
            finish()
        }

        presenter.takeView(this)
        presenter.backup()

        progressBar.max = 100

        setHasOptionsMenu(true)

        AdUtil.initializeMobileAds(context!!)

        AdUtil.loadBannerAd(adView, context!!)
        adLayout.visibility = if (AdUtil.isBannerAdHidden(context!!)) View.GONE else View.VISIBLE


        interstitialAd = AdUtil.instantiateAndLoadInterstitialAd(context!!)
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
        interstitialAd?.showIfNeeded(context!!)
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
        Log.i("BackupFragment", "*** setProgress ***")
        activity?.runOnUiThread {
            progressBar.setProgress(value)
        }
    }

    override fun showAlert(title: String, message: String) {
        activity?.runOnUiThread {
            AlertDialog.newInstance(activity!!, title, message) {
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
        inflater.inflate(R.menu.backup_menu, menu)
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