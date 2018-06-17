package org.macho.beforeandafter.gallery;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.SeekBar;

import org.macho.beforeandafter.GlideApp;
import org.macho.beforeandafter.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.macho.beforeandafter.BeforeAndAfterConst.PATH;

public class PhotoActivity2 extends AppCompatActivity {
    private List<String> items = new ArrayList<>();
    private int index;

    private ImageView imageView;

    private SeekBar seekBar;

    private static final int SWIPE_MIN_DISTANCE = 50; // X軸最低スワイプ距離
    private static final int SWIPE_THRESHOLD_VELOCITY = 200; // X軸最低スワイプスピード
    private static final int SWIPE_MAX_OFF_PATH = 250; // Y軸の移動距離　これ以上なら横移動を判定しない

    private GestureDetector gestureDetector;
    private final GestureDetector.SimpleOnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            System.out.println("*** MainActivity.onFling ***");
            // 移動距離・スピードを出力
//          float distance_x = Math.abs((event1.getX() - event2.getX()));
//          float velocity_x = Math.abs(velocityX);

            // Y軸の移動距離が大きすぎる場合
            if (Math.abs(event1.getY() - event2.getY()) > SWIPE_MAX_OFF_PATH) {
                return true;
            }
            // 開始位置から終了位置の移動距離が指定値より大きい
            // X軸の移動速度が指定値より大きい
            else if  (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (items.size() - 1 <= index) {
                    return true;
                }
                index++;
                System.out.println("*** MainActivity.onFling ++ ***");
//                    recyclerView.scrollToPosition(index);
                seekBar.setProgress(index);

            }
            // 終了位置から開始位置の移動距離が指定値より大きい
            // X軸の移動速度が指定値より大きい
            else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (0 >= index) {
                    return true;
                }
                index--;
                System.out.println("*** MainActivity.onFling -- ***");
//                    recyclerView.scrollToPosition(index);
                seekBar.setProgress(index);
            }
            return false;
        }
    };

    private final SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            index = i;
            showImage();
        }
        @Override public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("*** PhotoActivity2.onCreate ***");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Intent intent = getIntent();
        index = intent.getIntExtra("INDEX", 0);
        items = Arrays.asList(intent.getStringArrayExtra("PATHS"));

        imageView = (ImageView) findViewById(R.id.imageView2);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(index);
        seekBar.setMax(items.size() - 1);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        gestureDetector = new GestureDetector(this, onGestureListener);

        showImage();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    void showImage() {
        String path = items.get(index);
        GlideApp.with(this)
                .load(Uri.fromFile(new File(PATH, path)))
                .thumbnail(.1f)
                .error(new ColorDrawable(Color.GRAY))
                .into(imageView);
    }
}
