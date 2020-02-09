package org.macho.beforeandafter.graph

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.graph_frag.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.data.RecordRepository
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.shared.util.LogUtil
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import javax.inject.Inject

@ActivityScoped
class GraphFragment: DaggerFragment(), View.OnClickListener {

    override fun onClick(v: View?) {
        button1.isSelected = false
        button2.isSelected = false
        button3.isSelected = false

        val clickedButton = v as Button
        clickedButton.isSelected = true
        val index = (clickedButton.tag as String).toInt()
        graphView.range = GraphRange.values()[index]
        graphView.invalidate()
        SharedPreferencesUtil.setInt(context!!, SharedPreferencesUtil.Key.GRAPH_SELECTION, index)
    }

    @Inject
    lateinit var repository: RecordRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.graph_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtil.d(this, "onViewCreated")

        /*
        val records = mutableListOf<Record>()
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 21, 78f, 35f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 20, 78.5f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 19, 78f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 18, 76f, 22f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 17, 77f, 28f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 16, 78f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 15, 79f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 14, 75f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 13, 74f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 12, 75f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 11, 75f, 22f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 10, 76f, 21f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 9, 74f, 23f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 8, 76f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 7, 75f, 24f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 6, 76f, 22f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 5, 76f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 4, 75f, 23f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 3, 75f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 2, 74f, 21f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 1, 74f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 0, 73f, 22.2f))
         */

        repository.getRecords { records ->
            val list = mutableListOf<DataSet>()
            list.add(DataSet(DataType.LEFT, records.filter { it.weight != 0f }.map { Data(it.date, it.weight) }, ContextCompat.getColor(context!!, R.color.colorWeight)))
            list.add(DataSet(DataType.RIGHT, records.filter {it.rate != 0f}.map { Data(it.date, it.rate) }, ContextCompat.getColor(context!!, R.color.colorRate)))
            graphView.dataSetList = list

            graphView.invalidate()
        }

        val index = SharedPreferencesUtil.getInt(context!!, SharedPreferencesUtil.Key.GRAPH_SELECTION)
        graphView.range = GraphRange.values()[index]

        button1.isSelected = index == 0
        button2.isSelected = index == 1
        button3.isSelected = index == 2

        button1.setOnClickListener(this)
        button2.setOnClickListener(this)
        button3.setOnClickListener(this)

        AdUtil.initializeMobileAds(context!!)
        AdUtil.loadBannerAd(adView, context!!)
        adLayout.visibility = if (AdUtil.isBannerAdHidden(context!!)) View.GONE else View.VISIBLE
    }
}