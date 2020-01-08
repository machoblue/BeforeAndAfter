package org.macho.beforeandafter.preference

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.api.client.util.PemReader
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_preference.*
import kotlinx.android.synthetic.main.fragment_preference.adView
import org.macho.beforeandafter.BuildConfig
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import java.util.*
import javax.inject.Inject

@ActivityScoped
class PreferenceFragment @Inject constructor(): DaggerFragment() {

    private var items: MutableList<PreferenceElement> = mutableListOf()

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
    }

    override fun onStart() {
        super.onStart()
        listView.adapter = PreferenceAdapter(context!!, items)
    }

    private fun createItems(): MutableList<PreferenceElement> {
        var items = mutableListOf<PreferenceElement>()
        val activity = this.activity ?: return mutableListOf()

        items.add(SectionHeader(R.string.preference_section_header_base_settings))
        items.add(PreferenceItem(R.string.goal_title, R.string.goal_description) {
            val action = PreferenceFragmentDirections.actionPreferenceFragmentToEditGoalFragment()
            findNavController().navigate(action)
        })
        items.add(PreferenceItem(R.string.use_standard_camera, R.string.use_standard_camera_description) {
            UseStandardCameraDialog.newInstance(activity).show(fragmentManager!!, "")
        })



        if (AdUtil.isInEEA(activity.applicationContext)) {
            items.add(SectionHeader(R.string.preference_section_header_privacy))
            items.add(PreferenceItem(R.string.preference_item_change_or_revoke_consent_title, R.string.preference_item_change_or_revoke_consent_description) {
                AdUtil.showConsentForm(activity.applicationContext)
            })
        }

        items.add(SectionHeader(R.string.preference_section_header_data))
        items.add(PreferenceItem(R.string.preference_item_backup_title, R.string.preference_item_backup_description) {
            val action = if (haveWatchedAdRecently) PreferenceFragmentDirections.actionPreferenceFragmentToBackupDialog4() else PreferenceFragmentDirections.actionPreferenceFragmentToRewardDialog2()
            findNavController().navigate(action)
        })
        items.add(PreferenceItem(R.string.preference_item_restore_title, R.string.preference_item_restore_description) {
            val action = PreferenceFragmentDirections.actionPreferenceFragmentToRestoreDialog()
            findNavController().navigate(action)
        })
        items.add(PreferenceItem(R.string.delete_all_title, R.string.delete_all_description) {
            DeleteAllDialog.newInstance(activity).show(fragmentManager!!, "")
        })

        items.add(PreferenceFooter(BuildConfig.VERSION_NAME))

        return items
    }
}