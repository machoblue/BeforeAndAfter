package org.macho.beforeandafter.record.editaddrecord

import android.content.Intent
import android.graphics.Bitmap
import org.macho.beforeandafter.shared.BaseContract

interface EditAddRecordContract {
    // notify event, display
    interface View: BaseContract.View<Presenter> {
        fun setWeight(value: String)
        fun setRate(value: String)
        fun setMemo(value: String)
        fun setFrontImageBitmap(bitmap: Bitmap)
        fun setSideImageBitmap(bitmap: Bitmap)
        fun showDeleteButton()
        fun finish()
    }

    // call logic, pass value to view
    interface Presenter: BaseContract.Presenter<View> {
        var tempFrontImageFileName: String?
        var tempSideImageFileName: String?
        fun setDate(date: Long)
        fun saveRecord(weight: String?, rate: String?, memo: String?)
        fun deleteRecord()
        fun setWeight(weight: String?)
        fun setRate(rate: String?)
        fun setMemo(memo: String?)
    }
}