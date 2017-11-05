package org.macho.beforeandafter.record;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.nend.android.NendAdInterstitial;

import org.macho.beforeandafter.R;
import org.macho.beforeandafter.RecordDao;
import org.macho.beforeandafter.record.camera.CameraActivity;

import java.io.InputStream;
import java.util.Date;

/**
 * Created by yuukimatsushima on 2017/08/13.
 */

public class EditActivity extends AppCompatActivity {
    private static final int FRONT_IMAGE = 1;
    private static final int SIDE_IMAGE = 2;
//    private static final String FRONT_IMAGE_FILE_NAME_TEMPLATE = "front_%1$tF-%1$tH-%1$tM-%1$tS-%1$tL.jpg";
    private static final String SIDE_IMAGE_FILE_NAME_TEMPLATE = "side_%1$tF-%1$tH-%1$tM-%1$tS-%1$tL.jpg";
    private static final int REQUEST_CAMERA_PERMISSION = 1;
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

    private Record record;

    private int index;
//    private Uri frontImageUri;

    private View.OnClickListener onFrontImageViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            String fileName = String.format(FRONT_IMAGE_FILE_NAME_TEMPLATE, Calendar.getInstance());
//            ContentValues values = new ContentValues();
//            try {
//                values.put(MediaStore.Images.Media.TITLE, fileName);
//                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            frontImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, frontImageUri);
//            startActivityForResult(intent, FRONT_IMAGE);
//            if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(thisActivity, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
//                return;
//            }
//            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), FRONT_IMAGE);
            Intent intent = new Intent(EditActivity.this, CameraActivity.class);
            startActivityForResult(intent, FRONT_IMAGE);
        }
    };

    private View.OnClickListener onSideImageViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(thisActivity, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
//                return;
//            }
//            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), SIDE_IMAGE);
            Intent intent = new Intent(EditActivity.this, CameraActivity.class);
            startActivityForResult(intent, SIDE_IMAGE);
        }
    };

    private View.OnClickListener onCancelButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.putExtra("TYPE", 0);
            setResult(RESULT_OK, intent);

            NendAdInterstitial.showAd(EditActivity.this);

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

            NendAdInterstitial.showAd(EditActivity.this);

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

                NendAdInterstitial.showAd(EditActivity.this);

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
//        deleteButton = (Button) findViewById(R.id.delete);

        frontImage.setOnClickListener(onFrontImageViewClickListener);
        sideImage.setOnClickListener(onSideImageViewClickListener);
        cancelButton.setOnClickListener(onCancelButtonClickListener);
        saveButton.setOnClickListener(onSaveButtonClickListener);
//        deleteButton.sic_launcher.pngetOnClickListener(onDeleteButtonClickListener);

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

        NendAdInterstitial.loadAd(this, "3daf5b0053537a900e405a9cd1a0a2c57b2e3ba6", 811664);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
//            throw new RuntimeException("[RESULT_NG] requestCode:" + requestCode + ", resultCode:" + resultCode);
            return;
        }
        switch (requestCode) {
            case FRONT_IMAGE:
//                Bitmap frontImageBitmap = (Bitmap) data.getExtras().get("data");
//                String frontImageFilePath = String.format(FRONT_IMAGE_FILE_NAME_TEMPLATE, Calendar.getInstance());
//                try (FileOutputStream fos = openFileOutput(frontImageFilePath, Context.MODE_PRIVATE)) {
//                    frontImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                String frontImageFilePath = data.getStringExtra("PATH");
                System.out.println("path:" + frontImageFilePath);
                frontImage.setImageBitmap(BitmapFactory.decodeFile(frontImageFilePath));
                // TODO: "/" は記録フラグメントで context.openFileInputで開く時のため。
                String frontImageFileName = frontImageFilePath.replaceAll(this.getApplicationContext().getFilesDir().toString() + "/", "");
                System.out.println("name:" + frontImageFileName);
                record.setFrontImagePath(frontImageFileName);
                break;
//                frontImage.setImageURI(frontImageUri);
            case SIDE_IMAGE:
//                Bitmap sideImageBitmap = (Bitmap) data.getExtras().get("data");
//                String sideImageFilePath = String.format(SIDE_IMAGE_FILE_NAME_TEMPLATE, Calendar.getInstance());
//                try (FileOutputStream fos = openFileOutput(sideImageFilePath, Context.MODE_PRIVATE)) {
//                    sideImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                sideImage.setImageBitmap((Bitmap) data.getExtras().get("data"));
//                record.setSideImagePath(sideImageFilePath);
                String sideImageFilePath = data.getStringExtra("PATH");
                System.out.println("path:" + sideImageFilePath);
                sideImage.setImageBitmap(BitmapFactory.decodeFile(sideImageFilePath));
                String sideImageFileName = sideImageFilePath.replaceAll(this.getApplicationContext().getFilesDir().toString() + "/", "");
                System.out.println("name:" + sideImageFileName);
                record.setSideImagePath(sideImageFileName);
                break;
        }
    }

}
