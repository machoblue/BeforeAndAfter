package org.macho.beforeandafter.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.alarmsettingdialog.AlarmSettingDialog
import org.macho.beforeandafter.preference.PreferenceFragmentListener
import org.macho.beforeandafter.record.editaddrecord.OnRecordSavedListener
import org.macho.beforeandafter.shared.extensions.getBoolean
import org.macho.beforeandafter.shared.extensions.setupWithNavController
import org.macho.beforeandafter.shared.util.*
import org.macho.beforeandafter.shared.view.commondialog.CommonDialog
import org.macho.beforeandafter.shared.view.ratingdialog.RatingDialog
import java.util.*
import javax.inject.Inject

class MainActivity @Inject constructor(): DaggerAppCompatActivity(), OnRecordSavedListener, CommonDialog.CommonDialogListener, RatingDialog.RatingDialogListener, MainContract.View, PreferenceFragmentListener {

    companion object {
        const val STORE_REVIEW_DIALOG_RC = 1001
        const val BUG_REPORT_DIALOG_RC = 1002
        const val STORE_REVIEW_CONFIRM_DIALOG_RC = 1003
    }

    private var currentNavController: LiveData<NavController>? = null

    @Inject
    lateinit var alarmSettingDialog: AlarmSettingDialog

    @Inject
    lateinit var commonDialog: CommonDialog

    @Inject
    lateinit var ratingDialog: RatingDialog

    lateinit var analytics: Analytics

    private var isPause = false

    @Inject
    override lateinit var presenter: MainContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            setUpBottomNavigationBar()
        }

        configureAd()

        analytics = Analytics(this)
    }

    override fun onResume() {
        super.onResume()

        presenter.takeView(this)

        this.isPause = false
    }

    override fun onPause() {
        super.onPause()

        this.isPause = true
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        setUpBottomNavigationBar()
    }

    private fun setUpBottomNavigationBar() {
        val navGraphIds = mutableListOf(
                R.navigation.dashboard,
                R.navigation.graphe,
                R.navigation.records,
                R.navigation.settings
        )

        if (getBoolean(R.bool.is_gallery_visible)) {
            navGraphIds.add(2, R.navigation.gallery)
        }

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottom_nav.setupWithNavController(
                navGraphIds = navGraphIds,
                fragmentManager = supportFragmentManager,
                containerId = R.id.nav_host_container,
                intent = intent
        )

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, Observer { navController ->
            setupActionBarWithNavController(navController)
        })
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    private fun configureAd() {
        AdUtil.requestConsentInfoUpdateIfNeed(applicationContext)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    // MARK: - OnRecordSavedListener
    override fun onRecordSaved() {
        Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_SHORT).show()
        presenter.handleRecordSavedEvent()
    }

    // MARK: - MainContract.View
    override fun showAlarmSettingDialog() {
        Handler().postDelayed({
            if (isPause) return@postDelayed
            alarmSettingDialog.show(supportFragmentManager, null)
        }, 750)
    }

    override fun showSurveyDialog() {
        Handler().postDelayed({
            if (isPause) return@postDelayed
            ratingDialog.show(supportFragmentManager)
            SharedPreferencesUtil.setLong(this, SharedPreferencesUtil.Key.LAST_SURVEY_DIALOG_TIME, Date().time)
        }, 750)
    }

    // MAKR: - CommonDialogListener
    override fun onPositiveButtonClick(requestCode: Int) {
        when (requestCode) {
            STORE_REVIEW_DIALOG_RC -> {
                val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.review_url)))
                startActivity(intent)
                analytics.logEvent(Analytics.Event.STORE_REVIEW_DIALOG_OPEN_STORE)
            }
            BUG_REPORT_DIALOG_RC -> {
                MailAppLauncher().launchMailApp(this)
            }
            STORE_REVIEW_CONFIRM_DIALOG_RC -> {
                MailAppLauncher().launchMailApp(this)
            }
            else -> {
            }
        }
    }

    override fun onNegativeButtonClick(requestCode: Int) {
        when (requestCode) {
            STORE_REVIEW_DIALOG_RC -> {
                analytics.logEvent(Analytics.Event.STORE_REVIEW_DIALOG_CANCEL)
            }
            BUG_REPORT_DIALOG_RC -> {
                analytics.logEvent(Analytics.Event.BUG_REPORT_DIALOG_CANCEL)
            }
            STORE_REVIEW_CONFIRM_DIALOG_RC -> {
                val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.review_url)))
                startActivity(intent)
                analytics.logEvent(Analytics.Event.STORE_REVIEW_FRON_SETTING)
            }
            else -> {
            }
        }
    }


    override fun onStoreReviewClicked() {
        commonDialog.show(
                supportFragmentManager,
                STORE_REVIEW_CONFIRM_DIALOG_RC,
                getString(R.string.confirm_store_review_message),
                getString(R.string.mail_bug_report),
                getString(R.string.store_review))
    }

    // MARK: - RatingDialogListener

    override fun onRated(rate: Int) {
        LogUtil.d(this, "onRated: $rate")
        when (rate) {
            5 -> {
                analytics.logEvent(Analytics.Event.SURVEY_DIALOG_HELP, bundleOf("rating" to rate))
                Handler().postDelayed({ // Workaround: IllegalStateException: Fragment already added: CommonDialog. 本当はcommonDialogを使いまわさないほうがいいかも。
                    commonDialog.show(
                            supportFragmentManager,
                            STORE_REVIEW_DIALOG_RC,
                            getString(R.string.store_review_dialog_message),
                            getString(R.string.common_yes),
                            getString(R.string.common_no))
                }, 500)
            }
            4 -> {
                analytics.logEvent(Analytics.Event.SURVEY_DIALOG_HELP, bundleOf("rating" to rate))
                Handler().postDelayed({ // Workaround: IllegalStateException: Fragment already added: CommonDialog. 本当はcommonDialogを使いまわさないほうがいいかも。
                    commonDialog.show(
                            supportFragmentManager,
                            BUG_REPORT_DIALOG_RC,
                            getString(R.string.bug_report_dialog_message2),
                            getString(R.string.common_yes),
                            getString(R.string.common_no))
                }, 500)
            }
            else -> {
                analytics.logEvent(Analytics.Event.SURVEY_DIALOG_NOT_HELP)
                Handler().postDelayed({ // Workaround: IllegalStateException: Fragment already added: CommonDialog. 本当はcommonDialogを使いまわさないほうがいいかも。
                    commonDialog.show(
                            supportFragmentManager,
                            BUG_REPORT_DIALOG_RC,
                            getString(R.string.bug_report_dialog_message),
                            getString(R.string.common_yes),
                            getString(R.string.common_no))
                }, 500)
            }
        }
    }

    override fun onClose() {
        LogUtil.d(this, "onClosed")
    }
}