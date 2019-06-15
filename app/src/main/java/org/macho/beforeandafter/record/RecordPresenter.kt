package org.macho.beforeandafter.record

import org.macho.beforeandafter.data.RecordRepository
import org.macho.beforeandafter.record.RecordContract.Presenter

class RecordPresenter(val view: RecordContract.View, private val recordRepository: RecordRepository): Presenter {

    init {
        view.presenter = this
    }

    override fun start() {
        loadRecords()
    }

    override fun loadRecords() {
        recordRepository.getRecords {records ->
            view.showItems(records)
        }
    }

    override fun openAddRecord() {
        view.showAddRecordUI()
    }

    override fun openEditRecord(date: Long) {
        view.showEditRecordUI(date)
    }

}