package org.macho.beforeandafter.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
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
import org.macho.beforeandafter.shared.extensions.setupWithNavController
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.shared.util.Analytics
import org.macho.beforeandafter.shared.util.MailAppLauncher
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import org.macho.beforeandafter.shared.view.commondialog.CommonDialog
import java.util.*
import javax.inject.Inject

class MainActivity @Inject constructor(): DaggerAppCompatActivity(), OnRecordSavedListener, CommonDialog.CommonDialogListener, MainContract.View, PreferenceFragmentListener {

    companion object {
        const val SURVEY_DIALOG_RC = 1000
        const val STORE_REVIEW_DIALOG_RC = 1001
        const val BUG_REPORT_DIALOG_RC = 1002
        const val STORE_REVIEW_CONFIRM_DIALOG_RC = 1003
    }

    private var currentNavController: LiveData<NavController>? = null

    @Inject
    lateinit var alarmSettingDialog: AlarmSettingDialog

    @Inject
    lateinit var commonDialog: CommonDialog

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
        val navGraphIds = listOf(
                R.navigation.dashboard,
                R.navigation.graphe,
                R.navigation.gallery,
                R.navigation.records,
                R.navigation.settings
        )

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
            commonDialog.show(
                    supportFragmentManager,
                    SURVEY_DIALOG_RC,
                    getString(R.string.survey_dialog_message),
                    getString(R.string.common_yes),
                    getString(R.string.common_no))
            SharedPreferencesUtil.setLong(this, SharedPreferencesUtil.Key.LAST_SURVEY_DIALOG_TIME, Date().time)
        }, 750)
    }

    // MAKR: - CommonDialogListener
    override fun onPositiveButtonClick(requestCode: Int) {
        when (requestCode) {
            SURVEY_DIALOG_RC -> {
                commonDialog.show(
                        supportFragmentManager,
                        STORE_REVIEW_DIALOG_RC,
                        getString(R.string.store_review_dialog_message),
                        getString(R.string.common_yes),
                        getString(R.string.common_no))
                analytics.logEvent(Analytics.Event.SURVEY_DIALOG_HELP)
            }
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
            SURVEY_DIALOG_RC -> {
                commonDialog.show(
                        supportFragmentManager,
                        BUG_REPORT_DIALOG_RC,
                        getString(R.string.bug_report_dialog_message),
                        getString(R.string.common_yes),
                        getString(R.string.common_no))
                analytics.logEvent(Analytics.Event.SURVEY_DIALOG_NOT_HELP)
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
        SharedPreferencesUtil.setLong(this, SharedPreferencesUtil.Key.LAST_SURVEY_DIALOG_TIME, Date().time)
    }
}