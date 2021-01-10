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
import org.macho.beforeandafter.shared.data.record.RecordRepository
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.shared.util.LogUtil
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import org.macho.beforeandafter.shared.util.WeightScale
import java.text.DateFormat
import java.util.Date
import javax.inject.Inject

@ActivityScoped
class GraphFragment: DaggerFragment(), View.OnClickListener {

    private var range: GraphRange = GraphRange.THREE_WEEKS
        set(range) {
            field = range
            graphView.range = range
            graphView.invalidate()
            updateRangeLabel()

            SharedPreferencesUtil.setInt(context!!, SharedPreferencesUtil.Key.GRAPH_SELECTION, range.ordinal)
        }

    private var to: Date = Date()
        set(to) {
            field = to
            graphView.to = to
            graphView.invalidate()
            updateRangeLabel()
        }

    private val dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)

    override fun onClick(v: View?) {
        button1.isSelected = false
        button2.isSelected = false
        button3.isSelected = false

        val clickedButton = v as Button
        clickedButton.isSelected = true

        val index = (clickedButton.tag as String).toInt()
        this.range = GraphRange.values()[index]
    }

    @Inject
    lateinit var repository: RecordRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.graph_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtil.d(this, "onViewCreated")

        val weightScale = WeightScale(requireContext())
        repository.getRecords { records ->
            val list = mutableListOf<DataSet>()
            list.add(DataSet(DataType.LEFT, records.filter { it.weight != 0f }.map { Data(it.date, weightScale.convertFromKg(it.weight)) }, ContextCompat.getColor(requireContext(), R.color.colorWeight)))
            list.add(DataSet(DataType.RIGHT, records.filter {it.rate != 0f}.map { Data(it.date, it.rate) }, ContextCompat.getColor(requireContext(), R.color.colorRate)))
            graphView.dataSetList = list

            graphView.invalidate()
        }

        val index = SharedPreferencesUtil.getInt(requireContext(), SharedPreferencesUtil.Key.GRAPH_SELECTION)
        this.range = GraphRange.values()[index]

        val legends = mutableListOf<Legend>().also {
            it.add(Legend(String.format(getString(R.string.legend_weight), weightScale.weightUnitText), ContextCompat.getColor(requireContext(), R.color.colorWeight)))
            it.add(Legend(getString(R.string.legend_rate), ContextCompat.getColor(requireContext(), R.color.colorRate)))
        }
        graphView.legends = legends

        button1.isSelected = index == 0
        button2.isSelected = index == 1
        button3.isSelected = index == 2

        button1.setOnClickListener(this)
        button2.setOnClickListener(this)
        button3.setOnClickListener(this)

        previousButton.setOnClickListener {
            this.to = Date(this.to.time - range.time)
        }

        nextButton.setOnClickListener {
            this.to = Date(this.to.time + range.time)
        }

        textView.setOnClickListener {
            this.to = Date()
        }

        AdUtil.initializeMobileAds(requireContext())
        AdUtil.loadBannerAd(adView, requireContext())
        adLayout.visibility = if (AdUtil.isBannerAdHidden(requireContext())) View.GONE else View.VISIBLE
    }

    private fun updateRangeLabel() {
        val from = Date(to.time - range.time)
        textView.text = "${dateFormat.format(from)}\n 〜 ${dateFormat.format(to)}"
    }
}