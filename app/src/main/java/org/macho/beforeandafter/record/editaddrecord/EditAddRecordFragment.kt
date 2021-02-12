
package org.macho.beforeandafter.record.editaddrecord

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
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
import androidx.fragment.app.setFragmentResultListener
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
import org.macho.beforeandafter.shared.extensions.*
import org.macho.beforeandafter.shared.util.*
import org.macho.beforeandafter.shared.view.commondialog.CommonDialog2
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

interface OnRecordSavedListener {
    fun onRecordSaved()
}

@ActivityScoped
class EditAddRecordFragment @Inject constructor() : DaggerFragment(), EditAddRecordContract.View {
    companion object {
        const val GALLERY_PERMISSION_RC = 1
        const val CAMERA_PERMISSION_RC = 2
        const val OTHER_CAMERA_PERMISSION_RC = 6
        const val CUSTOM_CAMERA_RC = 3
        const val OS_CAMERA_RC = 4
        const val GALLERY_RC = 5
        const val FILE_NAME_TEMPLATE = "image-%1\$tF-%1\$tH-%1\$tM-%1\$tS-%1\$tL.jpg"

        const val DELETE_RECORD_REQUEST_KEY = "DELETE_RECORD_REQUEST_KEY"
    }

    val dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)

    @Inject
    override lateinit var presenter: EditAddRecordContract.Presenter

    @Inject
    lateinit var commonDialog: CommonDialog2

    private var interstitialAd: InterstitialAd? = null

    val args: EditAddRecordFragmentArgs by navArgs()

    var shouldShowInterstitialAd = false

    private var currentImageIndex: Int? = null
    private lateinit var imageViews: List<ImageView>

    private var onRecordSavedListener: OnRecordSavedListener? = null


    // MARK: Lifecycle

    override fun onCreateView(layoutInflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.edit_add_record_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        presenter.start(args.date)
        childFragmentManager.setFragmentResultListener(DELETE_RECORD_REQUEST_KEY, viewLifecycleOwner) { key, bundle ->
            when(bundle.getSerializable(CommonDialog2.BUTTON_TYPE) as CommonDialog2.ButtonType) {
                CommonDialog2.ButtonType.POSITIVE -> {
                    presenter.deleteRecord()
                }
                CommonDialog2.ButtonType.NEGATIVE -> { /* do nothing */ }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onRecordSavedListener = context as? OnRecordSavedListener
    }

    override fun onResume() {
        super.onResume()

        // Workaround: Group visibility not work in onViewCreated()
        val showOtherImages = PreferenceManager.getDefaultSharedPreferences(context!!).getBoolean("SHOW_OTHER_IMAGES", false)
        addImagesCheckBox.isChecked = showOtherImages
        otherImagesGroup.visibility = if (showOtherImages) View.VISIBLE else View.GONE

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

        currentImageIndex?.let {
            when (it) {
                0 -> {
                    presenter.modifyFrontImage(newImageFile)
                }
                1 -> {
                    presenter.modifySideImage(newImageFile)
                }
                2 -> {
                    presenter.modifyOtherImage1(newImageFile)
                }
                3 -> {
                    presenter.modifyOtherImage2(newImageFile)
                }
                4 -> {
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
                    presenter.onCameraButtonClicked(currentImageIndex ?: throw RuntimeException("currentImageIndex must not be null."))
                }
            }
            OTHER_CAMERA_PERMISSION_RC -> {
                if (PermissionUtils.permissionGranted(requestCode, OTHER_CAMERA_PERMISSION_RC, grantResults)) {
                    startOtherCameraApp()
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

    override fun onPrepareOptionsMenu(menu: Menu) {
        val isRecordNew = args.date == 0L
        menu.findItem(R.id.delete).isVisible = !isRecordNew

        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                presenter.saveRecord()
                SharedPreferencesUtil.setLong(activity!!, SharedPreferencesUtil.Key.TIME_OF_LATEST_RECORD, Date().time)
            }
            R.id.delete -> {
                commonDialog.show(childFragmentManager, DELETE_RECORD_REQUEST_KEY, getString(R.string.delete_record_message), getString(R.string.ok), getString(R.string.cancel))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // MARK: Event Listener
    private fun onImageViewClick(imageView: ImageView) {
        currentImageIndex = imageViews.indexOf(imageView)
        AlertDialog.Builder(context!!)
                .setMessage(R.string.dialog_select_prompt)
                .setPositiveButton(R.string.dialog_select_gallery) { dialog, which ->
                    startGalleryChooser()
                }
                .setNeutralButton(R.string.dialog_select_other_camera_app) { dialog, which ->
                    startOtherCameraApp()
                }
                .setNegativeButton(R.string.dialog_select_camera) { dialog, which ->
                    presenter.onCameraButtonClicked(currentImageIndex ?: throw RuntimeException("currentImageIndex must not be null."))
                }
                .create()
                .show()
    }

    private fun onCheckBoxClick(checkBox: CheckBox) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("SHOW_OTHER_IMAGES", checkBox.isChecked).apply()
        otherImagesGroup.visibility = if (checkBox.isChecked) View.VISIBLE else View.GONE
    }

    // MARK: EditAddRecordContract.View
    override fun updateViews(weightUnit: String?, date: Long?, weight: Float?, rate: Float?, memo: String?, frontImageFile: File?, sideImageFile: File?, other1ImageFile: File?, other2ImageFile: File?, other3ImageFile: File?) {
        if (!isAdded) {
            return // Workaround: IllegalStateException dateButton must not be null https://www.vvzixun.com/index.php/code/35ff970b286785750654dd580d0d491a
        }

        if (view == null) {
            return // Workaround: IllegalStateException dateButton must not be null. https://stackoverflow.com/a/19690491/8834586
        }

        date?.let {
            dateButton.text = dateFormat.format(Date(it))
            dateButton.tag = Date(it)
        }

        weightEditText.setText(if (weight == 0f) null else weight?.let { String.format("%.2f", it) })
        rateEditText.setText(if (rate == 0f) null else rate?.let { String.format("%.2f", it) })
        memoEditText.setText(memo)

        frontImageFile?.let {
            frontImage.loadImage(this, Uri.fromFile(it))
        }
        sideImageFile?.let {
            sideImage.loadImage(this, Uri.fromFile(it))
        }
        other1ImageFile?.let {
            otherImage1.loadImage(this, Uri.fromFile(it))
        }
        other2ImageFile?.let {
            otherImage2.loadImage(this, Uri.fromFile(it))
        }
        other3ImageFile?.let {
            otherImage3.loadImage(this, Uri.fromFile(it))
        }

        weightTextInputLayout.hint = String.format(context!!.getString(R.string.weight_label), weightUnit)
    }

    override fun close() {
        // Workaround: IllegalStateException: Fragment EditAddRecordFragment ... not associated with a fragment manager.
        if (!isAdded) {
            return
        }

        if (shouldShowInterstitialAd) {
            interstitialAd?.showIfNeeded(context!!)
        }
        findNavController().popBackStack()
        onRecordSavedListener?.onRecordSaved()
    }

    private fun showDatePickerDialog(defaultDate: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = defaultDate
        DatePickerDialog(context!!, { view, year, month, dayOfMonth ->
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

    override fun openCamera(guidePhotoFileName: String?) {
        if (PermissionUtils.requestPermission(this, CAMERA_PERMISSION_RC,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)) {

            val intent = Intent(context!!, CameraActivity::class.java).also {
                val guidePhotoFileName = guidePhotoFileName ?: return@also
                it.putExtras(Bundle().also { bundle ->
                    bundle.putString(CameraActivity.GUIDE_PHOTO_FILE_NAME, guidePhotoFileName)
                })
            }

            startActivityForResult(intent, CUSTOM_CAMERA_RC)
        }
    }

    private fun startOtherCameraApp() {
        if (PermissionUtils.requestPermission(this, OTHER_CAMERA_PERMISSION_RC,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)) {

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val uri = FileProvider.getUriForFile(context!!, "${BuildConfig.APPLICATION_ID}.fileprovider", getCameraFile())
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(intent, OS_CAMERA_RC)
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

    private fun initViews() {
        imageViews = listOf(frontImage, sideImage, otherImage1, otherImage2, otherImage3)
        imageViews.forEach { imageView ->
            imageView.setOnClickListener { onImageViewClick(imageView) }
        }

        addImagesCheckBox.setOnClickListener { onCheckBoxClick(it as CheckBox) }

        photoGroup.visibility = if (requireContext().getBoolean(R.bool.is_editaddrecord_photo_visible)) View.VISIBLE else View.GONE

        rateUpButton.setOnClickListener {
            val rateText = rateEditText.text.toString()
            var rateValue = if (rateText.isEmpty()) 0.0f else rateText.toFloat()
            rateValue += 0.1f
            rateEditText.setText("%.2f".format(rateValue))
        }
        rateDownButton.setOnClickListener {
            val rateText = rateEditText.text.toString()
            var rateValue = if (rateText.isEmpty()) 0.0f else rateText.toFloat()
            rateValue -= 0.1f
            if (rateValue >= 0) {
                rateEditText.setText("%.2f".format(rateValue))
            }
        }
        weightUpButton.setOnClickListener {
            val weightText = weightEditText.text.toString()
            var weightValue = if (weightText.isEmpty()) 0.0f else weightText.toFloat()
            weightValue = (weightValue * 10 + 1) / 10
            weightEditText.setText("%.2f".format(weightValue))
        }
        weightDownButton.setOnClickListener {
            val weightText = weightEditText.text.toString()
            var weightValue = if (weightText.isEmpty()) 0.0f else weightText.toFloat()
            weightValue = (weightValue * 10 - 1) / 10
            if (weightValue >= 0) {
                weightEditText.setText("%.2f".format(weightValue))
            }
        }

        dateButton.setOnClickListener {
            val date = dateFormat.parse(dateButton.text.toString())
            showDatePickerDialog(date)
        }

        weightEditText.setupClearButtonWithAction()
        weightEditText.addTextChangedListener { newText -> presenter.modifyWeight(newText) }

        rateEditText.setupClearButtonWithAction()
        rateEditText.addTextChangedListener { newText -> presenter.modifyRate(newText) }

        memoEditText.addTextChangedListener { newText -> presenter.modifyMemo(newText) }

        setHasOptionsMenu(true); // for save button on navBar

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
    }
}
