package org.macho.beforeandafter.preference

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_preference.*
import org.macho.beforeandafter.shared.AdUtil
import org.macho.beforeandafter.R

class PreferenceFragment: Fragment() {
    companion object {
        fun newFragment(activity: Activity): Fragment {
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