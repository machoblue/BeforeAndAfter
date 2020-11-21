package org.macho.beforeandafter.preference.dashboard

import android.os.Bundle
import android.view.*
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.dashboard_setting_frag.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.shared.util.LogUtil
import javax.inject.Inject

class DashboardSettingFragment @Inject constructor(): DaggerFragment(), DashboardSettingContract.View {

    @Inject
    override lateinit var presenter: DashboardSettingContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dashboard_setting_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context!!)
        val adapter = DashboardItemAdapter(context!!).also {
            it.list = DashboardCardType.values().toList()
        }
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        adapter.tracker = SelectionTracker.Builder<Long>(
                "mySelection",
                recyclerView,
                StableIdKeyProvider(recyclerView),
                DashboardItemDetailsLookup(recyclerView),
                StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
        ).build().also {
            it.addObserver(object: SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    presenter.select(it.selection.map { selectedItemId -> selectedItemId.toInt() })
                }
            })
        }

        AdUtil.initializeMobileAds(context!!)
        AdUtil.loadBannerAd(adView, context!!)
        adLayout.visibility = if (AdUtil.isBannerAdHidden(context!!)) View.GONE else View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.dropView()
    }

    override fun initializeSelection(selectedIndices: List<Int>) {
        for (index in selectedIndices) {
            LogUtil.i(this, "selectedIndex: $index")
            (recyclerView.adapter as? DashboardItemAdapter)?.tracker?.select(index.toLong())
        }
    }
}