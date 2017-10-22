package org.macho.beforeandafter.record.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;

import org.macho.beforeandafter.R;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by yuukimatsushima on 2017/10/14.
 */

public class CameraActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private FrameLayout frame;
    private TextureView textureView;
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private ImageReader imageReader;
    private CameraInfo cameraInfo;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private CaptureRequest captureRequest;
    private MediaActionSound mediaActionSound;

    private final View.OnClickListener onShutterButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.shutter_button) {
                takePicture();
            }
        }
    };

//    @Override
//    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = layoutInflater.inflate(R.layout.fragment_camera, container, false);
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        view.findViewById(R.id.shutter_button).setOnClickListener(onShutterButtonClickListener);
//        textureView = (TextureView) view.findViewById(R.id.preview_texture);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_camera);

        findViewById(R.id.shutter_button).setOnClickListener(onShutterButtonClickListener);
        textureView = (TextureView) findViewById(R.id.preview_texture);
        try {
            frame = (FrameLayout) findViewById(R.id.frame);
            frame.addView(new SquiresView(this));
        } catch (Exception e) {
            e.printStackTrace();
        }


        mediaActionSound = new MediaActionSound();
        mediaActionSound.load(MediaActionSound.SHUTTER_CLICK);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaActionSound.release();
    }

    @Override
    public void onResume() {
        super.onResume();
        startCamera();
    }

    private void startCamera() {
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());

        if (textureView.isAvailable()) {
            openCamera(textureView.getWidth(), textureView.getHeight());
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    private final TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            transformTexture(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    private void openCamera(int width, int height) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            FragmentCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }

        cameraInfo = new CameraChooser(this, width, height).chooseCamera();

        if (cameraInfo == null) {
            return;
        }

        imageReader = ImageReader.newInstance(cameraInfo.getPictureSize().getWidth(),
                cameraInfo.getPictureSize().getHeight(), ImageFormat.JPEG, 2);
        imageReader.setOnImageAvailableListener(onImageAvailableListener, backgroundHandler);

        transformTexture(width, height);

        CameraManager manager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
        try {
            manager.openCamera(cameraInfo.getCameraId(), stateCallback, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            CameraActivity.this.cameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
            CameraActivity.this.cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            CameraActivity.this.cameraDevice = null;
            Activity activity = CameraActivity.this;
            if (null != activity) {
                activity.finish();
            }
        }
    };

    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();

            texture.setDefaultBufferSize(cameraInfo.getPreviewSize().getWidth(), cameraInfo.getPreviewSize().getHeight());

            Surface surface = new Surface(texture);

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface, imageReader.getSurface()), sessionStateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private final CameraCaptureSession.StateCallback sessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
            if (cameraDevice == null) {
                return;
            }

            CameraActivity.this.cameraCaptureSession = cameraCaptureSession;

            try {
                captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                captureRequest = captureRequestBuilder.build();
                CameraActivity.this.cameraCaptureSession.setRepeatingRequest(captureRequest, null, backgroundHandler);
            } catch (CameraAccessException e ) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
        }
    };

    private final ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader imageReader) {
            Image image = imageReader.acquireLatestImage();

            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            image.close();

            backgroundHandler.post(new PictureSaver(CameraActivity.this, data));
        }
    };

    private void transformTexture(int viewWidth, int viewHeight) {
        Activity activity = this;
        if (textureView == null || cameraInfo == null || activity == null) {
            return;
        }

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        Size viewSize = new Size(viewWidth, viewHeight);

        Matrix matrix = new Matrix();

        RectF viewRect = new RectF(0, 0, viewSize.getWidth(), viewSize.getHeight());

        // TODO: height width の順番
        RectF bufferRect = new RectF(0, 0, cameraInfo.getPictureSize().getHeight(), cameraInfo.getPictureSize().getWidth());

        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());

            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);

            float scale = Math.max((float) viewSize.getHeight() / cameraInfo.getPictureSize().getHeight(), (float) viewSize.getWidth() / cameraInfo.getPictureSize().getWidth());

            matrix.postScale(scale, scale, centerX, centerY);

        } else if (rotation == Surface.ROTATION_180) {
            matrix.postRotate(180, centerX, centerY);
        }

        textureView.setTransform(matrix);
    }

    private void takePicture() {
        try {
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);

            cameraCaptureSession.capture(captureRequestBuilder.build(),
                    new CameraCaptureSession.CaptureCallback() {
                        @Override
                        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                            captureStillPicture();
                        }
                    }, backgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void captureStillPicture() {
        try {
            final Activity activity = this;
            if (null == activity || null == cameraDevice) {
                return;
            }

            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(imageReader.getSurface());

            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            int sensorOrientation = cameraInfo.getSensorOrientation();

            int jpegRotation = getPictureRotation(rotation, sensorOrientation);

            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, jpegRotation);

            CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    unlockFocus();
                }
            };

            mediaActionSound.play(MediaActionSound.SHUTTER_CLICK); // TODO

            cameraCaptureSession.stopRepeating();
            cameraCaptureSession.capture(captureBuilder.build(), captureCallback, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public int getPictureRotation(int deviceRotation, int sensorOrientation) {
        switch (deviceRotation) {
            case Surface.ROTATION_0:
                return sensorOrientation;
            case Surface.ROTATION_90:
                return (sensorOrientation + 270) % 360;
            case Surface.ROTATION_180:
                return (sensorOrientation + 180) % 360;
            case Surface.ROTATION_270:
                return (sensorOrientation + 90) % 360;
        }
        return 0;
    }

    private void unlockFocus() {
        try {
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            cameraCaptureSession.capture(captureRequestBuilder.build(), null, backgroundHandler);

            // previewに戻る // はコメントアウト
//            cameraCaptureSession.setRepeatingRequest(captureRequest, null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


}
