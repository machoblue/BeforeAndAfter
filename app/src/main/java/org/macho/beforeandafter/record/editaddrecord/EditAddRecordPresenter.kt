package org.macho.beforeandafter.record.editaddrecord

import android.content.Context
import android.graphics.BitmapFactory
import org.macho.beforeandafter.record.Record
import org.macho.beforeandafter.shared.BeforeAndAfterConst
import org.macho.beforeandafter.shared.data.RecordRepository
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import java.io.File
import java.util.*
import javax.inject.Inject

class EditAddRecordPresenter @Inject constructor(val recordRepository: RecordRepository): EditAddRecordContract.Presenter {

    var view: EditAddRecordContract.View? = null

    @Inject
    lateinit var context: Context

    private lateinit var record: Record

    // 画像選択後、途中で保存をやめた時にその画像を削除できるようにするためのフィールド
    override var tempFrontImageFileName: String? = null
    override var tempSideImageFileName: String? = null

    override fun setDate(date: Long) {
        record = Record()
        if (date != 0L) {
            recordRepository.getRecord(date) { record ->
                if (record == null) {
                    throw RuntimeException("record shouldn't be null.")
                }

                this.record = record

                updateView()

                view?.showDeleteButton()
            }
        } else {
            record.weight = SharedPreferencesUtil.getFloat(context, SharedPreferencesUtil.Key.LATEST_WEIGHT)
            record.rate = SharedPreferencesUtil.getFloat(context, SharedPreferencesUtil.Key.LATEST_RATE)
        }

        tempFrontImageFileName = null
        tempSideImageFileName = null
    }

    override fun saveRecord(weight: String?, rate: String?, memo: String?) {
        record.weight = returnZeroIfEmptyOrMinus(weight)
        record.rate = returnZeroIfEmptyOrMinus(rate)
        record.memo = if (memo == null) "" else memo

        if (tempFrontImageFileName != null) {
            val oldName = record.frontImagePath
            deleteIfExists(oldName)
            record.frontImagePath = tempFrontImageFileName
            tempFrontImageFileName = null // destory時にファイルを削除するので、その時に消さないようにnullにする
        }

        if (tempSideImageFileName != null) {
            val oldName = record.sideImagePath;
            deleteIfExists(oldName)
            record.sideImagePath = tempSideImageFileName;
            tempSideImageFileName = null // destory時にファイルを削除するので、その時に消さないようにnullにする
        }

        recordRepository.getRecord(record.date) { record ->
            if (record == null) {
                this.record.date = Date().time
                recordRepository.register(this.record, null)
                view?.finish()
            } else {
                recordRepository.update(this.record, null)
                view?.finish()
            }
        }

        SharedPreferencesUtil.setFloat(context, SharedPreferencesUtil.Key.LATEST_WEIGHT, record.weight)
        SharedPreferencesUtil.setFloat(context, SharedPreferencesUtil.Key.LATEST_RATE, record.rate)
    }

    override fun deleteRecord() {
        recordRepository.delete(record.date, null)

        view?.finish()
    }

    // NOTE: this method will be called Fragment.onResume()
    override fun takeView(view: EditAddRecordContract.View) {
        this.view = view
        updateView()
    }

    // NOTE: this method will be called Fragment.onDestoryView()
    override fun dropView() {
        deleteIfExists(tempFrontImageFileName)
        deleteIfExists(tempSideImageFileName)
        view = null
    }

    override fun setMemo(memo: String?) {
        if (memo == null) {
            return
        }
        record.memo = memo
    }

    override fun setWeight(weight: String?) {
        if (weight == null || weight.isEmpty()) {
            record.weight = 0f
        } else {
            record.weight = weight.toFloat()
        }
    }

    override fun setRate(rate: String?) {
        if (rate == null || rate.isEmpty()) {
            record.rate = 0f
        } else {
            record.rate = rate.toFloat()
        }
    }

    private fun isFileExists(fileName: String?): Boolean {
        return fileName != null && File(context.filesDir, fileName).exists()
    }

    private fun updateView() {
        if (isFileExists(record.frontImagePath)) {
            view?.setFrontImage(File(context.filesDir, record.frontImagePath))
        }

        if (isFileExists(record.sideImagePath)) {
            view?.setSideImage(File(context.filesDir, record.sideImagePath))
        }

        view?.setWeight(if (record.weight == 0f) "" else "%.2f".format(record.weight))
        view?.setRate(if (record.rate == 0f) "" else "%.2f".format(record.rate))
        view?.setMemo(record.memo)
    }

    private fun returnZeroIfEmptyOrMinus(value: String?): Float {
        if (value == null || value.isEmpty()) {
            return 0f
        }
        val floatValue = value.toFloat()
        return if (floatValue < 0) 0f else floatValue
    }

    private fun deleteIfExists(fileName: String?) {
        if (fileName == null) {
            return
        }
        if (fileName.isEmpty()) {
            return
        }
        val target = File(context.filesDir, fileName)
        if (!target.exists()) {
            return
        }
        target.delete()
    }
}