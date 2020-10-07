package org.macho.beforeandafter.record.editaddrecord

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.appcompat.app.AlertDialog
import android.view.*
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.InterstitialAd
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.edit_add_record_frag.*
import org.macho.beforeandafter.BuildConfig
import org.macho.beforeandafter.R
import org.macho.beforeandafter.record.camera.CameraActivity
import org.macho.beforeandafter.record.camera.PermissionUtils
import org.macho.beforeandafter.shared.data.record.Record
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.extensions.hideKeyboardIfNeeded
import org.macho.beforeandafter.shared.extensions.loadImage
import org.macho.beforeandafter.shared.extensions.setupClearButtonWithAction
import org.macho.beforeandafter.shared.util.*
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@ActivityScoped
class EditAddRecordFragment @Inject constructor() : DaggerFragment(), EditAddRecordContract.View {
    companion object {
        const val GALLERY_PERMISSION_RC = 1
        const val CAMERA_PERMISSION_RC = 2
        const val CUSTOM_CAMERA_RC = 3
        const val OS_CAMERA_RC = 4
        const val GALLERY_RC = 5
        const val FILE_NAME_TEMPLATE = "image-%1\$tF-%1\$tH-%1\$tM-%1\$tS-%1\$tL.jpg"
    }

    val dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)

    @Inject
    override lateinit var presenter: EditAddRecordContract.Presenter

    private var interstitialAd: InterstitialAd? = null

    val args: EditAddRecordFragmentArgs by navArgs()

    var shouldShowInterstitialAd = false

    private var currentImageView: ImageView? = null

    // MARK: Lifecycle

    override fun onCreateView(layoutInflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.edit_add_record_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        frontImage.setOnClickListener { onImageViewClick(it as ImageView) }
        sideImage.setOnClickListener { onImageViewClick(it as ImageView) }
        otherImage1.setOnClickListener { onImageViewClick(it as ImageView) }
        otherImage2.setOnClickListener { onImageViewClick(it as ImageView) }
        otherImage3.setOnClickListener { onImageViewClick(it as ImageView) }

        addImagesCheckBox.setOnClickListener { onCheckBoxClick(it as CheckBox) }

        val showOtherImages = PreferenceManager.getDefaultSharedPreferences(context!!).getBoolean("SHOW_OTHER_IMAGES", false)
        addImagesCheckBox.isChecked = showOtherImages
        otherImagesGroup.visibility = if (showOtherImages) View.VISIBLE else View.GONE

        rateUpButton.setOnClickListener {
            val rateText = rate.text.toString()
            var rateValue = if (rateText.isEmpty()) 0.0f else rateText.toFloat()
            rateValue += 0.1f
            rate.setText("%.2f".format(rateValue))
        }
        rateDownButton.setOnClickListener {
            val rateText = rate.text.toString()
            var rateValue = if (rateText.isEmpty()) 0.0f else rateText.toFloat()
            rateValue -= 0.1f
            if (rateValue >= 0) {
                rate.setText("%.2f".format(rateValue))
            }
        }
        weightUpButton.setOnClickListener {
            val weightText = weight.text.toString()
            var weightValue = if (weightText.isEmpty()) 0.0f else weightText.toFloat()
            weightValue = (weightValue * 10 + 1) / 10
            weight.setText("%.2f".format(weightValue))
        }
        weightDownButton.setOnClickListener {
            val weightText = weight.text.toString()
            var weightValue = if (weightText.isEmpty()) 0.0f else weightText.toFloat()
            weightValue = (weightValue * 10 - 1) / 10
            if (weightValue >= 0) {
                weight.setText("%.2f".format(weightValue))
            }
        }

        dateButton.setOnClickListener {
            val date = dateFormat.parse(dateButton.text.toString())
            showDatePickerDialog(date)
        }

        weight.setupClearButtonWithAction()
        rate.setupClearButtonWithAction()

        setHasOptionsMenu(true); // for save button on navBar

        deleteButton.setOnClickListener {
            presenter.deleteRecord()
        }

        AdUtil.initializeMobileAds(context!!)

        AdUtil.loadBannerAd(adView, context!!)
        adLayout.visibility = if (AdUtil.isBannerAdHidden(context!!)) View.GONE else View.VISIBLE

        val contextRef = activity!!

        interstitialAd = AdUtil.instantiateAndLoadInterstitialAd(context!!)
        interstitialAd?.adListener = object: AdListener() {
            override fun onAdClosed() {
                Toast.makeText(contextRef, R.string.interstitial_message, Toast.LENGTH_LONG).show()
            }
        }

        val timeOfLastRecord = SharedPreferencesUtil.getLong(activity!!, SharedPreferencesUtil.Key.TIME_OF_LATEST_RECORD)
        val isFirstRecord = timeOfLastRecord == 0L
        val cal1 = Calendar.getInstance().also { it.time = Date() }
        val cal2 = Calendar.getInstance().also{ it.time = Date(timeOfLastRecord)}
        val recordEveryday = cal1.get(Calendar.DATE) - cal2.get(Calendar.DATE) < 2
        shouldShowInterstitialAd = !isFirstRecord && !recordEveryday

        presenter.start(args.date)
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
    }

    override fun onStop() {
        super.onStop()
        hideKeyboardIfNeeded()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ImageUtil.releaseImageView(frontImage)
        ImageUtil.releaseImageView(sideImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        val newImageFile: File? = when (requestCode) {
            CUSTOM_CAMERA_RC -> data?.getStringExtra("PATH")?.let { File(it) }
            OS_CAMERA_RC -> {
                getCameraFile()
            }
            GALLERY_RC -> data?.data?.let { uri ->
                File(context!!.filesDir, FILE_NAME_TEMPLATE.format(Date())).also { file ->
                    saveUriToFile(uri, file)
                }
            }
            else -> null
        }

        LogUtil.i(this, "newImageFile: $newImageFile")

        currentImageView?.let { imageView ->
            when (imageView) {
                frontImage -> {
                    presenter.modifyFrontImage(newImageFile)
                }
                sideImage -> {
                    presenter.modifySideImage(newImageFile)
                }
                otherImage1 -> {
                    presenter.modifyOtherImage1(newImageFile)
                }
                otherImage2 -> {
                    presenter.modifyOtherImage2(newImageFile)
                }
                otherImage3 -> {
                    presenter.modifyOtherImage3(newImageFile)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_RC -> {
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSION_RC, grantResults)) {
                    startCamera()
                }
            }
            GALLERY_PERMISSION_RC -> {
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSION_RC, grantResults)) {
                    startGalleryChooser()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.editaddrecord_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                presenter.saveRecord(
                        weight.text.toString(),
                        rate.text.toString(),
                        memo.text.toString()
                )
                SharedPreferencesUtil.setLong(activity!!, SharedPreferencesUtil.Key.TIME_OF_LATEST_RECORD, Date().time)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // MARK: Event Listener
    private fun onImageViewClick(imageView: ImageView) {
        currentImageView = imageView
        AlertDialog.Builder(context!!)
                .setMessage(R.string.dialog_select_prompt)
                .setPositiveButton(R.string.dialog_select_gallery) { dialog, which ->
                    startGalleryChooser()
                }
                .setNegativeButton(R.string.dialog_select_camera) { dialog, which ->
                    startCamera()
                }
                .create()
                .show()
    }

    private fun onCheckBoxClick(checkBox: CheckBox) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("SHOW_OTHER_IMAGES", checkBox.isChecked).apply()
        otherImagesGroup.visibility = if (checkBox.isChecked) View.VISIBLE else View.GONE
    }

    // MARK: EditAddRecordContract.View
    override fun showRecord(record: Record?) {
        LogUtil.i(this, "showRecord: ${record?.frontImagePath}")
        record?.date?.let {
            dateButton.text = dateFormat.format(Date(it))
            dateButton.tag = Date(it)
        }

        weight.setText(record?.weight.toString())
        rate.setText(record?.rate.toString())
        memo.setText(record?.memo)

        record?.frontImageFile(context!!)?.let {
            frontImage.loadImage(this, Uri.fromFile(it))
        }
        record?.sideImageFile(context!!)?.let {
            sideImage.loadImage(this, Uri.fromFile(it))
        }
        record?.otherImageFile1(context!!)?.let {
            otherImage1.loadImage(this, Uri.fromFile(it))
        }
        record?.otherImageFile2(context!!)?.let {
            otherImage2.loadImage(this, Uri.fromFile(it))
        }
        record?.otherImageFile3(context!!)?.let {
            otherImage3.loadImage(this, Uri.fromFile(it))
        }

        deleteButton.visibility = if (args.date == 0L) View.GONE else View.VISIBLE
    }

    override fun close() {
        if (shouldShowInterstitialAd) {
            interstitialAd?.showIfNeeded(context!!)
        }
        findNavController().popBackStack()
    }

    private fun showDatePickerDialog(defaultDate: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = defaultDate
        DatePickerDialog(context, { view, year, month, dayOfMonth ->
            TimePickerDialog(context, {view, hourOfDay, minute ->
                val newCalendar = Calendar.getInstance()
                newCalendar.set(year, month, dayOfMonth, hourOfDay, minute)
                presenter.modifyDate(newCalendar.time)

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }


    // MARK: Private method

    private fun getCameraFile(): File {
        val tempDir = File(context!!.filesDir, "temp")
        if (!tempDir.exists()) {
            tempDir.mkdir()
        }

//        return File(context!!.filesDir, "temp.jpg") // これだとだめ。
        return File(tempDir, "temp.jpg")
    }

    private fun startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSION_RC, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT

            val createChooserIntent = Intent.createChooser(intent, "写真を選択してください。")
            startActivityForResult(createChooserIntent, GALLERY_RC)
        }
    }

    private fun startCamera() {
        if (PermissionUtils.requestPermission(this, CAMERA_PERMISSION_RC,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)) {

            val preferences = PreferenceManager.getDefaultSharedPreferences(context!!)
            val useStandardCamera = preferences.getBoolean("USE_STANDARD_CAMERA", false)
            if (useStandardCamera) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val uri = FileProvider.getUriForFile(context!!, "${BuildConfig.APPLICATION_ID}.fileprovider", getCameraFile())
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                startActivityForResult(intent, OS_CAMERA_RC)
            } else {
                val intent = Intent(context!!, CameraActivity::class.java)
                startActivityForResult(intent, CUSTOM_CAMERA_RC)
            }
        }
    }

    private fun saveUriToFile(uri: Uri, toFile: File) {
        BufferedInputStream(activity!!.contentResolver.openInputStream(uri)).use { bis ->
            BufferedOutputStream(FileOutputStream(toFile)).use { bos ->
                while(bis.available() > 0) {
                    bos.write(bis.read())
                }
            }
        }
    }
}
