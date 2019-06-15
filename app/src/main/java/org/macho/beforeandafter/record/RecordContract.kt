package org.macho.beforeandafter.record

import org.macho.beforeandafter.BaseContract

interface RecordContract {
    interface View: BaseContract.View<Presenter> {
        fun showItems(items: List<Record>)
        fun showAddRecordUI()
        fun showEditRecordUI(date: Long)
    }

    interface Presenter: BaseContract.Presenter {
        fun loadRecords()
        fun openAddRecord()
        fun openEditRecord(date: Long)
    }
}