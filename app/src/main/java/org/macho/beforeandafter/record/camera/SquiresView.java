package org.macho.beforeandafter.record.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yuukimatsushima on 2017/10/15.
 */

public class SquiresView extends View {
    private Paint paint;
    private float parentX;
    private float parentY;
    private float parentWidth;
    private float parentHeight;
    private int horizontalSquireNum = 5;
    private int verticalSquireNum = 10;
    private float squireWidth;
    private float squireHeight;
    public SquiresView(Context context) {
        super(context);
    }

    public SquiresView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquiresView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SquiresView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onDraw(Canvas canvas) {
        try {
            paint = new Paint();
            paint.setColor(Color.argb(100, 200, 200, 200));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);

            ViewGroup parent = (ViewGroup) getParent();
            parentX = parent.getX();
            parentY = parent.getY();
            parentWidth = parent.getWidth();
            parentHeight = parent.getHeight();

            squireWidth = parentWidth / horizontalSquireNum;
            squireHeight = parentHeight / verticalSquireNum;

            for (int i = 0; i < verticalSquireNum; i++) {
                float y = parentY + (squireHeight * (i + 1));
                canvas.drawLine(parentX, y, parentX + parentWidth, y, paint);
            }

            for (int i = 0; i < horizontalSquireNum; i++) {
                float x = parentX + (squireWidth * (i + 1));
                canvas.drawLine(x, parentY, x, parentY + parentHeight, paint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
