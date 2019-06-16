package org.macho.beforeandafter.record

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
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_edit.*
import org.macho.beforeandafter.*
import org.macho.beforeandafter.record.camera.CameraActivity
import org.macho.beforeandafter.record.camera.PermissionUtils
import org.macho.beforeandafter.shared.BeforeAndAfterConst
import org.macho.beforeandafter.shared.data.RecordDao
import org.macho.beforeandafter.shared.data.RecordDaoImpl
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.shared.util.ImageUtil
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

class EditActivity: AppCompatActivity() {
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

    private lateinit var deleteButton: Button

    private var date = 0L

    private var tempFrontImageFileName: String? = null
    private var tempSideImageFileName: String? = null

    private lateinit var record: Record


    private lateinit var interstitialAd: InterstitialAd

    private var recordDao: RecordDao = RecordDaoImpl() // TODO: take from Dagger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        frontImage.setOnClickListener(onFrontImageViewClickListener)
        sideImage.setOnClickListener(onSideImageViewClickListener)
        cancelButton.setOnClickListener(onCancelButtonClickListener)
        saveButton.setOnClickListener(onSaveButtonClickListener)

        val intent = getIntent()
        date = intent.getLongExtra("DATE", 0)
        if (date != 0L) {
            record = recordDao.find(date)!!
            if (record.frontImagePath != null && File(BeforeAndAfterConst.PATH, record.frontImagePath).exists()) { openFileInput(record.frontImagePath).use {
                    val frontBitmap = BitmapFactory.decodeStream(it)
                    frontImage.setImageBitmap(frontBitmap)
                }
            }
            if (record.sideImagePath != null && File(BeforeAndAfterConst.PATH, record.sideImagePath).exists()) {
                openFileInput(record.sideImagePath).use {
                    val sideBitmap = BitmapFactory.decodeStream(it)
                    sideImage.setImageBitmap(sideBitmap)
                }
            }

            weight.setText("%.2f".format(record.weight))
            rate.setText("%.2f".format(record.rate))
            memo.setText(record.memo)

            deleteButton = Button(this)
            val params = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f)
            deleteButton.layoutParams = params
            deleteButton.text = resources.getString(R.string.delete)
            deleteButton.setOnClickListener(onDeleteButtonClickListener)
            buttonLayout.addView(deleteButton)

        } else {
            record = Record()
        }

        tempFrontImageFileName = null
        tempSideImageFileName = null

        MobileAds.initialize(this, getString(R.string.admob_app_id))

        AdUtil.loadBannerAd(adView, applicationContext)

        interstitialAd = InterstitialAd(this)
        AdUtil.loadInterstitialAd(interstitialAd, applicationContext)
    }

    private val onFrontImageViewClickListener = object: View.OnClickListener {
        override fun onClick(view: View?) {
            AlertDialog.Builder(this@EditActivity)
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
            AlertDialog.Builder(this@EditActivity)
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

    private fun startGalleryChooser(front: Boolean) {
        val requestCode = if (front) GALLERY_PERMISSIONS_REQUEST else GALLERY_PERMISSIONS_REQUEST_SIDE
        if (PermissionUtils.requestPermission(this, requestCode, Manifest.permission.READ_EXTERNAL_STORAGE)) {
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
        if (PermissionUtils.requestPermission(this, requestCode,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)) {

            val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val useStandardCamera = preferences.getBoolean("USE_STANDARD_CAMERA", false)
            if (useStandardCamera) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val uri = FileProvider.getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.fileprovider", getCameraFile())
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                val requestCode2 = if (front) FRONT_IMAGE_STANDARD_CAMERA else SIDE_IMAGE_STANDARD_CAMERA
                startActivityForResult(intent, requestCode2)
            } else {
                val intent = Intent(this@EditActivity, CameraActivity::class.java)
                val requestCode2 = if (front) FRONT_IMAGE else SIDE_IMAGE
                startActivityForResult(intent, requestCode2)
            }
        }
    }

    private val onCancelButtonClickListener = object: View.OnClickListener {
        override fun onClick(view: View?) {
            val intent = Intent();
            intent.putExtra("TYPE", 0);
            setResult(RESULT_OK, intent);

            AdUtil.show(interstitialAd);

            finish();
        }
    }

    private val onSaveButtonClickListener = object: View.OnClickListener {
        override fun onClick(view: View?) {
            val intent = Intent()
            record.weight = returnZeroIfNeed(weight)
            record.rate = returnZeroIfNeed(rate)
            record.memo = memo.getText().toString()

            if (tempFrontImageFileName != null) {
                val oldName = record.frontImagePath
                deleteIfNeed(oldName)
                record.frontImagePath = tempFrontImageFileName
                tempFrontImageFileName = null
            }

            if (tempSideImageFileName != null) {
                val oldName = record.sideImagePath;
                deleteIfNeed(oldName);
                record.sideImagePath = tempSideImageFileName;
                tempSideImageFileName = null;
            }

            if (date != 0L) {
                recordDao.update(record);
                intent.putExtra("ISNEW", false);
            } else {
                record.date = Date().time;
                recordDao.register(record);
                intent.putExtra("ISNEW", true);
                intent.putExtra("DATE", record.date);
            }

            intent.putExtra("TYPE", 2);
            setResult(RESULT_OK, intent);

            AdUtil.show(interstitialAd);

            finish();
        }
    }

    private fun returnZeroIfNeed(text: TextView): Float {
        if (text.text == null || text.text.toString().isEmpty()) {
            return 0f
        }
        val value = text.text.toString().toFloat()
        return if (value < 0) 0f else value
    }

    private val onDeleteButtonClickListener = object: View.OnClickListener {
        override fun onClick(view: View?) {
            recordDao.delete(record.date)
            val intent = Intent()
            intent.putExtra("TYPE", 1)
            setResult(Activity.RESULT_OK, intent)

            AdUtil.show(interstitialAd)

            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }


        when (requestCode) {
            FRONT_IMAGE -> {
                val frontImageFilePath = data?.getStringExtra("PATH") ?: return
                ImageUtil.setOrientationModifiedImageFile(frontImage, File(frontImageFilePath))
                val frontImageFileName = frontImageFilePath.replace(applicationContext.filesDir.toString() + "/", "")
                tempFrontImageFileName = frontImageFileName
            }
            SIDE_IMAGE -> {
                val sideImageFilePath = data?.getStringExtra("PATH") ?: return
                ImageUtil.setOrientationModifiedImageFile(sideImage, File(sideImageFilePath))
                val sideImageFileName = sideImageFilePath.replace(applicationContext.filesDir.toString() + "/", "")
                tempSideImageFileName = sideImageFileName

            }
            FRONT_IMAGE_STANDARD_CAMERA -> {
                val tempFile = getCameraFile()
                ImageUtil.setOrientationModifiedImageFile(frontImage, tempFile)

                val outputDir = applicationContext.filesDir
                val fileName = FILE_NAME_TEMPLATE.format(Date())
                FileOutputStream(File(outputDir, fileName)).use {
                    val bitmap = BitmapFactory.decodeFile(tempFile.path)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    ImageUtil.releaseBitmap(bitmap)
                }
                tempFrontImageFileName = fileName
            }
            SIDE_IMAGE_STANDARD_CAMERA -> {
                val tempFile = getCameraFile()
                ImageUtil.setOrientationModifiedImageFile(sideImage, tempFile)

                val outputDir = applicationContext.filesDir
                val fileName = FILE_NAME_TEMPLATE.format(Date())
                FileOutputStream(File(outputDir, fileName)).use {
                    val bitmap = BitmapFactory.decodeFile(tempFile.path)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    ImageUtil.releaseBitmap(bitmap)
                }
                tempSideImageFileName = fileName
            }
            FRONT_GALLERY_IMAGE_REQUEST -> {
                val uri = data?.getData() ?: return
                val bis = BufferedInputStream(contentResolver.openInputStream(uri))
                val bitmap = BitmapFactory.decodeStream(bis)

                val orientation = ImageUtil.extractOrientationFromGalleryImage(applicationContext, uri)
                val orientationModifiedBitmap = ImageUtil.getOrientationModifiedBitmap(bitmap, orientation)

                val outputDir = applicationContext.filesDir
                val fileName = FILE_NAME_TEMPLATE.format(Date())
                FileOutputStream(File(outputDir, fileName)).use {
                    orientationModifiedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 ,it)
                }

                frontImage.scaleType = ImageView.ScaleType.CENTER_CROP
                frontImage.setImageBitmap(orientationModifiedBitmap)

                tempFrontImageFileName = fileName
            }
            SIDE_GALLERY_IMAGE_REQUEST -> {
                val uri = data?.getData() ?: return
                val bis = BufferedInputStream(contentResolver.openInputStream(uri))
                val bitmap = BitmapFactory.decodeStream(bis)

                val orientation = ImageUtil.extractOrientationFromGalleryImage(applicationContext, uri)
                val orientationModifiedBitmap = ImageUtil.getOrientationModifiedBitmap(bitmap, orientation)

                val outputDir = applicationContext.filesDir
                val fileName = FILE_NAME_TEMPLATE.format(Date())
                FileOutputStream(File(outputDir, fileName)).use {
                    orientationModifiedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 ,it)
                }

                sideImage.scaleType = ImageView.ScaleType.CENTER_CROP
                sideImage.setImageBitmap(orientationModifiedBitmap)

                tempSideImageFileName = fileName
            }
        }
    }

    private fun getCameraFile(): File {
        val dir = File(applicationContext.filesDir, "/temp")
        if (!dir.exists()) {
            dir.mkdir()
        }
        return File(dir, "temp.jpg")
    }

    private fun deleteIfNeed(fileName: String?) {
        if (fileName == null) {
            return
        }
        if (fileName.isEmpty()) {
            return
        }
        val target = File(applicationContext.filesDir, fileName)
        if (!target.exists()) {
            return
        }
        target.delete()
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

    override fun onDestroy() {
        super.onDestroy()
        deleteIfNeed(tempFrontImageFileName)
        deleteIfNeed(tempSideImageFileName)

        ImageUtil.releaseImageView(frontImage)
        ImageUtil.releaseImageView(sideImage)
    }

}