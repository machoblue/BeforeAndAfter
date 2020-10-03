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
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.extensions.addTextChangedListener
import org.macho.beforeandafter.shared.extensions.hideKeyboardIfNeeded
import org.macho.beforeandafter.shared.extensions.loadImage
import org.macho.beforeandafter.shared.extensions.setupClearButtonWithAction
import org.macho.beforeandafter.shared.util.*
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
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

    @Inject
    override lateinit var presenter: EditAddRecordContract.Presenter

    private var interstitialAd: InterstitialAd? = null

    val args: EditAddRecordFragmentArgs by navArgs()

    var shouldShowInterstitialAd = false

    private val onImageViewClickListener = object: View.OnClickListener {
        override fun onClick(view: View?) {
            currentImageView = view as ImageView
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
    }

    private var currentImageView: ImageView? = null

    // MARK: Lifecycle

    override fun onCreateView(layoutInflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.edit_add_record_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        frontImage.setOnClickListener(onImageViewClickListener)
        sideImage.setOnClickListener(onImageViewClickListener)

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
            LogUtil.i(this, "dateButton.onClick")
            presenter.onDateButtonClicked()
        }

        weight.setupClearButtonWithAction()
        rate.setupClearButtonWithAction()

        setHasOptionsMenu(true); // for save button on navBar

        weight.addTextChangedListener { newText ->
            presenter.setWeight(newText)
        }

        rate.addTextChangedListener { newText ->
            presenter.setRate(newText)
        }

        memo.addTextChangedListener { newText ->
            presenter.setMemo(newText)
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

        presenter.setDate(args.date)
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

        val imageView = currentImageView ?: return

        var newImageFileName: String? = null

        when (requestCode) {
            CUSTOM_CAMERA_RC -> {
                val imageFilePath = data?.getStringExtra("PATH") ?: return
                imageView.loadImage(this, Uri.fromFile(File(imageFilePath)))
                newImageFileName  = imageFilePath.replace(context!!.filesDir.toString() + "/", "")
            }

            OS_CAMERA_RC -> {
                val toFile = File(context!!.filesDir, FILE_NAME_TEMPLATE.format(Date()))
                getCameraFile().copyTo(toFile)
                imageView.loadImage(this, Uri.fromFile(toFile))
                newImageFileName = toFile.name
            }

            GALLERY_RC -> {
                val uri = data?.data ?: return
                val toFile = File(context!!.filesDir, FILE_NAME_TEMPLATE.format(Date()))
                saveUriToFile(uri, toFile)
                imageView.loadImage(this, Uri.fromFile(toFile))
                newImageFileName = toFile.name
            }
        }

        when (currentImageView) {
            frontImage -> {
                presenter.tempFrontImageFileName = newImageFileName
            }

            sideImage -> {
                presenter.tempSideImageFileName = newImageFileName
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
                presenter.saveRecord(weight.text.toString(), rate.text.toString(), memo.text.toString())
                SharedPreferencesUtil.setLong(activity!!, SharedPreferencesUtil.Key.TIME_OF_LATEST_RECORD, Date().time)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // MARK: EditAddRecordContract.View
    override fun setWeight(value: String) {
        weight.setText(value)
    }

    override fun setRate(value: String) {
        rate.setText(value)
    }

    override fun setMemo(value: String) {
        memo.setText(value)
    }

    override fun setFrontImage(file: File) {
        frontImage.loadImage(this, Uri.fromFile(file))
    }

    override fun setSideImage(file: File) {
        sideImage.loadImage(this, Uri.fromFile(file))
    }

    override fun showDeleteButton() {
        deleteButton.visibility = View.VISIBLE
        deleteButton.setOnClickListener {
            presenter.deleteRecord()
        }
    }

    override fun setDateButtonLabel(value: String) {
        dateButton.text = value
    }

    override fun finish() {
        if (shouldShowInterstitialAd) {
            interstitialAd?.showIfNeeded(context!!)
        }
        findNavController().popBackStack()
    }

    override fun showDatePickerDialog(defaultDate: Date) {
        LogUtil.i(this, "shoDatePickerDialog $defaultDate")
        val calendar = Calendar.getInstance()
        calendar.time = defaultDate
        DatePickerDialog(context, { view, year, month, dayOfMonth ->
            TimePickerDialog(context, {view, hourOfDay, minute ->
                val newCalendar = Calendar.getInstance()
                newCalendar.set(year, month, dayOfMonth, hourOfDay, minute)
                presenter.onDateSelected(newCalendar.time)

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }


    // MARK: Private method

    private fun getCameraFile(): File {
        val dir = File(context!!.filesDir, "/temp")
        if (!dir.exists()) {
            dir.mkdir()
        }
        return File(dir, "temp_${currentImageView!!.id}.jpg")
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
