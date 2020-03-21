package org.macho.beforeandafter.record

import org.macho.beforeandafter.shared.BaseContract
import org.macho.beforeandafter.shared.data.Record

interface RecordContract {
    interface View: BaseContract.View<Presenter> {
        fun showItems(items: List<Record>)
        fun showAddRecordUI()
        fun showEditRecordUI(date: Long)
        fun showEmptyView()
        fun hideEmptyView()
        fun showReviewDialogIfNeeded()
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun loadRecords()
        fun openAddRecord()
        fun openEditRecord(date: Long)
    }
}