package org.macho.beforeandafter.record.editaddrecord

import org.macho.beforeandafter.shared.BaseContract
import java.io.File
import java.util.*

interface EditAddRecordContract {
    // notify event, display
    interface View: BaseContract.View<Presenter> {
        fun updateViews(weightUnit: String?, date: Long?, weight: Float?, rate: Float?, memo: String?, frontImageFile: File?, sideImageFile: File?, other1ImageFile: File?, other2ImageFile: File?, other3ImageFile: File?)
        fun close()
        fun openCamera(guidePhotoFileName: String?)
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

        fun onCameraButtonClicked(index: Int)

        fun saveRecord()
        fun deleteRecord()
    }
}