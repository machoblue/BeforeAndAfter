package org.macho.beforeandafter.record.editaddrecord

import android.content.Context
import org.macho.beforeandafter.shared.data.record.Record
import org.macho.beforeandafter.shared.data.record.RecordRepository
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import org.macho.beforeandafter.shared.util.WeightScale
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.math.max

class EditAddRecordPresenter @Inject constructor(val recordRepository: RecordRepository): EditAddRecordContract.Presenter {
    companion object {
        const val FILE_NAME_TEMPLATE = "image-%1\$tF-%1\$tH-%1\$tM-%1\$tS-%1\$tL.jpg"
    }

    var view: EditAddRecordContract.View? = null

    @Inject
    lateinit var context: Context

    private var originalRecord: Record? = null
    private lateinit var tempRecord: Record

    private lateinit var weightScale: WeightScale

    override fun start(date: Long) {
        weightScale = WeightScale(context)

        tempRecord = Record()
        if (date != 0L) {
            recordRepository.getRecord(date) { record ->
                if (record == null) {
                    throw RuntimeException("record shouldn't be null.")
                }

                this.originalRecord = record
                this.tempRecord = record.copy()
                updateViews()
            }

        } else {
            originalRecord = null

            val isDefaultWeightAndBodyFatEnabled = SharedPreferencesUtil.getBoolean(context, SharedPreferencesUtil.Key.IS_DEFAULT_WEIGHT_AND_BODY_FAT_ENABLED, true)
            if (isDefaultWeightAndBodyFatEnabled) {
                tempRecord.weight = SharedPreferencesUtil.getFloat(context, SharedPreferencesUtil.Key.LATEST_WEIGHT)
                tempRecord.rate = SharedPreferencesUtil.getFloat(context, SharedPreferencesUtil.Key.LATEST_RATE)
            }
        }
    }

    override fun modifyDate(date: Date) {
        tempRecord.date = date.time
        updateViews()
    }

    override fun modifyWeight(weight: String?) {
        tempRecord.weight = weightScale.convertToKg(max(weight?.toFloatOrNull() ?: 0f, 0f))
        // 無限ループになるので、showRecordは呼ばない。
    }

    override fun modifyRate(rate: String?) {
        tempRecord.rate = max(rate?.toFloatOrNull() ?: 0f, 0f)
        // 無限ループになるので、showRecordは呼ばない。
    }

    override fun modifyMemo(memo: String?) {
        tempRecord.memo = memo ?: ""
        // 無限ループになるので、showRecordは呼ばない。
    }

    override fun modifyFrontImage(frontImageFile: File?) {
        tempRecord.frontImagePath = persistFile(frontImageFile)?.name
        updateViews()
    }

    override fun modifySideImage(sideImageFile: File?) {
        tempRecord.sideImagePath = persistFile(sideImageFile)?.name
        updateViews()
    }

    override fun modifyOtherImage1(other1ImageFile: File?) {
        tempRecord.otherImagePath1 = persistFile(other1ImageFile)?.name
        updateViews()
    }

    override fun modifyOtherImage2(other2ImageFile: File?) {
        tempRecord.otherImagePath2 = persistFile(other2ImageFile)?.name
        updateViews()
    }

    override fun modifyOtherImage3(other3ImageFile: File?) {
        tempRecord.otherImagePath3 = persistFile(other3ImageFile)?.name
        updateViews()
    }

    override fun onCameraButtonClicked(index: Int) {
        val mapper: ((Record) -> String?) = when (index) {
            0 -> { record -> record.frontImagePath }
            1 -> { record -> record.sideImagePath }
            2 -> { record -> record.otherImagePath1 }
            3 -> { record -> record.otherImagePath2 }
            4 -> { record -> record.otherImagePath3 }
            else -> { throw RuntimeException("index must not be greater than 4.") }
        }
        val guidePhotoMode = SharedPreferencesUtil.getInt(context, SharedPreferencesUtil.Key.GUIDE_PHOTO_MODE)
        val startTime = SharedPreferencesUtil.getLong(context, SharedPreferencesUtil.Key.START_TIME)
        val multiplier = if (guidePhotoMode == GuidePhotoMode.FIRST) 1 else -1
        recordRepository.getRecords { records ->
            val guidePhotoFileName = records.asSequence()
                    .filter { it.date > startTime }
                    .sortedBy { it.date * multiplier }
                    .map(mapper)
                    .firstOrNull{ it?.isNotEmpty() ?: false }
            view?.openCamera(guidePhotoFileName)
        }
    }

    override fun saveRecord() {
        val originalRecord = this.originalRecord
        if (originalRecord == null) {
            recordRepository.register(tempRecord) {
                view?.close()
            }

        } else if (originalRecord.date != tempRecord.date) {
            recordRepository.register(tempRecord) {
                recordRepository.delete(originalRecord.date) {
                    deleteOldFileIfNeed(originalRecord.frontImagePath, tempRecord.frontImagePath)
                    deleteOldFileIfNeed(originalRecord.sideImagePath, tempRecord.sideImagePath)
                    deleteOldFileIfNeed(originalRecord.otherImagePath1, tempRecord.otherImagePath1)
                    deleteOldFileIfNeed(originalRecord.otherImagePath2, tempRecord.otherImagePath2)
                    deleteOldFileIfNeed(originalRecord.otherImagePath3, tempRecord.otherImagePath3)
                    view?.close()
                }
            }

        } else {
            recordRepository.update(tempRecord) {
                deleteOldFileIfNeed(originalRecord.frontImagePath, tempRecord.frontImagePath)
                deleteOldFileIfNeed(originalRecord.sideImagePath, tempRecord.sideImagePath)
                deleteOldFileIfNeed(originalRecord.otherImagePath1, tempRecord.otherImagePath1)
                deleteOldFileIfNeed(originalRecord.otherImagePath2, tempRecord.otherImagePath2)
                deleteOldFileIfNeed(originalRecord.otherImagePath3, tempRecord.otherImagePath3)
                view?.close()
            }
        }

        SharedPreferencesUtil.setFloat(context, SharedPreferencesUtil.Key.LATEST_WEIGHT, tempRecord.weight)
        SharedPreferencesUtil.setFloat(context, SharedPreferencesUtil.Key.LATEST_RATE, tempRecord.rate)
    }

    override fun deleteRecord() {
        originalRecord?.let {
            deleteAndClose(it)
        }
    }

    private fun persistFile(file: File?): File? {
//        if (file == null || !file.exists()) {
        if (file == null) {
            return null
        }

        val newFile = File(context!!.filesDir, FILE_NAME_TEMPLATE.format(Date()))
        file.renameTo(newFile)
        return newFile
    }

    private fun deleteOldFileIfNeed(oldFileName: String?, newFileName: String?) {
        if (oldFileName == null || oldFileName == newFileName) {
            return
        }

        deleteFileIfNeeded(oldFileName)
    }

    private fun deleteFileIfNeeded(fileName: String?) {
        if (fileName == null) {
            return
        }

        val file = File(context!!.filesDir, fileName)
        if (!file.exists()) {
            return
        }

        file.delete()
    }

    private fun deleteAndClose(record: Record) {
        recordRepository.delete(record.date) {
            deleteFileIfNeeded(record.frontImagePath)
            deleteFileIfNeeded(record.sideImagePath)
            deleteFileIfNeeded(record.otherImagePath1)
            deleteFileIfNeeded(record.otherImagePath2)
            deleteFileIfNeeded(record.otherImagePath3)

            view?.close()
        }
    }

    // NOTE: this method will be called Fragment.onResume()
    override fun takeView(view: EditAddRecordContract.View) {
        this.view = view
        this.weightScale = WeightScale(context)
        updateViews()
    }

    // NOTE: this method will be called Fragment.onDestoryView()
    override fun dropView() {
        view = null
    }

    private fun updateViews() {
        view?.updateViews(
                weightScale.weightUnitText,
                tempRecord.date,
                weightScale.convertFromKg(tempRecord.weight),
                tempRecord.rate,
                tempRecord.memo,
                tempRecord.frontImageFile(context),
                tempRecord.sideImageFile(context),
                tempRecord.otherImageFile1(context),
                tempRecord.otherImageFile2(context),
                tempRecord.otherImageFile3(context)
        )
    }
}