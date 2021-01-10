
package org.macho.beforeandafter.gallery

import android.Manifest
import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_photo.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.record.camera.PermissionUtils
import org.macho.beforeandafter.shared.GlideApp
import org.macho.beforeandafter.shared.util.LogUtil
import org.macho.beforeandafter.shared.util.WeightScale
import java.io.*
import java.text.DateFormat


class PhotoActivity: AppCompatActivity() {
    companion object {
        const val PATHS = "PATH"
        const val INDEX = "INDEX"
        private const val SWIPE_MIN_DISTANCE = 50  // X軸最低スワイプ距離
        private const val SWIPE_THRESHOLD_VELOCITY = 200 // X軸最低スワイプスピード
        private const val SWIPE_MAX_OFF_PATH = 250 // Y軸の移動距離　これ以上なら横移動を判定しない


        private const val SAVE_TO_SHARED_STORAGE = 1001
    }

    private var items: MutableList<GalleryPhoto> = mutableListOf()
    private var index = 0

    private lateinit var gestureDetector: GestureDetector

    private var dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)

    private lateinit var weightScale: WeightScale

    private val onGestureListener = object: GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            if (group.visibility == View.VISIBLE) {
                group.visibility = View.GONE
                supportActionBar?.hide()

            } else {
                group.visibility = View.VISIBLE
                supportActionBar?.show()
            }
            return false
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            if (e1 == null || e2 == null) {
                return true
            }

            if (Math.abs(velocityX) <= SWIPE_THRESHOLD_VELOCITY) {
                return true
            }

            // Y軸の移動距離が大きすぎる場合
            if (Math.abs(e1.y - e2.y) > SWIPE_MAX_OFF_PATH) {
                return true
            }

            if (e1.x - e2.x > SWIPE_MIN_DISTANCE) {
                if (items.lastIndex <= index) {
                    return true
                }
                seekBar.progress = ++ index

            } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE) {
                if (index == 0) {
                    return true
                }
                seekBar.progress = -- index
            }

            return false
        }
    }

    private val onSeekBarChangeListener = object: SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            index = p1
            updateView()
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
            // do nothing
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
            // do nothing
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        this.weightScale = WeightScale(this)

        index = intent.getIntExtra(INDEX, 0)
        items = (intent.getSerializableExtra(PATHS) as Array<GalleryPhoto>).toMutableList()

        seekBar.progress = index
        seekBar.max = items.lastIndex
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener)

        gestureDetector = GestureDetector(this, onGestureListener)

        updateView()

        previousButton.setOnClickListener {
            if (index == 0) {
                return@setOnClickListener
            }
            seekBar.progress = --index
        }

        nextButton.setOnClickListener {
            if (index == items.lastIndex) {
                return@setOnClickListener
            }
            seekBar.progress = ++index
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.photo_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.close -> {
                finish()
            }
            R.id.save_to_shared_storage -> {
                saveToSharedStorage()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            SAVE_TO_SHARED_STORAGE -> {
                if (PermissionUtils.permissionGranted(requestCode, SAVE_TO_SHARED_STORAGE, grantResults)) {
                    saveToSharedStorage()
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    fun updateView() {
        val galleryPhoto = items.get(index)

        val path = galleryPhoto.fileName
        GlideApp.with(this)
                .load(Uri.fromFile(File(filesDir, path ?: "")))
                .thumbnail(.1f)
                .error(ColorDrawable(Color.GRAY))
                .into(imageView)

        title = dateFormat.format(galleryPhoto.dateTime)
        val weightText = "${galleryPhoto.weight?.let { String.format("%.1f", weightScale.convertFromKg(it)) } ?: "-"}${weightScale.weightUnitText}"
        val rateText = "${galleryPhoto.rate?.let { String.format("%.1f", it) } ?: "-"}%"
        weightAndRateText.text = "${weightText}/${rateText}"
    }

    // MARK: - Private
    private fun saveToSharedStorage() {
        if (Build.VERSION.SDK_INT < 29) {
            if (!PermissionUtils.requestPermission(this, SAVE_TO_SHARED_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                return
            }
        }

        if (Environment.MEDIA_MOUNTED != Environment.getExternalStorageState()) {
            LogUtil.i(this, "MEDIA_MOUNTED false")
            return
        }

        val photoFileName = items[index].fileName

        val values = ContentValues().also {
            it.put(MediaStore.Images.Media.DISPLAY_NAME, photoFileName)
            it.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= 29) {
                it.put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        val resolver = applicationContext.contentResolver
        val volumeName = if (Build.VERSION.SDK_INT < 29) MediaStore.VOLUME_EXTERNAL else MediaStore.VOLUME_EXTERNAL_PRIMARY
        val collection = MediaStore.Images.Media.getContentUri(volumeName)
        val item = resolver.insert(collection, values)
        try {
            FileInputStream(File(filesDir, photoFileName)).buffered().use { inputStream ->
                resolver.openOutputStream(item!!)?.buffered().use { outputStream ->
                    while (inputStream.available() > 0) {
                        outputStream!!.write(inputStream.read())
                    }
                }
            }

            Toast.makeText(this@PhotoActivity, R.string.toast_saved, Toast.LENGTH_LONG).show()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        values.clear()

        if (Build.VERSION.SDK_INT >= 29) {
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(item!!, values, null, null)
        }
    }
}