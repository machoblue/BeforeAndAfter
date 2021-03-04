package org.macho.beforeandafter.record.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.*
import android.media.ImageReader
import android.media.MediaActionSound
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.MotionEvent
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_camera.*
import org.macho.beforeandafter.BuildConfig
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.extensions.loadImage
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.shared.util.LogUtil
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import java.io.File
import java.util.*

class CameraActivity: AppCompatActivity() {

    companion object {
        private const val TAG = "CameraActivity"
        private const val REQUEST_CAMERA_PERMISSION = 1
        const val GUIDE_PHOTO_FILE_NAME = "GUIDE_PHOTO_FILE_NAME"
    }

    private lateinit var backgroundThread: HandlerThread
    private lateinit var backgroundHandler: Handler
    private lateinit var imageReader: ImageReader
    private lateinit var cameraInfo: CameraInfo
    private var cameraDevice: CameraDevice? = null
    private lateinit var cameraCaptureSession: CameraCaptureSession
    private lateinit var captureRequestBuilder: CaptureRequest.Builder
    private lateinit var captureRequest: CaptureRequest
    private lateinit var mediaActionSound: MediaActionSound

    private var isGuidePhotoVisible = false
        set(value) {
            field = value
            showGuidePhotoButton.colorFilter = if (value) PorterDuffColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_IN) else null
            guidePhoto.visibility = if (value) View.VISIBLE else View.INVISIBLE
        }

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        shutterButton.setOnClickListener { _ -> takePicture() }
        textureView.setOnTouchListener(onTouchListener)
        turnCameraButton.setOnClickListener {
            turnCamera()
        }

        isGuidePhotoVisible = !SharedPreferencesUtil.getBoolean(this, SharedPreferencesUtil.Key.HIDE_GUIDE_PHOTO)

        intent.extras?.getString(GUIDE_PHOTO_FILE_NAME)?.let {
            guidePhoto.loadImage(this, Uri.fromFile(File(this.filesDir, it)))
            showGuidePhotoButton.setOnClickListener {
                isGuidePhotoVisible = !isGuidePhotoVisible
                SharedPreferencesUtil.setBoolean(this, SharedPreferencesUtil.Key.HIDE_GUIDE_PHOTO, !isGuidePhotoVisible)
            }
        } ?: let {
            guidePhoto.visibility = View.GONE
            showGuidePhotoButton.visibility = View.GONE
        }

        timerButton.setOnClickListener {
            scheduleToTakePhotoIfNeeded()
        }

        mediaActionSound = MediaActionSound()
        mediaActionSound.load(MediaActionSound.SHUTTER_CLICK)

        AdUtil.initializeMobileAds(this)
        AdUtil.loadBannerAd(adView, this)
        adLayout.visibility = if (AdUtil.isBannerAdHidden(this)) View.GONE else View.VISIBLE
    }

    override fun onPause() {
        super.onPause()

        // E/Camera3-Device: Camera 0: initialize: Could not open camera session: Too many users (-87)
        closeCamera()

        stopTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaActionSound.release()
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    private fun startCamera() {
        backgroundThread = HandlerThread("CameraBackground")
        backgroundThread.start()
        backgroundHandler = Handler(backgroundThread.looper)

        if (textureView.isAvailable) {
            openCamera(textureView.width, textureView.height)
        } else {
            textureView.surfaceTextureListener = textureListener
        }
    }

    private val textureListener: TextureView.SurfaceTextureListener = object: TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture?, width: Int, height: Int) {
            openCamera(width, height)
        }

        override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture?, width: Int, height: Int) {
            transformTexture(width, height)
        }

        override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture?): Boolean {
            return true
        }

        override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
        }
    }

    private fun openCamera(width: Int, height: Int) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            return
        }

        cameraInfo = CameraChooser(this, width, height, isBackCamera).chooseCamera() ?: return

        imageReader = ImageReader.newInstance(cameraInfo.pictureSize.width, cameraInfo.pictureSize.height, ImageFormat.JPEG, 2)
        imageReader.setOnImageAvailableListener(onImageAvailableListener, backgroundHandler)

        transformTexture(width, height)

        val manager = this.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        manager.openCamera(cameraInfo.cameraId, stateCallback, backgroundHandler)
    }

    private val stateCallback = object: CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice) {
            this@CameraActivity.cameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onDisconnected(p0: CameraDevice) {
            closeCamera()
        }

        override fun onError(cameraDevice: CameraDevice, i: Int) {
            closeCamera()
            val activity = this@CameraActivity
            if (null != activity) {
                LogUtil.w(this, "CameraDevice.StateCallback.onError: $i")
                activity.finish()
            }
        }
    }

    private fun createCameraPreviewSession() {
        if (this.cameraDevice == null) {
            return // Workaround: フロントカメラとバックカメラの切り替え時に、IllegalStateException: CameraDevice was already closed
        }

        val texture = textureView.surfaceTexture
        texture.setDefaultBufferSize(cameraInfo.previewSize.width, cameraInfo.previewSize.height)
        val surface = Surface(texture)
        captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        captureRequestBuilder.addTarget(surface)
        cameraDevice!!.createCaptureSession(listOf(surface, imageReader.surface), sessionStateCallback, null)
    }

    private val sessionStateCallback = object: CameraCaptureSession.StateCallback() {
        override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
            if (cameraDevice == null) {
                return
            }

            this@CameraActivity.cameraCaptureSession = cameraCaptureSession ?: return

            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            captureRequest = captureRequestBuilder.build()
            this@CameraActivity.cameraCaptureSession.setRepeatingRequest(captureRequest, null, backgroundHandler)
        }

        override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
        }
    }

    private val onImageAvailableListener = object: ImageReader.OnImageAvailableListener {

        override fun onImageAvailable(imageReader: ImageReader?) {
            val image = imageReader!!.acquireLatestImage()

            val buffer = image.planes[0].buffer
            val data = ByteArray(buffer.remaining())
            buffer.get(data)
            image.close()

            backgroundHandler.post(PictureSaver(this@CameraActivity, data))
        }
    }

    private fun transformTexture(viewWidth: Int, viewHeight: Int) {
        val activity = this@CameraActivity
        if (textureView == null || cameraInfo == null || activity == null) {
            return
        }

        val rotation = activity.windowManager.defaultDisplay.rotation

        val viewSize = Size(viewWidth, viewHeight)
        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewSize.width.toFloat(), viewSize.height.toFloat())
        // NOTE: height -> widthの順番
        val bufferRect = RectF(0f, 0f, cameraInfo.pictureSize.height.toFloat(), cameraInfo.pictureSize.width.toFloat())

        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()

        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
            val scale = Math.max(viewSize.height.toFloat() / cameraInfo.pictureSize.height.toFloat(),
                    viewSize.width.toFloat() / cameraInfo.pictureSize.width.toFloat())
            matrix.postScale(scale, scale, centerX, centerY)

        } else if (rotation == Surface.ROTATION_180) {
            matrix.postRotate(180f, centerX, centerY)
        }

        textureView.setTransform(matrix)
    }

    private fun takePicture() {
        if (captureRequestBuilder == null) {
            mainThreadHandler.post {
                Toast.makeText(this, getString(R.string.camera_in_preparation), Toast.LENGTH_SHORT).show()
            }
            return
        }
        
        if (cameraDevice == null) {
            mainThreadHandler.post {
                Toast.makeText(this, getString(R.string.camera_in_preparation), Toast.LENGTH_SHORT).show()
            }
            return
        }

        captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START)

        cameraCaptureSession.capture(captureRequestBuilder.build(), object: CameraCaptureSession.CaptureCallback() {
            override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
                captureStillPicture()
            }
        }, backgroundHandler)
    }

    private fun captureStillPicture() {
        val activity = this@CameraActivity
        if (null == activity || null == cameraDevice) {
            return
        }

        val captureBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        captureBuilder.addTarget(imageReader.surface)

        captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
        captureBuilder.set(CaptureRequest.SCALER_CROP_REGION, cropRegion) // enable zoom

        val rotation = activity.windowManager.defaultDisplay.rotation
        val sensorOrientation = cameraInfo.sensorOrientation

        val jpegRotation = getPictureRotation(rotation, sensorOrientation)
        captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, jpegRotation)

        val captureCallback = object: CameraCaptureSession.CaptureCallback() {
            override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
//                unlockFocus() // 1枚撮影したら、閉じるので、不要。以下のエラー対応。
                // IllegalStateException: CameraDevice was already closed
                // unLockFocus()→capture()で発生。
            }
        }

        if (!BuildConfig.DEBUG) {
            mediaActionSound.play(MediaActionSound.SHUTTER_CLICK)
        }

        cameraCaptureSession.stopRepeating()
        cameraCaptureSession.capture(captureBuilder.build(), captureCallback, null)
    }

    private fun getPictureRotation(deviceRotation: Int, sensorOrientation: Int): Int {
        when(deviceRotation) {
            Surface.ROTATION_0 -> return sensorOrientation
            Surface.ROTATION_90 -> return (sensorOrientation + 270) % 360
            Surface.ROTATION_180 -> return (sensorOrientation + 180) % 360
            Surface.ROTATION_270 -> return (sensorOrientation + 90) % 360
            else -> return 0
        }
    }

    private fun unlockFocus() {
        captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL)
        cameraCaptureSession.capture(captureRequestBuilder.build(), null, backgroundHandler)

        cameraCaptureSession.setRepeatingRequest(captureRequest, null, backgroundHandler) // previewにもどる
    }

    private fun closeCamera() {
        val cameraDevice = this.cameraDevice ?: return
        this.cameraDevice = null
        cameraDevice.close()
    }

    // MARK: - Zoom

    private var currentFingerSpace = 0f

    private val onTouchListener = object: View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            try {
                Log.i(TAG, "*** onTouch: ${event?.action}, ${event?.pointerCount}, UP: ${MotionEvent.ACTION_UP}, DOWN: ${MotionEvent.ACTION_DOWN}, MOVE: ${MotionEvent.ACTION_MOVE}")
                if (event == null) {
                    return false
                }

                if (event.pointerCount != 2) {
                    currentFingerSpace = 0f
                    return true
                }

                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        currentFingerSpace = 0f
                        return false
                    }
                    MotionEvent.ACTION_DOWN -> {
                        currentFingerSpace = event.fingerSpace()
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val fingerSpace = event.fingerSpace()
                        val fingerSpaceDiff = fingerSpace - currentFingerSpace
                        if (fingerSpaceDiff > 0) {
                            val level = currentZoomLevel + 0.1f
                            changeZoomLevel(level)
                        } else {
                            val level = currentZoomLevel - 0.1f
                            changeZoomLevel(level)
                        }
                        currentFingerSpace = fingerSpace
                        return true
                    }
                }
                return true

            } catch (e: Exception) {
                Log.e(TAG, "*** Error!", e)

                return true
            }
        }
    }

    private var currentZoomLevel = 1f
    private var cropRegion: Rect? = null

    private fun changeZoomLevel(level: Float) {
        if (level < 1) {
            return
        }

        if (level == currentZoomLevel) {
            return
        }

        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val characteristics = cameraManager.getCameraCharacteristics(cameraInfo.cameraId)

        val max = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)
        Log.i(TAG, "max:${max}")
        if (max != null && level > max) {
            return
        }

        val activeArraySize = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)!!
        if (level == 1f) {
            cropRegion = activeArraySize
        } else {
            val centerX = activeArraySize.centerX()
            val centerY = activeArraySize.centerY()
            val newHalfWidth = ((activeArraySize.width() / 2) / currentZoomLevel).toInt()
            val newHalfHeight = ((activeArraySize.height() / 2) / currentZoomLevel).toInt()

            val left = centerX - newHalfWidth
            val top = centerY - newHalfHeight
            val right = centerX + newHalfWidth
            val bottom = centerX + newHalfHeight
            cropRegion = Rect(left, top, right, bottom)
        }

        currentZoomLevel = level

        captureRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, cropRegion);
        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null);
    }

    // MARK: - Switch back to front, vice versa

    private var isBackCamera = true
        set(value) {
            field = value
            guidePhoto.rotationY = if (value) 0f else 180f
        }

    private fun turnCamera() {
        closeCamera()

        isBackCamera = !isBackCamera
        startCamera()
    }

    // MARK: - Timer

    private var timer: Timer? = null
    private val handler: Handler = Handler(Looper.getMainLooper())

    private fun scheduleToTakePhotoIfNeeded() {
        timer?.let { timer ->
            stopTimer()

        } ?: let {
            var count = SharedPreferencesUtil.getInt(this, SharedPreferencesUtil.Key.CAMERA_TIMER_SECONDS, 5)
            timer = Timer()
            timer?.schedule(object: TimerTask() {
                override fun run() {
                    count--
                    handler.post {
                        timerText.text = count.toString()
                    }
                    if (count == 0) {
                        takePicture()
                        stopTimer()
                    }
                }
            }, 1000, 1000)

            timerButton.setColorFilter(ContextCompat.getColor(this@CameraActivity, R.color.colorAccent))
            timerText.visibility = View.VISIBLE
            timerText.text = count.toString()
        }
    }

    private fun stopTimer() {
        timer?.let {
            it.cancel()
            this@CameraActivity.timer = null
            handler.post {
                timerButton.setColorFilter(Color.WHITE)
                timerText.visibility = View.GONE
            }
        }
    }
}

fun MotionEvent.fingerSpace(): Float {
    if (pointerCount != 2) {
        return 0f
    }
    val xDiff = getX(0) - getX(1)
    val yDiff = getY(0) - getY(0)
    return Math.sqrt((xDiff * xDiff + yDiff * yDiff).toDouble()).toFloat()
}