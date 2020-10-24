package org.macho.beforeandafter.record.editaddrecord

import android.content.Intent
import android.graphics.Bitmap
import org.macho.beforeandafter.shared.BaseContract
import org.macho.beforeandafter.shared.data.record.Record
import java.io.File
import java.util.*

interface EditAddRecordContract {
    // notify event, display
    interface View: BaseContract.View<Presenter> {
        fun showRecord(record: Record?)
        fun close()
    }

    // call logic, pass value to view
    interface Presenter: BaseContract.Presenter<View> {
        fun start(date: Long)

        fun modifyDate(date: Date)
        fun modifyWeight(weight: String?)
        fun modifyRate(rate: String?)
        fun modifyMemo(memo: String?)
        fun modifyFrontImage(frontImageFile: File?)
        fun modifySideImage(sideImageFile: File?)
        fun modifyOtherImage1(other1ImageFile: File?)
        fun modifyOtherImage2(other2ImageFile: File?)
        fun modifyOtherImage3(other3ImageFile: File?)

        fun saveRecord()
        fun deleteRecord()
    }
}