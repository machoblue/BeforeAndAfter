package org.macho.beforeandafter.gallery

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_photo.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.GlideApp
import java.io.File
import java.text.DateFormat

class PhotoActivity: AppCompatActivity() {
    companion object {
        const val PATHS = "PATH"
        const val INDEX = "INDEX"
        private const val SWIPE_MIN_DISTANCE = 50  // X軸最低スワイプ距離
        private const val SWIPE_THRESHOLD_VELOCITY = 200 // X軸最低スワイプスピード
        private const val SWIPE_MAX_OFF_PATH = 250 // Y軸の移動距離　これ以上なら横移動を判定しない
    }

    private var items: MutableList<GalleryPhoto> = mutableListOf()
    private var index = 0

    private lateinit var gestureDetector: GestureDetector

    private var dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)

    private val onGestureListener = object: GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            group.visibility = if (group.visibility == View.VISIBLE) View.GONE else View.VISIBLE
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

        index = intent.getIntExtra(INDEX, 0)
        items = (intent.getSerializableExtra(PATHS) as Array<GalleryPhoto>).toMutableList()

        seekBar.progress = index
        seekBar.max = items.lastIndex
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener)

        gestureDetector = GestureDetector(this, onGestureListener)

        updateView()

        closeButton.setOnClickListener {
            finish()
        }

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

        dateText.text = dateFormat.format(galleryPhoto.dateTime)
        weightAndRateText.text = "${galleryPhoto.weight ?: "-"}kg/${galleryPhoto.rate ?: "-"}%"
    }
}