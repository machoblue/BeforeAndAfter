package org.macho.beforeandafter

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
import org.macho.beforeandafter.alarmsettingdialog.AlarmSettingDialog
import org.macho.beforeandafter.record.editaddrecord.OnRecordSavedListener
import org.macho.beforeandafter.shared.extensions.setupWithNavController
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.shared.util.Analytics
import org.macho.beforeandafter.shared.util.MailAppLauncher
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import org.macho.beforeandafter.shared.view.commondialog.CommonDialog
import javax.inject.Inject

class MainActivity: DaggerAppCompatActivity(), OnRecordSavedListener, CommonDialog.CommonDialogListener {

    companion object {
        const val SURVEY_DIALOG_RC = 1000
        const val STORE_REVIEW_DIALOG_RC = 1001
        const val BUG_REPORT_DIALOG_RC = 1002
    }

    private var currentNavController: LiveData<NavController>? = null

    @Inject
    lateinit var alarmSettingDialog: AlarmSettingDialog

    @Inject
    lateinit var commonDialog: CommonDialog

    lateinit var analytics: Analytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            setUpBottomNavigationBar()
        }

        configureAd()

        analytics = Analytics(this)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        setUpBottomNavigationBar()
    }

    private fun setUpBottomNavigationBar() {
        val navGraphIds = listOf(R.navigation.records, R.navigation.gallery, R.navigation.graphe, R.navigation.settings)

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

        /*
        val isAlarmEnabled = SharedPreferencesUtil.getBoolean(this, SharedPreferencesUtil.Key.ALARM_ENABLED)
        val neverDisplayAlarmSettingDialog = SharedPreferencesUtil.getBoolean(this, SharedPreferencesUtil.Key.NEVER_DISPLAY_ALARM_SETTING_DIALOG)
        if (isAlarmEnabled || neverDisplayAlarmSettingDialog) {
            return
        }

        Handler().postDelayed({
            alarmSettingDialog.show(supportFragmentManager, null)
        }, 1000)
         */
        Handler().postDelayed({
            commonDialog.show(
                    supportFragmentManager,
                    SURVEY_DIALOG_RC,
                    getString(R.string.survey_dialog_message),
                    getString(R.string.common_yes),
                    getString(R.string.common_no))
        }, 1000)
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
            }
            STORE_REVIEW_DIALOG_RC -> {
                val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.review_url)))
                startActivity(intent)
                SharedPreferencesUtil.setBoolean(this, SharedPreferencesUtil.Key.STORE_REVIEW_PROMPT_COMPLETED, true)
            }
            BUG_REPORT_DIALOG_RC -> {
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
            }
            else -> {
            }
        }
    }
}