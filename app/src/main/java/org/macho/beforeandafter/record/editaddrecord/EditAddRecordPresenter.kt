package org.macho.beforeandafter.record.editaddrecord

import android.content.Context
import org.macho.beforeandafter.shared.data.record.Record
import org.macho.beforeandafter.shared.data.record.RecordRepository
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
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

    private var latestFrontImageFileName: String? = null
    private var latestSideImageFileName: String? = null
    private var latestOtherImageFileName1: String? = null
    private var latestOtherImageFileName2: String? = null
    private var latestOtherImageFileName3: String? = null

    private var latestImageFileNames: List<String?> = listOf()
        get() = listOf(latestFrontImageFileName, latestSideImageFileName, latestOtherImageFileName1, latestOtherImageFileName2, latestOtherImageFileName3)

    override fun start(date: Long) {
        tempRecord = Record()
        if (date != 0L) {
            recordRepository.getRecord(date) { record ->
                if (record == null) {
                    throw RuntimeException("record shouldn't be null.")
                }

                this.originalRecord = record
                this.tempRecord = record
                view?.showRecord(this.tempRecord)
            }

        } else {
            tempRecord.weight = SharedPreferencesUtil.getFloat(context, SharedPreferencesUtil.Key.LATEST_WEIGHT)
            tempRecord.rate = SharedPreferencesUtil.getFloat(context, SharedPreferencesUtil.Key.LATEST_RATE)
        }

        recordRepository.getRecords { records ->
            val sortedRecords = records.filter { it.date != date }.sortedBy { -it.date }
            latestFrontImageFileName = sortedRecords.firstOrNull { !it.frontImagePath.isNullOrEmpty() }?.frontImagePath
            latestSideImageFileName = sortedRecords.firstOrNull { !it.sideImagePath.isNullOrEmpty() }?.sideImagePath
            latestOtherImageFileName1 = sortedRecords.firstOrNull { !it.otherImagePath1.isNullOrEmpty() }?.otherImagePath1
            latestOtherImageFileName2 = sortedRecords.firstOrNull { !it.otherImagePath2.isNullOrEmpty() }?.otherImagePath2
            latestOtherImageFileName3 = sortedRecords.firstOrNull { !it.otherImagePath3.isNullOrEmpty() }?.otherImagePath3
        }
    }

    override fun modifyDate(date: Date) {
        tempRecord.date = date.time
        view?.showRecord(tempRecord)
    }

    override fun modifyWeight(weight: String?) {
        tempRecord.weight = max(if (weight.isNullOrEmpty()) 0f else weight.toFloat(), 0f)
        // 無限ループになるので、showRecordは呼ばない。
    }

    override fun modifyRate(rate: String?) {
        tempRecord.rate = max(if (rate.isNullOrEmpty()) 0f else rate.toFloat(), 0f)
        // 無限ループになるので、showRecordは呼ばない。
    }

    override fun modifyMemo(memo: String?) {
        tempRecord.memo = memo ?: ""
        // 無限ループになるので、showRecordは呼ばない。
    }

    override fun modifyFrontImage(frontImageFile: File?) {
        tempRecord.frontImagePath = persistFile(frontImageFile)?.name
        view?.showRecord(tempRecord)
    }

    override fun modifySideImage(sideImageFile: File?) {
        tempRecord.sideImagePath = persistFile(sideImageFile)?.name
        view?.showRecord(tempRecord)
    }

    override fun modifyOtherImage1(other1ImageFile: File?) {
        tempRecord.otherImagePath1 = persistFile(other1ImageFile)?.name
        view?.showRecord(tempRecord)
    }

    override fun modifyOtherImage2(other2ImageFile: File?) {
        tempRecord.otherImagePath2 = persistFile(other2ImageFile)?.name
        view?.showRecord(tempRecord)
    }

    override fun modifyOtherImage3(other3ImageFile: File?) {
        tempRecord.otherImagePath3 = persistFile(other3ImageFile)?.name
        view?.showRecord(tempRecord)
    }

    override fun onCameraButtonClicked(index: Int) {
        val latestImageFileName = latestImageFileNames[index]
        view?.openCamera(latestImageFileName)
    }

    override fun saveRecord() {
        val originalRecord = this.originalRecord
        if (originalRecord == null) {
            recordRepository.register(tempRecord) {
                view?.close()
            }

        } else if (originalRecord.date != tempRecord.date) {
            recordRepository.register(tempRecord) {
                deleteAndClose(originalRecord)
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
        if (oldFileName == null || oldFileName.equals(newFileName)) {
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

    override fun deleteRecord() {
        originalRecord?.let {
            deleteAndClose(it)
        }
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
        view.showRecord(tempRecord)
    }

    // NOTE: this method will be called Fragment.onDestoryView()
    override fun dropView() {
        view = null
    }
}