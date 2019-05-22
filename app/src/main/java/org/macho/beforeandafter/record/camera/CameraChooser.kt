package org.macho.beforeandafter.record.camera

import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.params.StreamConfigurationMap
import android.util.Size

class CameraChooser(val context: Context, val width: Int, val height: Int) {
    fun chooseCamera(): CameraInfo? {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        val cameraIds = cameraManager.cameraIdList

        for (cameraId in cameraIds) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)

            if (!isBackFacing(characteristics)) continue

            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) ?: continue
            val pictureSize = chooseImageSize(map) ?: continue
            val previewSize = choosePreviewSize(map) ?: continue
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
        return getMinimalSize(width, height, pictureSizes)
    }

    private fun choosePreviewSize(map: StreamConfigurationMap): Size? {
        val previewSizes = map.getOutputSizes(SurfaceTexture::class.java)
        return getMinimalSize(width / 2, height / 2, previewSizes)
    }

    private fun getMinimalSize(minWidth: Int, minHeight: Int, sizes: Array<Size>): Size? {
        val sortedSizes = sizes.sortedBy { it.width * it.height }

        for (size in sortedSizes) {
            if ((size.width >= minWidth && size.height >= minHeight) || (size.width >= minHeight && size.height >= minWidth)) {
                return size
            }
        }

        return null
    }
}