package org.macho.beforeandafter.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.SeekBar;

import org.macho.beforeandafter.ImageUtil;
import org.macho.beforeandafter.R;

import java.io.File;

/**
 * Created by yuukimatsushima on 2017/08/13.
 */

public class PhotoActivity extends AppCompatActivity {
    private int index;
    private String[] imagePaths;
    private ImageView imageView;
    private SeekBar seekBar;

    // X軸最低スワイプ距離
    private static final int SWIPE_MIN_DISTANCE = 50;
    // X軸最低スワイプスピード
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    // Y軸の移動距離　これ以上なら横移動を判定しない
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private GestureDetector gestureDetector;
    private GestureDetector.SimpleOnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            try {
                // 移動距離・スピードを出力
                float distance_x = Math.abs((event1.getX() - event2.getX()));
                float velocity_x = Math.abs(velocityX);

                // Y軸の移動距離が大きすぎる場合
                if (Math.abs(event1.getY() - event2.getY()) > SWIPE_MAX_OFF_PATH) {
                    return true;
                }
                // 開始位置から終了位置の移動距離が指定値より大きい
                // X軸の移動速度が指定値より大きい
                else if  (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (imagePaths.length - 1 <= index) {
                        return true;
                    }
                    index++;
                    seekBar.setProgress(index);
//                    imageView.setImageBitmap(BitmapFactory.decodeFile("/data/data/org.macho.beforeandafter/files/" + imagePaths[index]));
                    ImageUtil.setOrientationModifiedImageFile(imageView, new File("/data/data/org.macho.beforeandafter/files/" + imagePaths[index]));

                }
                // 終了位置から開始位置の移動距離が指定値より大きい
                // X軸の移動速度が指定値より大きい
                else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (0 >= index) {
                        return true;
                    }
                    index--;
                    seekBar.setProgress(index);
//                    imageView.setImageBitmap(BitmapFactory.decodeFile("/data/data/org.macho.beforeandafter/files/" + imagePaths[index]));
                    ImageUtil.setOrientationModifiedImageFile(imageView, new File("/data/data/org.macho.beforeandafter/files/" + imagePaths[index]));
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
            return false;
        }
    };

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//            imageView.setImageBitmap(BitmapFactory.decodeFile("/data/data/org.macho.beforeandafter/files/" + imagePaths[i]));
            ImageUtil.setOrientationModifiedImageFile(imageView, new File("/data/data/org.macho.beforeandafter/files/" + imagePaths[i]));
            index = i;
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_photo);

            Intent intent = getIntent();
            index = intent.getIntExtra("INDEX", 0);
            imagePaths = intent.getStringArrayExtra("PATHS");
            imageView = (ImageView) findViewById(R.id.imageView2);
            ImageUtil.setOrientationModifiedImageFile(imageView, new File("/data/data/org.macho.beforeandafter/files/" + imagePaths[index]));
            seekBar = (SeekBar) findViewById(R.id.seekBar);
            seekBar.setProgress(index);
            seekBar.setMax(imagePaths.length - 1);
            seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        gestureDetector = new GestureDetector(this, onGestureListener);
    }

}
