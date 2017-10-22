package org.macho.beforeandafter.record.camera;

import android.util.Size;

/**
 * Created by yuukimatsushima on 2017/10/14.
 */

public class CameraInfo {
    private String cameraId;
    private Size previewSize;
    private Size pictureSize;
    private int sensorOrientation;

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public Size getPreviewSize() {
        return previewSize;
    }

    public void setPreviewSize(Size previewSize) {
        this.previewSize = previewSize;
    }

    public Size getPictureSize() {
        return pictureSize;
    }

    public void setPictureSize(Size pictureSize) {
        this.pictureSize = pictureSize;
    }

    public int getSensorOrientation() {
        return sensorOrientation;
    }

    public void setSensorOrientation(int sensorOrientation) {
        this.sensorOrientation = sensorOrientation;
    }
}
