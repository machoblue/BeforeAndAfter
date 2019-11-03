package org.macho.beforeandafter.record

import org.macho.beforeandafter.shared.data.RecordRepository
import org.macho.beforeandafter.shared.di.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class RecordPresenter @Inject constructor(val recordRepository: RecordRepository): RecordContract.Presenter {

    var view: RecordContract.View? = null

    override fun takeView(view: RecordContract.View) {
        this.view = view
        loadRecords()
    }

    override fun dropView() {
        view = null
    }


    override fun loadRecords() {
        recordRepository.getRecords {records ->
            if (records.isEmpty()) {
                view?.showEmptyView()

            } else {
                view?.hideEmptyView()
                view?.showItems(records.sortedBy { - it.date }) // sort descendant
            }
        }
    }

    override fun openAddRecord() {
        view?.showAddRecordUI()
    }

    override fun openEditRecord(date: Long) {
        view?.showEditRecordUI(date)
    }

}