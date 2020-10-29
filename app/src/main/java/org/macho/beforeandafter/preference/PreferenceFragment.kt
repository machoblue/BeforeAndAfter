package org.macho.beforeandafter.preference

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_preference.*
import kotlinx.android.synthetic.main.fragment_preference.adView
import org.macho.beforeandafter.BuildConfig
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.R
import org.macho.beforeandafter.preference.pin.PinEnableActivity
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.preference.pin.PinDisableActivity
import org.macho.beforeandafter.shared.data.restoreimage.RestoreImage
import org.macho.beforeandafter.shared.data.restoreimage.RestoreImageRepository
import org.macho.beforeandafter.shared.util.MailAppLauncher
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import java.util.*
import javax.inject.Inject

@ActivityScoped
class PreferenceFragment @Inject constructor(): DaggerFragment() {
    companion object {
        const val RC_ENABLE_PIN = 4001
        const val RC_DISABLE_PIN = 4002
    }

    @Inject
    lateinit var restoreImageRepository: RestoreImageRepository

    @Inject
    lateinit var mailAppLaucher: MailAppLauncher

    private var items: MutableList<PreferenceElement> = mutableListOf()
    private lateinit var adapter: PreferenceAdapter
    private var pinItem: CheckboxPreferenceItem? = null

    private var haveWatchedAdRecently: Boolean = false
        get() {
            val elapsedTime = Date().time - SharedPreferencesUtil.getLong(activity!!, SharedPreferencesUtil.Key.LATEST_WATCH_REWARDED_AD)
            return elapsedTime < 1000 * 60 * 60 * 24
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.fragment_preference, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.items = createItems()
        listView.setOnItemClickListener { adapterView, view, i, l ->
            val item = items.get(i)
            when (item) {
                is PreferenceItem -> { item.action.invoke() }
            }
        }

        AdUtil.loadBannerAd(adView, context!!)
        adLayout.visibility = if (AdUtil.isBannerAdHidden(context!!)) View.GONE else View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        adapter = PreferenceAdapter(context!!, items)
        listView.adapter = adapter
    }

    private fun createItems(): MutableList<PreferenceElement> {
        var items = mutableListOf<PreferenceElement>()
        val activity = this.activity ?: return mutableListOf()

        // MARK: - Basic Settings
        items.add(SectionHeader(R.string.preference_section_header_base_settings))
        items.add(PreferenceItem(R.string.goal_title, R.string.goal_description) {
            val action = PreferenceFragmentDirections.actionPreferenceFragmentToEditGoalFragment()
            findNavController().navigate(action)
        })
        items.add(PreferenceItem(R.string.alarm_title, R.string.alarm_description) {
            val action = PreferenceFragmentDirections.actionPreferenceFragmentToAlarmFragment()
            findNavController().navigate(action)
        })

        // MARK: - Privacy Settings
        items.add(SectionHeader(R.string.preference_section_header_privacy))

        val enabledPIN = !SharedPreferencesUtil.getString(context!!, SharedPreferencesUtil.Key.PIN).isEmpty()
        val pinItem = CheckboxPreferenceItem(R.string.preference_pin_title,  R.string.preference_pin_description, enabledPIN) { enablePIN ->
            if (enablePIN) {
                enablePIN()
            } else {
                disablePIN()
            }
        }
        items.add(pinItem)
        this.pinItem = pinItem

        if (AdUtil.isInEEA(activity.applicationContext)) {
            items.add(PreferenceItem(R.string.preference_item_change_or_revoke_consent_title, R.string.preference_item_change_or_revoke_consent_description) {
                AdUtil.showConsentForm(activity.applicationContext)
            })
        }

        // MARK: - Data Settings
        items.add(SectionHeader(R.string.preference_section_header_data))
        items.add(PreferenceItem(R.string.preference_item_backup_title, R.string.preference_item_backup_description) {
            val action = if (haveWatchedAdRecently || AdUtil.isRewardAdHidden(context!!)) {
                PreferenceFragmentDirections.actionPreferenceFragmentToBackupDialog4()
            } else {
                PreferenceFragmentDirections.actionPreferenceFragmentToRewardDialog2()
            }
            findNavController().navigate(action)
        })
        items.add(PreferenceItem(R.string.preference_item_restore_title, R.string.preference_item_restore_description) {
            restoreImageRepository.getRestoreImages { restoreImages ->
                if (restoreImages.filter { it.status != RestoreImage.Status.COMPLETE }.isNotEmpty()) {
                    val action = PreferenceFragmentDirections.actionPreferenceFragmentToRestoreResumeDialog()
                    findNavController().navigate(action)

                } else {
                    val action = PreferenceFragmentDirections.actionPreferenceFragmentToRestoreDialog()
                    findNavController().navigate(action)
                }
            }
        })
        items.add(PreferenceItem(R.string.delete_all_title, R.string.delete_all_description) {
            DeleteAllDialog.newInstance(activity).show(fragmentManager!!, "")
        })

        items.add(SectionHeader(R.string.preference_section_header_inquiry))
        items.add(PreferenceItem(R.string.preference_inquiry_title, R.string.preference_inquiry_description) {
            mailAppLaucher.launchMailApp(context!!)
        })

        // MARK: - Version
        items.add(PreferenceFooter("ver.${BuildConfig.VERSION_NAME}"))

        return items
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            RC_ENABLE_PIN -> {
                pinItem?.isOn = true
                adapter.notifyDataSetChanged()
            }

            RC_DISABLE_PIN -> {
                pinItem?.isOn = false
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun enablePIN() {
        val intent = Intent(context!!, PinEnableActivity::class.java)
        startActivityForResult(intent, RC_ENABLE_PIN)
    }

    private fun disablePIN() {
        val intent = Intent(context!!, PinDisableActivity::class.java)
        startActivityForResult(intent, RC_DISABLE_PIN)
    }
}