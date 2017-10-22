package org.macho.beforeandafter.record.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Size;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yuukimatsushima on 2017/10/14.
 */

public class CameraChooser {
    private Context context;
    private int width;
    private int height;
    public CameraChooser(Context context, int width, int height) {
        this.context = context;
        this.width = width;
        this.height = height;
    }

    public CameraInfo chooseCamera() {
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        try {
            String[] cameraIds = cameraManager.getCameraIdList();

            for (String cameraId : cameraIds) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);

                if (!isBackFacing(characteristics)) continue;

                StreamConfigurationMap map;
                if ((map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)) == null) {
                    continue;
                }

                Size pictureSize;
                if ((pictureSize = chooseImageSize(map)) == null) {
                    continue;
                }

                Size previewSize;
                if ((previewSize = choosePreviewSize(map)) == null) {
                    continue;
                }

                Integer sensorOrientation;
                if ((sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)) == null) {
                    continue;
                }

                CameraInfo cameraInfo = new CameraInfo();
                cameraInfo.setCameraId(cameraId);
                cameraInfo.setPictureSize(pictureSize);
                cameraInfo.setPreviewSize(previewSize);
                cameraInfo.setSensorOrientation(sensorOrientation);

                return cameraInfo;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Size choosePreviewSize(StreamConfigurationMap map) {
        Size[] previewSizes = map.getOutputSizes(SurfaceTexture.class);

        return getMinimalSize(width / 2, height/ 2, previewSizes);
    }

    private Size chooseImageSize(StreamConfigurationMap map) {
        Size[] pictureSizes = map.getOutputSizes(ImageFormat.JPEG);

        return getMinimalSize(width, height, pictureSizes);
    }

    private boolean isBackFacing(CameraCharacteristics characteristics) {
        Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
        return (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK);
    }

    private Size getMinimalSize(int minWidth, int minHeight, Size[] sizes) {
        List<Size> sizeList = Arrays.asList(sizes);

        Collections.sort(sizeList, new Comparator<Size>() {
            @Override
            public int compare(Size lhs, Size rhs) {
                return lhs.getHeight() * lhs.getWidth() - rhs.getHeight() * rhs.getWidth();
            }
        });

        for (Size size : sizeList) {
            if ((size.getWidth() >= minWidth && size.getHeight() >= minHeight) || (size.getWidth() >= minHeight && size.getHeight() >= minWidth)) {
                return size;
            }
        }
        return null;
    }


}
