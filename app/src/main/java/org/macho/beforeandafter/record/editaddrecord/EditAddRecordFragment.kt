package org.macho.beforeandafter.record.editaddrecord

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.edit_add_record_frag.*
import org.macho.beforeandafter.BuildConfig
import org.macho.beforeandafter.R
import org.macho.beforeandafter.record.camera.CameraActivity
import org.macho.beforeandafter.record.camera.PermissionUtils
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.shared.util.ImageUtil
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import javax.inject.Inject

@ActivityScoped
class EditAddRecordFragment @Inject constructor() : DaggerFragment(), EditAddRecordContract.View {
    companion object {
        const val FRONT_IMAGE = 1
        const val SIDE_IMAGE = 2
        const val FRONT_IMAGE_STANDARD_CAMERA = 3
        const val SIDE_IMAGE_STANDARD_CAMERA = 4
        const val FRONT_GALLERY_IMAGE_REQUEST = 6
        const val SIDE_GALLERY_IMAGE_REQUEST = 9
        const val GALLERY_PERMISSIONS_REQUEST = 5
        const val CAMERA_PERMISSIONS_REQUEST = 7
        const val GALLERY_PERMISSIONS_REQUEST_SIDE = 10
        const val CAMERA_PERMISSIONS_REQUEST_SIDE = 11
        const val FILE_NAME_TEMPLATE = "image-%1\$tF-%1\$tH-%1\$tM-%1\$tS-%1\$tL.jpg"
    }

    @Inject
    override lateinit var presenter: EditAddRecordContract.Presenter

    private lateinit var deleteButton: Button

    private lateinit var interstitialAd: InterstitialAd

    private val onFrontImageViewClickListener = object: View.OnClickListener {
        override fun onClick(view: View?) {
            AlertDialog.Builder(context!!)
                    .setMessage(R.string.dialog_select_prompt)
                    .setPositiveButton(R.string.dialog_select_gallery) { dialog, which ->
                        startGalleryChooser(true)
                    }
                    .setNegativeButton(R.string.dialog_select_camera) { dialog, which ->
                        startCamera(true)
                    }
                    .create()
                    .show()
        }
    }

    private val onSideImageViewClickListener = object: View.OnClickListener {
        override fun onClick(view: View?) {
            AlertDialog.Builder(context!!)
                    .setMessage(R.string.dialog_select_prompt)
                    .setPositiveButton(R.string.dialog_select_gallery) { dialog, which ->
                        startGalleryChooser(false)
                    }
                    .setNegativeButton(R.string.dialog_select_camera) { dialog, which ->
                        startCamera(false)
                    }
                    .create()
                    .show()
        }
    }

    private val onCancelButtonClickListener = object: View.OnClickListener {
        override fun onClick(view: View?) {
            finish();
        }
    }

    private val onSaveButtonClickListener = object: View.OnClickListener {
        override fun onClick(view: View?) {
            presenter.saveRecord(weight.text.toString(), rate.text.toString(), memo.text.toString())
        }
    }

    private val onDeleteButtonClickListener = object: View.OnClickListener {
        override fun onClick(view: View?) {
            presenter.deleteRecord()
        }
    }


    // MARK: Lifecycle

    override fun onCreateView(layoutInflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.edit_add_record_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        frontImage.setOnClickListener(onFrontImageViewClickListener)
        sideImage.setOnClickListener(onSideImageViewClickListener)
        cancelButton.setOnClickListener(onCancelButtonClickListener)
        saveButton.setOnClickListener(onSaveButtonClickListener)

        MobileAds.initialize(context, getString(R.string.admob_app_id))

        AdUtil.loadBannerAd(adView, context!!)

        interstitialAd = InterstitialAd(context)
        AdUtil.loadInterstitialAd(interstitialAd, context!!)

        val intent = activity!!.getIntent()
        presenter.setDate(intent.getLongExtra("DATE", 0))
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
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

        when (requestCode) {
            FRONT_IMAGE -> {
                val frontImageFilePath = data?.getStringExtra("PATH") ?: return
                ImageUtil.setOrientationModifiedImageFile(frontImage, File(frontImageFilePath))
                val frontImageFileName = frontImageFilePath.replace(context!!.filesDir.toString() + "/", "")
                presenter.tempFrontImageFileName = frontImageFileName
            }
            SIDE_IMAGE -> {
                val sideImageFilePath = data?.getStringExtra("PATH") ?: return
                ImageUtil.setOrientationModifiedImageFile(sideImage, File(sideImageFilePath))
                val sideImageFileName = sideImageFilePath.replace(context!!.filesDir.toString() + "/", "")
                presenter.tempSideImageFileName = sideImageFileName

            }
            FRONT_IMAGE_STANDARD_CAMERA -> {
                val tempFile = getCameraFile()
                ImageUtil.setOrientationModifiedImageFile(frontImage, tempFile)

                val outputDir = context!!.filesDir
                val fileName = FILE_NAME_TEMPLATE.format(Date())
                FileOutputStream(File(outputDir, fileName)).use {
                    val bitmap = BitmapFactory.decodeFile(tempFile.path)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    ImageUtil.releaseBitmap(bitmap)
                }
                presenter.tempFrontImageFileName = fileName
            }
            SIDE_IMAGE_STANDARD_CAMERA -> {
                val tempFile = getCameraFile()
                ImageUtil.setOrientationModifiedImageFile(sideImage, tempFile)

                val outputDir = context!!.filesDir
                val fileName = FILE_NAME_TEMPLATE.format(Date())
                FileOutputStream(File(outputDir, fileName)).use {
                    val bitmap = BitmapFactory.decodeFile(tempFile.path)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    ImageUtil.releaseBitmap(bitmap)
                }
                presenter.tempSideImageFileName = fileName
            }
            FRONT_GALLERY_IMAGE_REQUEST -> {
                val uri = data?.getData() ?: return
                val bis = BufferedInputStream(activity!!.contentResolver.openInputStream(uri))
                val bitmap = BitmapFactory.decodeStream(bis)

                val orientation = ImageUtil.extractOrientationFromGalleryImage(context!!, uri)
                val orientationModifiedBitmap = ImageUtil.getOrientationModifiedBitmap(bitmap, orientation)

                val outputDir = context!!.filesDir
                val fileName = FILE_NAME_TEMPLATE.format(Date())
                FileOutputStream(File(outputDir, fileName)).use {
                    orientationModifiedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 ,it)
                }

                frontImage.scaleType = ImageView.ScaleType.CENTER_CROP
                frontImage.setImageBitmap(orientationModifiedBitmap)

                presenter.tempFrontImageFileName = fileName
            }
            SIDE_GALLERY_IMAGE_REQUEST -> {
                val uri = data?.getData() ?: return
                val bis = BufferedInputStream(activity!!.contentResolver.openInputStream(uri))
                val bitmap = BitmapFactory.decodeStream(bis)

                val orientation = ImageUtil.extractOrientationFromGalleryImage(context!!, uri)
                val orientationModifiedBitmap = ImageUtil.getOrientationModifiedBitmap(bitmap, orientation)

                val outputDir = context!!.filesDir
                val fileName = FILE_NAME_TEMPLATE.format(Date())
                FileOutputStream(File(outputDir, fileName)).use {
                    orientationModifiedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 ,it)
                }

                sideImage.scaleType = ImageView.ScaleType.CENTER_CROP
                sideImage.setImageBitmap(orientationModifiedBitmap)

                presenter.tempSideImageFileName = fileName
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSIONS_REQUEST -> {
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera(true)
                }
            }
            CAMERA_PERMISSIONS_REQUEST_SIDE -> {
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST_SIDE, grantResults)) {
                    startCamera(false)
                }
            }
            GALLERY_PERMISSIONS_REQUEST -> {
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser(true)
                }
            }
            GALLERY_PERMISSIONS_REQUEST_SIDE -> {
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST_SIDE, grantResults)) {
                    startGalleryChooser(false)
                }
            }
        }
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

    override fun setFrontImageBitmap(bitmap: Bitmap) {
        frontImage.setImageBitmap(bitmap)
    }

    override fun setSideImageBitmap(bitmap: Bitmap) {
        sideImage.setImageBitmap(bitmap)
    }

    override fun showDeleteButton() {
        deleteButton = Button(context)
        val params = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f)
        deleteButton.layoutParams = params
        deleteButton.text = resources.getString(R.string.delete)
        deleteButton.setOnClickListener(onDeleteButtonClickListener)
        buttonLayout.addView(deleteButton)
    }

    override fun finish() {
        AdUtil.show(interstitialAd)
        activity?.finish()
    }


    // MARK: Private method

    private fun getCameraFile(): File {
        val dir = File(context!!.filesDir, "/temp")
        if (!dir.exists()) {
            dir.mkdir()
        }
        return File(dir, "temp.jpg")
    }

    private fun startGalleryChooser(front: Boolean) {
        val requestCode = if (front) GALLERY_PERMISSIONS_REQUEST else GALLERY_PERMISSIONS_REQUEST_SIDE
        if (PermissionUtils.requestPermission(activity!!, requestCode, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT

            val createChooserIntent = Intent.createChooser(intent, "写真を選択してください。")
            val requestCode2 = if (front) FRONT_GALLERY_IMAGE_REQUEST else SIDE_GALLERY_IMAGE_REQUEST
            startActivityForResult(createChooserIntent, requestCode2)
        }
    }

    private fun startCamera(front: Boolean) {
        val requestCode = if (front) CAMERA_PERMISSIONS_REQUEST else CAMERA_PERMISSIONS_REQUEST_SIDE
        if (PermissionUtils.requestPermission(activity!!, requestCode,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)) {

            val preferences = PreferenceManager.getDefaultSharedPreferences(context!!)
            val useStandardCamera = preferences.getBoolean("USE_STANDARD_CAMERA", false)
            if (useStandardCamera) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val uri = FileProvider.getUriForFile(context!!, "${BuildConfig.APPLICATION_ID}.fileprovider", getCameraFile())
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                val requestCode2 = if (front) FRONT_IMAGE_STANDARD_CAMERA else SIDE_IMAGE_STANDARD_CAMERA
                startActivityForResult(intent, requestCode2)
            } else {
                val intent = Intent(context!!, CameraActivity::class.java)
                val requestCode2 = if (front) FRONT_IMAGE else SIDE_IMAGE
                startActivityForResult(intent, requestCode2)
            }
        }
    }
}