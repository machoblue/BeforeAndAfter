package org.macho.beforeandafter.record.camera

import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.params.StreamConfigurationMap
import android.util.Size
import org.macho.beforeandafter.shared.util.LogUtil
import kotlin.math.abs

class CameraChooser(val context: Context, val width: Int, val height: Int, val isBackCamera: Boolean) {
    fun chooseCamera(): CameraInfo? {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        val cameraIds = cameraManager.cameraIdList

        for (cameraId in cameraIds) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)

            if (!(isBackCamera == isBackFacing(characteristics))) continue

            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) ?: continue
            val pictureSize = chooseImageSize(map) ?: continue
            val previewSize = choosePreviewSize(map, pictureSize) ?: continue
            val sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: continue
            return CameraInfo(cameraId, previewSize, pictureSize, sensorOrientation)
        }

        return null
    }

    private fun isBackFacing(characteristics: CameraCharacteristics): Boolean {
        val facing = characteristics.get(CameraCharacteristics.LENS_FACING) ?: return false
        return facing == CameraCharacteristics.LENS_FACING_BACK
    }

    private fun chooseImageSize(map: StreamConfigurationMap): Size? {
        val pictureSizes = map.getOutputSizes(ImageFormat.JPEG)
        pictureSizes.forEach { size ->
            LogUtil.i(this, "pictureSize: $size, rate: ${size.height.toFloat() / size.width}")
        }
        return getMinimalSize(width, height, pictureSizes)
    }

    private fun choosePreviewSize(map: StreamConfigurationMap, pictureSize: Size): Size? {
        val previewSizes = map.getOutputSizes(SurfaceTexture::class.java)
        previewSizes.forEach { size ->
            LogUtil.i(this, "previewSize: $size, rate: ${size.height.toFloat() / size.width}")
        }
//        return getMinimalSize(width / 2, height / 2, previewSizes)
        return getOptimalSize(width / 2, height / 2, previewSizes, pictureSize)
    }

    private fun getMinimalSize(minWidth: Int, minHeight: Int, sizes: Array<Size>): Size? {
        return sizes
                .sortedBy { it.width * it.height }
                .firstOrNull { isEnoughBig(minWidth, minHeight, it) }
    }

    private fun getOptimalSize(minWidth: Int, minHeight: Int, sizes: Array<Size>, pictureSize: Size): Size? {
        val filteredSizes = sizes
                .sortedBy { it.width * it.height }
                .filter { isEnoughBig(minWidth, minHeight, it) }
        val pictureSizeRatio = pictureSize.height.toFloat() / pictureSize.width
        return filteredSizes.firstOrNull { (it.height.toFloat() / it.width) == pictureSizeRatio }
                ?: filteredSizes.firstOrNull { abs((it.height.toFloat() / it.width) - pictureSizeRatio) <= 0.05 }
                ?: filteredSizes.firstOrNull()
    }

    private fun isEnoughBig(minWidth: Int, minHeight: Int, size: Size): Boolean {
        return (size.width >= minWidth && size.height >= minHeight) || (size.width >= minHeight && size.height >= minWidth)
    }
}