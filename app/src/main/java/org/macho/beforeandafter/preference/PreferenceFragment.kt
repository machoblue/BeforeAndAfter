package org.macho.beforeandafter.preference

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_preference.*
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.R
import org.macho.beforeandafter.preference.backup.BackupDialog
import org.macho.beforeandafter.preference.editgoal.EditGoalActivity
import org.macho.beforeandafter.preference.restore.RestoreDialog
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil

class PreferenceFragment: androidx.fragment.app.Fragment() {
    companion object {
        fun newFragment(activity: Activity): androidx.fragment.app.Fragment {
            var fragment = PreferenceFragment()
            fragment.items.add(PreferenceItem(R.string.goal_title, R.string.goal_description) {
                val intent = Intent(activity.applicationContext, EditGoalActivity::class.java)
                activity.startActivity(intent)
            })
            fragment.items.add(PreferenceItem(R.string.delete_all_title, R.string.delete_all_description) {
                DeleteAllDialog.newInstance(activity).show(fragment.fragmentManager, "")
            })
            fragment.items.add(PreferenceItem(R.string.use_standard_camera, R.string.use_standard_camera_description) {
                UseStandardCameraDialog.newInstance(activity).show(fragment.fragmentManager, "")
            })

            if (AdUtil.isInEEA(activity.applicationContext)) {
                fragment.items.add(PreferenceItem(R.string.preference_item_change_or_revoke_consent_title, R.string.preference_item_change_or_revoke_consent_description) {
                    AdUtil.showConsentForm(activity.applicationContext)
                })
            }

            if (SharedPreferencesUtil.getBoolean(activity, SharedPreferencesUtil.Key.CAN_BACKUP_AND_RESTORE)) {
                fragment.items.add(PreferenceItem(R.string.preference_item_backup_title, R.string.preference_item_backup_description) {
                    BackupDialog.newInstance(activity).show(fragment.fragmentManager, "")
                })

                fragment.items.add(PreferenceItem(R.string.preference_item_restore_title, R.string.preference_item_restore_description) {
                    RestoreDialog.newInstance(activity).show(fragment.fragmentManager, "")
                })
            }

            return fragment
        }
    }

    private var items: MutableList<PreferenceItem> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.fragment_preference, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listView.setOnItemClickListener { adapterView, view, i, l ->
            items.get(i).action.invoke()
        }
    }

    override fun onStart() {
        super.onStart()
        listView.adapter = PreferenceAdapter(context!!, items)
    }
}