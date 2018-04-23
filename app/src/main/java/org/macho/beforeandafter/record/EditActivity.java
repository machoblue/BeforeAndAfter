package org.macho.beforeandafter.record;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.macho.beforeandafter.AdUtil;
import org.macho.beforeandafter.BuildConfig;
import org.macho.beforeandafter.ImageUtil;
import org.macho.beforeandafter.R;
import org.macho.beforeandafter.RecordDao;
import org.macho.beforeandafter.record.camera.CameraActivity;
import org.macho.beforeandafter.record.camera.PermissionUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yuukimatsushima on 2017/08/13.
 */

public class EditActivity extends AppCompatActivity {
    private static final int FRONT_IMAGE = 1;
    private static final int SIDE_IMAGE = 2;
    private static final int FRONT_IMAGE_STANDARD_CAMERA = 3;
    private static final int SIDE_IMAGE_STANDARD_CAMERA = 4;
    private static final String FILE_NAME_TEMPLATE = "image-%1$tF-%1$tH-%1$tM-%1$tS-%1$tL.jpg";
    private static final int FRONT_GALLERY_IMAGE_REQUEST = 6;
    private static final int SIDE_GALLERY_IMAGE_REQUEST = 9;
////    private static final String FRONT_IMAGE_FILE_NAME_TEMPLATE = "front_%1$tF-%1$tH-%1$tM-%1$tS-%1$tL.jpg";
//    private static final String SIDE_IMAGE_FILE_NAME_TEMPLATE = "side_%1$tF-%1$tH-%1$tM-%1$tS-%1$tL.jpg";
//    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int GALLERY_PERMISSIONS_REQUEST = 5;
    private static final int GALLERY_IMAGE_REQUEST = 6;
    public static final int CAMERA_PERMISSIONS_REQUEST = 7;
    private static final int GALLERY_PERMISSIONS_REQUEST_SIDE = 10;
    public static final int CAMERA_PERMISSIONS_REQUEST_SIDE = 11;
    public static final int CAMERA_IMAGE_REQUEST = 8;
    private Activity thisActivity = this;
    private ImageView frontImage;
    private ImageView sideImage;
    private EditText weight;
    private EditText rate;
    private EditText memo;
    private Button cancelButton;
    private Button saveButton;
    private Button deleteButton;
    private LinearLayout buttonLayout;
    long date;

    private String tempFrontImageFileName = null;
    private String tempSideImageFileName = null;

    private Record record;

    private int index;

    private InterstitialAd interstitialAd;

    private View.OnClickListener onFrontImageViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
            builder
                    .setMessage(R.string.dialog_select_prompt)
                    .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startGalleryChooser(true);
                        }
                    })
                    .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startCamera(true);
                        }
                    });
            builder.create().show();
        }
    };

    // ギャラリーが選択されたときの処理
    public void startGalleryChooser(boolean front) {
        if (PermissionUtils.requestPermission(this,
                front ? GALLERY_PERMISSIONS_REQUEST : GALLERY_PERMISSIONS_REQUEST_SIDE,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "写真を選択してください"),
                    front ? FRONT_GALLERY_IMAGE_REQUEST : SIDE_GALLERY_IMAGE_REQUEST);
        }
    }

    // カメラが選択された時の処理
    public void startCamera(boolean front) {
        if (PermissionUtils.requestPermission(
                this,
                front ? CAMERA_PERMISSIONS_REQUEST : CAMERA_PERMISSIONS_REQUEST_SIDE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean useStandardCamera = preferences.getBoolean("USE_STANDARD_CAMERA", false);
            if (!useStandardCamera) {
                Intent intent = new Intent(EditActivity.this, CameraActivity.class);
                startActivityForResult(intent, front ? FRONT_IMAGE : SIDE_IMAGE);
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri uri  = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileprovider", getCameraFile());
                System.out.println("URI:" + uri + ", cameraFile:" + getCameraFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, front ? FRONT_IMAGE_STANDARD_CAMERA : SIDE_IMAGE_STANDARD_CAMERA);
            }
        }
    }

    public File getCameraFile() {
        File dir = new File(getApplicationContext().getFilesDir(), "/temp");
        if (!dir.exists()) {
            dir.mkdir();
        }
        return new File(dir, "temp.jpg");
    }

    private View.OnClickListener onSideImageViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
            builder
                    .setMessage(R.string.dialog_select_prompt)
                    .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startGalleryChooser(false);
                        }
                    })
                    .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startCamera(false);
                        }
                    });
            builder.create().show();
        }
    };

    private View.OnClickListener onCancelButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.putExtra("TYPE", 0);
            setResult(RESULT_OK, intent);

            AdUtil.show(interstitialAd);

            finish();
        }
    };

    private View.OnClickListener onDeleteButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecordDao.getInstance().delete(record.getDate());
            Intent intent = new Intent();
            intent.putExtra("INDEX", index);
            intent.putExtra("TYPE", 1);
            setResult(RESULT_OK, intent);

            AdUtil.show(interstitialAd);

            finish();
        }
    };

    private View.OnClickListener onSaveButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            try {
                record.setWeight(returnZeroIfNeed(weight));
                record.setRate(returnZeroIfNeed(rate));
                record.setMemo(memo.getText().toString());

                if (tempFrontImageFileName != null) {
                    String oldName = record.getFrontImagePath();
                    deleteIfNeed(oldName);
                    record.setFrontImagePath(tempFrontImageFileName);
                    tempFrontImageFileName = null;
                }

                if (tempSideImageFileName != null) {
                    String oldName = record.getSideImagePath();
                    deleteIfNeed(oldName);
                    record.setSideImagePath(tempSideImageFileName);
                    tempSideImageFileName = null;
                }

                if (date != 0L) {
                    RecordDao.getInstance().update(record);
                    intent.putExtra("ISNEW", false);
                } else {
                    record.setDate(new Date().getTime());
                    RecordDao.getInstance().register(record);
                    intent.putExtra("ISNEW", true);
                    intent.putExtra("DATE", record.getDate());
                }

                intent.putExtra("INDEX", index);
                intent.putExtra("TYPE", 2);
                setResult(RESULT_OK, intent);

                AdUtil.show(interstitialAd);

                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private float returnZeroIfNeed(TextView text) {
        if (text == null || text.getText() == null || text.getText().toString() == null || "".equals(text.getText().toString())) {
            return 0f;
        }
        float value = Float.parseFloat(text.getText().toString());
        return value < 0 ? 0 : value;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit);
        frontImage = (ImageView) findViewById(R.id.frontImage);
        sideImage = (ImageView) findViewById(R.id.sideImage);
        weight = (EditText) findViewById(R.id.weight);
        rate = (EditText) findViewById(R.id.rate);
        memo = (EditText) findViewById(R.id.memo);
        cancelButton = (Button) findViewById(R.id.cancel);
        saveButton = (Button) findViewById(R.id.save);
        buttonLayout = (LinearLayout) findViewById(R.id.button_layout);

        frontImage.setOnClickListener(onFrontImageViewClickListener);
        sideImage.setOnClickListener(onSideImageViewClickListener);
        cancelButton.setOnClickListener(onCancelButtonClickListener);
        saveButton.setOnClickListener(onSaveButtonClickListener);

        Intent intent = getIntent();
        date = intent.getLongExtra("DATE", 0);
        index = intent.getIntExtra("INDEX", 0);
        if (date != 0L) {
            record = RecordDao.getInstance().find(date);
            try (InputStream is = openFileInput(record.getFrontImagePath())) {
                Bitmap frontBitmap = BitmapFactory.decodeStream(is);
                frontImage.setImageBitmap(frontBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try (InputStream is = openFileInput(record.getSideImagePath())) {
                Bitmap sideBitmap = BitmapFactory.decodeStream(is);
                sideImage.setImageBitmap(sideBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

            weight.setText(String.format("%.2f",record.getWeight()));
            rate.setText(String.format("%.2f", record.getRate()));
            memo.setText(record.getMemo());

            deleteButton = new Button(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
            deleteButton.setLayoutParams(params);
            deleteButton.setText(getResources().getString(R.string.delete));
            deleteButton.setOnClickListener(onDeleteButtonClickListener);
            buttonLayout.addView(deleteButton);

        } else {
            record = new Record();
        }

        tempFrontImageFileName = null;
        tempSideImageFileName = null;

        MobileAds.initialize(this, getString(R.string.admob_app_id));

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_unit_id_interstitial));
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode != RESULT_OK) {
                return;
            }
            switch (requestCode) {
                case FRONT_IMAGE:
                    String frontImageFilePath = data.getStringExtra("PATH");
                    System.out.println("path:" + frontImageFilePath);
                    ImageUtil.setOrientationModifiedImageFile(frontImage, new File(frontImageFilePath));
                    // TODO: "/" は記録フラグメントで context.openFileInputで開く時のため。
                    String frontImageFileName = frontImageFilePath.replaceAll(this.getApplicationContext().getFilesDir().toString() + "/", "");
                    System.out.println("name:" + frontImageFileName);
                    tempFrontImageFileName = frontImageFileName;
                    break;
                case SIDE_IMAGE:
                    String sideImageFilePath = data.getStringExtra("PATH");
                    System.out.println("path:" + sideImageFilePath);
                    ImageUtil.setOrientationModifiedImageFile(sideImage, new File(sideImageFilePath));
                    String sideImageFileName = sideImageFilePath.replaceAll(this.getApplicationContext().getFilesDir().toString() + "/", "");
                    System.out.println("name:" + sideImageFileName);
                    tempSideImageFileName = sideImageFileName;
                    break;
                case FRONT_IMAGE_STANDARD_CAMERA:
                    File tempFile = getCameraFile();
                    ImageUtil.setOrientationModifiedImageFile(frontImage, tempFile);

                    // temp -> appディレクトリに
                    File outputDir = this.getApplicationContext().getFilesDir();
                    String fileName = String.format(FILE_NAME_TEMPLATE, Calendar.getInstance());
                    try (FileOutputStream fos = new FileOutputStream(new File(outputDir, fileName))) {
                        Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getPath());
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        ImageUtil.releaseBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    tempFrontImageFileName = fileName;
                    break;
                case FRONT_GALLERY_IMAGE_REQUEST:
                    if (data == null) {
                        break;
                    }
                    BufferedInputStream bis = new BufferedInputStream(this.getContentResolver().openInputStream(data.getData()));
                    Bitmap bitmap2 = BitmapFactory.decodeStream(bis);

                    // Bitmapの向きを調整
                    Uri uri = data.getData();
                    System.out.println("URI:" + uri);
                    int orientation = ImageUtil.extractOrientationFromGalleryImage(getApplicationContext(), uri);
                    Bitmap orientationModifiedBitmap = ImageUtil.getOrientationModifiedBitmap(bitmap2, orientation);

                    // Realmに入れるため、DTOに設定
                    File outputDir2 = this.getApplicationContext().getFilesDir();
                    String fileName2 = String.format(FILE_NAME_TEMPLATE, Calendar.getInstance());
                    File file2 = new File(outputDir2, fileName2);
                    try (FileOutputStream fos = new FileOutputStream(file2)) {
                        orientationModifiedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    ImageUtil.setOrientationModifiedImageFile(frontImage, file2);
                    frontImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    frontImage.setImageBitmap(orientationModifiedBitmap);

                    tempFrontImageFileName = fileName2;

                    break;
                case SIDE_IMAGE_STANDARD_CAMERA:
                    File tempFile2 = getCameraFile();
                    ImageUtil.setOrientationModifiedImageFile(sideImage, tempFile2);

                    // temp -> app ディレクトリ
                    File outputDir3 = this.getApplicationContext().getFilesDir();
                    String fileName3 = String.format(FILE_NAME_TEMPLATE, Calendar.getInstance());
                    try (FileOutputStream fos = new FileOutputStream(new File(outputDir3, fileName3))) {
                        Bitmap bitmap3 = BitmapFactory.decodeFile(tempFile2.getPath());
                        bitmap3.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        ImageUtil.releaseBitmap(bitmap3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    tempSideImageFileName = fileName3;
                    break;
                case SIDE_GALLERY_IMAGE_REQUEST:
                    if (data == null) {
                        break;
                    }
                    BufferedInputStream bis2 = new BufferedInputStream(this.getContentResolver().openInputStream(data.getData()));
                    Bitmap bitmap4 = BitmapFactory.decodeStream(bis2);

                    Uri uri2 = data.getData();
                    int orientation2 = ImageUtil.extractOrientationFromGalleryImage(getApplicationContext(), uri2);
                    Bitmap orientationModifiedBitmap2 = ImageUtil.getOrientationModifiedBitmap(bitmap4, orientation2);

                    // Realmに入れるため、DTOに設定
                    File outputDir4 = this.getApplicationContext().getFilesDir();
                    String fileName4 = String.format(FILE_NAME_TEMPLATE, Calendar.getInstance());
                    File file4 = new File(outputDir4, fileName4);
                    try (FileOutputStream fos = new FileOutputStream(file4)) {
                        orientationModifiedBitmap2.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    ImageUtil.setOrientationModifiedImageFile(sideImage, file4);
                    sideImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    sideImage.setImageBitmap(orientationModifiedBitmap2);
                    tempSideImageFileName = fileName4;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteIfNeed(String fileName) {
        if (fileName == null || "".equals(fileName)) {
            return false;
        }
        File target = new File(getApplicationContext().getFilesDir(), fileName);
        if (!target.exists()) {
            System.out.println("target not exists");
            return false;
        }
        boolean deleted = target.delete();
        System.out.println("EditActivity.deleteIfNeed:" + target + ", " + deleted);
        return deleted;
    }

    // 画像アクセスのための権限設定
    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera(true);
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser(true);
                }
                break;
            case CAMERA_PERMISSIONS_REQUEST_SIDE:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST_SIDE, grantResults)) {
                    startCamera(false);
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST_SIDE:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST_SIDE, grantResults)) {
                    startGalleryChooser(false);
                }
                break;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("--- EditActivity.onDestroy ---");

        deleteIfNeed(tempFrontImageFileName);
        deleteIfNeed(tempSideImageFileName);

        // OutOfMemory対策
        ImageUtil.releaseImageView(frontImage);
        ImageUtil.releaseImageView(sideImage);
    }

}
