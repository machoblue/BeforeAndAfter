package org.macho.beforeandafter.graphe2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuukimatsushima on 2017/12/03.
 */

public class MyView extends View {
    public MyView(Context context, List<Data> dataList) {
        super(context);
        this.dataList = dataList;
        setUpPaint();
//        initData();
        calculateYMaxAndMin();
    }
    private static final float TEXT_SIZE = 50;

    private List<Data> dataList = new ArrayList<>();
    private float leftYAxisMax = 0.0f;
    private float leftYAxisMin = 0.0f;
    private float rightYAxisMax = 0.0f;
    private float rightYAxisMin = 0.0f;

    private Paint leftYAxisScalePaint;
    private Paint leftYAxisTextPaint;
    private Paint rightYAxisScalePaint;
    private Paint rightYAxisTextPaint;
    private Paint centerLinePaint;

    // TODO:DELETE
    public void initData() {
        dataList = new ArrayList<>();
        Data data = new Data();
        data.setColor(Color.rgb(255, 0, 0));
        data.setLeftYAxis(true);
        List<Poin2> poin2s = new ArrayList<>();
        poin2s.add(new Poin2(1000 * 60 * 60 * 24 * 1, 50));
        poin2s.add(new Poin2(1000 * 60 * 60 * 24 * 2, 51));
        poin2s.add(new Poin2(1000 * 60 * 60 * 24 * 3, 53));
        poin2s.add(new Poin2(1000 * 60 * 60 * 24 * 4, 52));
        poin2s.add(new Poin2(1000 * 60 * 60 * 24 * 5, 51.5f));
        data.setPoin2s(poin2s);
        dataList.add(data);

        Data data2 = new Data();
        data2.setColor(Color.rgb(0, 255, 0));
        data2.setLeftYAxis(false);
        List<Poin2> poin2s2 = new ArrayList<>();
        poin2s2.add(new Poin2(1000 * 60 * 60 * 24 * 1, 20));
        poin2s2.add(new Poin2(1000 * 60 * 60 * 24 * 2, 22));
        poin2s2.add(new Poin2(1000 * 60 * 60 * 24 * 3, 19));
        poin2s2.add(new Poin2(1000 * 60 * 60 * 24 * 4, 21));
        poin2s2.add(new Poin2(1000 * 60 * 60 * 24 * 5, 21.5f));
        data2.setPoin2s(poin2s2);
        dataList.add(data2);


        Data data3 = new Data();
        data3.setColor(Color.rgb(255, 100, 100));
        data3.setLeftYAxis(true);
        data3.setDottedLine(true);
        List<Poin2> poin2s3 = new ArrayList<>();
        poin2s3.add(new Poin2(0, 45));
        poin2s3.add(new Poin2(0, 45));
        data3.setPoin2s(poin2s3);
        dataList.add(data3);

        Data data4 = new Data();
        data4.setColor(Color.rgb(100, 255, 100));
        data4.setLeftYAxis(false);
        data4.setDottedLine(true);
        List<Poin2> poin2s4 = new ArrayList<>();
        poin2s4.add(new Poin2(0, 15));
        poin2s4.add(new Poin2(0, 15));
        data4.setPoin2s(poin2s4);
        dataList.add(data4);
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawLeftXAxis(canvas);
        drawRightXAxis(canvas);
        float x = getWidth() * 0.5f;
        canvas.drawLine(x, 0, x, getHeight(), centerLinePaint);
    }

    private void drawLeftXAxis(Canvas canvas) {
        float value = 0;
        while (value < leftYAxisMin + 1) {
            value ++;
        }
        while (value <= leftYAxisMax - 1) {
            float y = (1f - (value - leftYAxisMin) / (leftYAxisMax - leftYAxisMin)) * getHeight() * 0.9f + getHeight() * 0.1f;
            canvas.drawLine(0, y, 25, y, leftYAxisScalePaint);
            canvas.drawText(String.valueOf(value), 30, y + TEXT_SIZE / 2 - 5, leftYAxisTextPaint); // +5は誤差
            value ++;
        }
    }

    private void drawRightXAxis(Canvas canvas) {
        float value = 0;
        while (value < rightYAxisMin + 1) {
            value ++;
        }
        while (value <= rightYAxisMax - 1) {
            float y = (1f - (value - rightYAxisMin) / (rightYAxisMax - rightYAxisMin)) * getHeight() * 0.9f + getHeight() * 0.1f;
            canvas.drawLine(getWidth() - 25, y, getWidth(), y, rightYAxisScalePaint);
            canvas.drawText(String.valueOf(value), getWidth() - 25 - 5 - rightYAxisTextPaint.measureText(String.valueOf(value)), y + TEXT_SIZE / 2 - 5, rightYAxisTextPaint);
            value ++;
        }
    }

    private void calculateYMaxAndMin() {
        for (Data data : dataList) {
            if (data.isLeftYAxis()) {
                float initValue = data.getPoin2s().size() == 0 ? 0 : data.getPoin2s().get(0).getValue();
                leftYAxisMin = leftYAxisMin == 0 ? initValue : leftYAxisMin;
                leftYAxisMax = leftYAxisMax == 0 ? initValue : leftYAxisMax;
                for (Poin2 poin2 : data.getPoin2s()) {
                    float value = poin2.getValue();
                    if (value < leftYAxisMin) {
                        leftYAxisMin = value;
                    } else if (leftYAxisMax < value) {
                        leftYAxisMax = value;
                    }
                }
            } else {
                float initValue = data.getPoin2s().size() == 0 ? 0 : data.getPoin2s().get(0).getValue();
                rightYAxisMin = rightYAxisMin == 0 ? initValue : rightYAxisMin;
                rightYAxisMax = rightYAxisMax == 0 ? initValue : rightYAxisMax;
                for (Poin2 poin2 : data.getPoin2s()) {
                    float value = poin2.getValue();
                    if (value < rightYAxisMin) {
                        rightYAxisMin = value;
                    } else if (rightYAxisMax < value) {
                        rightYAxisMax = value;
                    }
                }
            }
        }
        leftYAxisMin -= 1;
        leftYAxisMax += 1;
        rightYAxisMin -= 1;
        rightYAxisMax += 1;
    }

    private void setUpPaint() {
        rightYAxisScalePaint = new Paint();
        rightYAxisScalePaint.setAntiAlias(true);
        rightYAxisScalePaint.setStyle(Paint.Style.STROKE);
        rightYAxisScalePaint.setColor(Color.argb(128, 129, 199, 132));
        rightYAxisScalePaint.setStrokeWidth(5);

        rightYAxisTextPaint = new Paint();
        rightYAxisTextPaint.setAntiAlias(true);
        rightYAxisTextPaint.setStyle(Paint.Style.FILL);
        rightYAxisTextPaint.setColor(Color.argb(128, 129, 199, 132));
        rightYAxisTextPaint.setTextSize(TEXT_SIZE);

        leftYAxisScalePaint = new Paint();
        leftYAxisScalePaint.setAntiAlias(true);
        leftYAxisScalePaint.setStyle(Paint.Style.STROKE);
        leftYAxisScalePaint.setColor(Color.argb(128, 229, 83, 80));
        leftYAxisScalePaint.setStrokeWidth(5);

        leftYAxisTextPaint = new Paint();
        leftYAxisTextPaint.setAntiAlias(true);
        leftYAxisTextPaint.setStyle(Paint.Style.FILL);
        leftYAxisTextPaint.setColor(Color.argb(128, 229, 83, 80));
        leftYAxisTextPaint.setTextSize(TEXT_SIZE);

        centerLinePaint = new Paint();
        centerLinePaint.setStyle(Paint.Style.STROKE);
        centerLinePaint.setColor(Color.argb(128, 48, 48, 48));
        centerLinePaint.setStrokeWidth(2.5f);
    }
}
