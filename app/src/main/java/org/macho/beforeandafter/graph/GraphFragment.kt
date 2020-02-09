package org.macho.beforeandafter.graph

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.graph_frag.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.data.RecordRepository
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.LogUtil
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import javax.inject.Inject

@ActivityScoped
class GraphFragment: DaggerFragment(), View.OnClickListener {

    override fun onClick(v: View?) {
        print("#####")
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
            list.add(DataSet(DataType.LEFT, records.map { Data(it.date, it.weight) }, Color.RED))
            list.add(DataSet(DataType.RIGHT, records.map { Data(it.date, it.rate) }, Color.BLUE))
            graphView.dataSetList = list


            graphView.invalidate()
        }

        val index = SharedPreferencesUtil.getInt(context!!, SharedPreferencesUtil.Key.GRAPH_SELECTION)
        graphView.range = GraphRange.values()[index]

        button1.setOnClickListener (this)
        button2.setOnClickListener (this)
        button3.setOnClickListener (this)

    }
}