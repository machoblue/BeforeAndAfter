package org.macho.beforeandafter.graph

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.graph_frag.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.data.Record
import org.macho.beforeandafter.shared.data.RecordRepository
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.LogUtil
import java.util.*
import javax.inject.Inject

@ActivityScoped
class GraphFragment: DaggerFragment() {
    @Inject
    lateinit var repository: RecordRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.graph_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtil.d(this, "onViewCreated")

        val records = mutableListOf<Record>()
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 21, 78f, 35f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 20, 78.5f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 19, 78f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 18, 76f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 17, 77f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 16, 78f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 15, 79f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 14, 75f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 13, 74f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 12, 75f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 11, 75f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 10, 76f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 9, 74f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 8, 76f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 7, 75f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 6, 76f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 5, 76f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 4, 75f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 3, 75f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 2, 74f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 1, 74f, 20f))
        records.add(Record(Date().time - 1000L * 60 * 60 * 24 * 0, 73f, 20f))

        val list = mutableListOf<DataSet>()
        list.add(DataSet(DataType.LEFT, records.map { Data(it.date, it.weight) }))
        list.add(DataSet(DataType.RIGHT, records.map { Data(it.date, it.rate) }))
        graphView.dataSetList = list
    }
}