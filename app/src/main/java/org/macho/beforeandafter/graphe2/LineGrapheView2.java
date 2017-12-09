package org.macho.beforeandafter.graphe2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.graphics.Paint.Style.FILL;

/**
 * Created by yuukimatsushima on 2017/11/19.
 */

public class LineGrapheView2 extends View {
    public LineGrapheView2(Context context, long fromX, long toX, long unitX, List<Data> dataList) {
        super(context);

//        initData(); // TODO: delete
        viewToScreenRatio = 3;
        this.fromX = fromX;
        this.toX = toX;
        this.unitX = unitX;
        this.dataList = dataList;

        setUpPaint();
        calculateYMaxAndMin();
    }

    private ViewGroup parent;
    private int parentWidth;
    private int parentHeight;
    private static final int match_parent = ViewGroup.LayoutParams.MATCH_PARENT;

    private int viewToScreenRatio; // スクロールする幅はスクリーンの３倍

    private Paint framePaint;
    private List<Paint> linePaints = new ArrayList<>();
    private List<Paint> pointPaints = new ArrayList<>();
    private Paint textPaint;
    private Paint evenColumnPaint;
    private Paint saturdayColumnPaint;
    private Paint sundayColumnPaint;



    private long fromX;
    private long toX;
    private long unitX;

    private float leftYAxisMax = 0.0f;
    private float leftYAxisMin = 0.0f;
    private float rightYAxisMax = 0.0f;
    private float rightYAxisMin = 0.0f;

    private List<Data> dataList = new ArrayList<>();

    private int width;
    private int height;

    // TODO:DELETE
    public void initData() {
        viewToScreenRatio = 3;
        toX = System.currentTimeMillis();
//        fromX = toX - ((long) 1000) * 60 * 60 * 24 * 1096;
        fromX = toX - ((long) 1000) * 60 * 60 * 24 * 31 * 3;
//        unitX = 1000 * 60 * 60 * 24 * 31;
        unitX = 1000 * 60 * 60 * 24;
        dataList = new ArrayList<>();
        Data data = new Data();
        data.setColor(Color.rgb(229, 57, 53));
        data.setLeftYAxis(true);
        List<Poin2> poin2s = new ArrayList<>();
        poin2s.add(new Poin2(fromX + 1000 * 60 * 60 * 24 * 1, 50));
        poin2s.add(new Poin2(fromX + 1000 * 60 * 60 * 24 * 2, 51));
        poin2s.add(new Poin2(fromX + 1000 * 60 * 60 * 24 * 3, 53));
        poin2s.add(new Poin2(fromX + 1000 * 60 * 60 * 24 * 4, 52));
        poin2s.add(new Poin2(fromX + 1000 * 60 * 60 * 24 * 5, 51.5f));
        data.setPoin2s(poin2s);
        dataList.add(data);

        Data data2 = new Data();
        data2.setColor(Color.rgb(67, 160, 71));
        data2.setLeftYAxis(false);
        List<Poin2> poin2s2 = new ArrayList<>();
        poin2s2.add(new Poin2(fromX + 1000 * 60 * 60 * 24 * 1, 20));
        poin2s2.add(new Poin2(fromX + 1000 * 60 * 60 * 24 * 2, 22));
        poin2s2.add(new Poin2(fromX + 1000 * 60 * 60 * 24 * 3, 19));
        poin2s2.add(new Poin2(fromX + 1000 * 60 * 60 * 24 * 4, 21));
        poin2s2.add(new Poin2(fromX + 1000 * 60 * 60 * 24 * 5, 21.5f));
        data2.setPoin2s(poin2s2);
        dataList.add(data2);


        Data data3 = new Data();
        data3.setColor(Color.rgb(229, 83, 80));
        data3.setLeftYAxis(true);
        data3.setDottedLine(true);
        List<Poin2> poin2s3 = new ArrayList<>();
        poin2s3.add(new Poin2(fromX, 45));
        poin2s3.add(new Poin2(toX, 45));
        data3.setPoin2s(poin2s3);
        dataList.add(data3);

        Data data4 = new Data();
        data4.setColor(Color.rgb(129, 199, 132));
        data4.setLeftYAxis(false);
        data4.setDottedLine(true);
        List<Poin2> poin2s4 = new ArrayList<>();
        poin2s4.add(new Poin2(fromX, 15));
        poin2s4.add(new Poin2(toX, 15));
        data4.setPoin2s(poin2s4);
        dataList.add(data4);
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        parent = (ViewGroup) getParent();
        parentWidth = parent.getWidth();
        parentHeight = parent.getHeight();
        width = getWidth();
        height = getHeight();

//        float width = parentWidth * viewToScreenRatio; // 表示中の週＋前後１週間
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) width, match_parent);

        if (unitX == 1000 * 60 * 60 * 24) {
//            drawFrame(canvas);
//            drawLabel(canvas);
            drawFrame2(canvas);
        } else if (unitX == 1000L * 60 * 60 * 24 * 31) {
//            drawFrameMonth(canvas);
            drawLabelMonth(canvas);

        }

        drawLines(canvas);
    }

//    private void drawFrameMonth(Canvas canvas) {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date(fromX));
//        cal.add(Calendar.MONTH, 1);
//        cal.set(Calendar.DAY_OF_MONTH, 0);
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MILLISECOND, 0);
//        while (cal.getTime().getTime() <= toX) {
//            float x = ((float) (cal.getTime().getTime() - fromX)) / (toX - fromX) * getWidth();
//            canvas.drawLine(x, 0, x, parentHeight, framePaint);
//            cal.add(Calendar.MONTH, 1);
//        }
//
//        canvas.drawLine(0, 0, getWidth(), 0, framePaint);
//        canvas.drawLine(0, (float) (parentHeight * 0.05), getWidth(), (float) (parentHeight * 0.05), framePaint);
//    }

    private void drawLabelMonth(Canvas canvas) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(fromX));
        Calendar cal1 = getCalendarYYYYMM01000000000(cal);
        cal.add(Calendar.MONTH, 1);
        Calendar cal2 = getCalendarYYYYMM01000000000(cal);

        float textSize = calculateValidTextSize(textPaint, "99", getWidth() / ((float) (toX - fromX) / (1000L * 60 * 60 * 24 * 30)), getHeight() * 0.05f);
        textPaint.setTextSize(textSize);
        float textWidth = textPaint.measureText("99");

        float textY = height * 0.05f + 0.05f * getHeight() - (0.05f * getHeight() - textSize) / 2 - 5; // -5 は誤差
        while (cal2.getTime().getTime() <= toX) {

            // 縦線
//            float x = ((float) (cal2.getTime().getTime() - fromX)) / (toX - fromX) * getWidth();
//            canvas.drawLine(x, 0, x, parentHeight, framePaint);

            // 列に色をつける
            if (cal1.get(Calendar.MONTH) % 2 == 1) {
                float x0 = ((float) (cal1.getTime().getTime() - fromX)) / (toX - fromX) * getWidth();
                float x1 = ((float) (cal2.getTime().getTime() - fromX)) / (toX - fromX) * getWidth();
                float y0 = height * 0.05f;
                float y1 = height;
                canvas.drawRect(x0, y0, x1, y1, evenColumnPaint);
            }

            // 月を表示
            long middle = (cal1.getTime().getTime() + cal2.getTime().getTime()) / 2;
            float middleX = ((float) (middle - fromX)) / (toX - fromX) * getWidth();
            String text = String.valueOf(cal1.get(Calendar.MONTH) + 1);
            float textX = text.length() == 1 ? middleX - (textWidth / 2 / 2) : middleX - (textWidth / 2);
            canvas.drawText(text, textX, textY, textPaint);

            // インクリメント
            cal1.add(Calendar.MONTH, 1);
            cal2.add(Calendar.MONTH, 1);
        }

        // 年を表示
        cal = Calendar.getInstance();
        cal.setTime(new Date(fromX));
        cal1 = getCalendarYYYY0101000000000(cal);
        cal.add(Calendar.YEAR, 1);
        cal2 = getCalendarYYYY0101000000000(cal);
        textY = 0.05f * getHeight() - (0.05f * getHeight() - textSize) / 2 - 5; // -5 は誤差
        while (cal1.getTime().getTime() <= toX) {
            float x0 = ((float) (cal1.getTime().getTime() - fromX)) / (toX - fromX) * width;
            float x1 = ((float) (cal2.getTime().getTime() - fromX)) / (toX - fromX) * width;
            float y0 = 0f;
            float y1 = height * 0.05f;

            // 列に色をつける
            int year = cal1.get(Calendar.YEAR);
            if (year % 2 == 0) {
                canvas.drawRect(x0, y0, x1, y1, evenColumnPaint);
            }

            // 縦線
            canvas.drawLine(x1, 0, x1, height, framePaint);

            // 月を表示する。
            long middle = (cal1.getTime().getTime() + cal2.getTime().getTime()) / 2;
            float middleX = ((float) (middle - fromX)) / (toX - fromX) * getWidth();
            String text = String.valueOf(cal1.get(Calendar.YEAR));
            float textX = text.length() == 1 ? middleX - (textWidth / 2 / 2) : middleX - (textWidth / 2);
            canvas.drawText(text, textX, textY, textPaint);

            cal1.add(Calendar.YEAR, 1);
            cal2.add(Calendar.YEAR, 1);
        }

        // 横線
        canvas.drawLine(0, 0, getWidth(), 0, framePaint);
        canvas.drawLine(0, (float) (parentHeight * 0.05), getWidth(), (float) (parentHeight * 0.05), framePaint);
        canvas.drawLine(0, (float) (parentHeight * 0.10), getWidth(), (float) (parentHeight * 0.10), framePaint);
    }

    private Calendar getCalendarYYYYMM01000000000(Calendar calendar) {
        Calendar cal = (Calendar) calendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    private Calendar getCalendarYYYYMMdd000000000(Calendar calendar) {
        Calendar cal = (Calendar) calendar.clone();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    private Calendar getCalendarYYYY0101000000000(Calendar calendar) {
        Calendar cla = (Calendar) calendar.clone();
        cla.set(Calendar.MONTH, 0);
        cla.set(Calendar.DAY_OF_MONTH, 1);
        cla.set(Calendar.HOUR_OF_DAY, 0);
        cla.set(Calendar.MINUTE, 0);
        cla.set(Calendar.SECOND, 0);
        cla.set(Calendar.MILLISECOND, 0);
        return cla;
    }

    private void drawLines(Canvas canvas) {
//        for (Data data : dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            Data data = dataList.get(i);
            if (data.isLeftYAxis()) {
                if (data.getPoin2s() == null || data.getPoin2s().size() < 1) {
                    continue;
                }
                float x1;
                float y1;
                float x2;
                float y2;
                Poin2 point = data.getPoin2s().get(0);
                x1 = (float) (point.getDateTime() - fromX) / (toX - fromX) * getWidth();
                y1 = parentHeight - (point.getValue() - leftYAxisMin) / (leftYAxisMax - leftYAxisMin) * getHeight() * 0.9f;
                if (!data.isDottedLine()) {
                    canvas.drawCircle(x1, y1, 10, pointPaints.get(i));
                }
//                for (Poin2 point : data.getPoin2s()) {
                for (int j = 1; j < data.getPoin2s().size(); j++) {
                    Poin2 point2 = data.getPoin2s().get(j);
                    x2 = (float) (point2.getDateTime() - fromX) / (toX - fromX) * getWidth();
                    y2 = parentHeight - (point2.getValue() - leftYAxisMin) / (leftYAxisMax - leftYAxisMin) * getHeight() * 0.9f;
                    if (!data.isDottedLine()) {
                        canvas.drawLine(x1, y1, x2, y2, linePaints.get(i));
                        canvas.drawCircle(x2, y2, 10, pointPaints.get(i));
                    } else {
                        drawDottedLine(canvas, x1, y1, x2, y2, linePaints.get(i), 10);
                    }
                    x1 = x2;
                    y1 = y2;
                }
            } else {
                if (data.getPoin2s() == null || data.getPoin2s().size() < 1) {
                    continue;
                }
                float x1;
                float y1;
                float x2;
                float y2;
                Poin2 point = data.getPoin2s().get(0);
                x1 = (float) (point.getDateTime() - fromX) / (toX - fromX) * getWidth();
                y1 = parentHeight - (point.getValue() - rightYAxisMin) / (rightYAxisMax - rightYAxisMin) * getHeight() * 0.9f;
                if (!data.isDottedLine()) {
                    canvas.drawCircle(x1, y1, 10, pointPaints.get(i));
                }
//                for (Poin2 point : data.getPoin2s()) {
                for (int j = 1; j < data.getPoin2s().size(); j++) {
                    Poin2 point2 = data.getPoin2s().get(j);
                    x2 = (float) (point2.getDateTime() - fromX) / (toX - fromX) * getWidth();
                    y2 = parentHeight - (point2.getValue() - rightYAxisMin) / (rightYAxisMax - rightYAxisMin) * getHeight() * 0.9f;
                    if (!data.isDottedLine()) {
                        canvas.drawLine(x1, y1, x2, y2, linePaints.get(i));
                        canvas.drawCircle(x2, y2, 10, pointPaints.get(i));
                    } else {
                        drawDottedLine(canvas, x1, y1, x2, y2, linePaints.get(i), 10);
                    }
                    x1 = x2;
                    y1 = y2;
                }
            }
        }
    }

//    private void drawFrame(Canvas canvas) {
//        float colomnWidth = parentWidth / numDisplayAtOnce;
//        int length = (int) ((toX - fromX) / unitX);
//        float columnWidth = ((float) getWidth()) / length;
//
//        canvas.drawLine(0, 0, 0, parentHeight, framePaint);
//
//        for (int i = 0; i < length; i++) {
//            float x = (i + 1) * columnWidth;
//            canvas.drawLine(x, 0, x, parentHeight, framePaint);
//        }
//
//        canvas.drawLine(0, 0, width , 0, framePaint);
//        canvas.drawLine(0, (float) (parentHeight * 0.05), width, (float) (parentHeight * 0.05), framePaint);
//        canvas.drawLine(0, (float) (parentHeight * 0.10), width, (float) (parentHeight * 0.10), framePaint);
//        canvas.drawLine(0, (float) (parentHeight * 1.00), width, (float) (parentHeight * 1.00), framePaint);
//    }

    private void drawFrame2(Canvas canvas) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(fromX));
        Calendar cal1 = getCalendarYYYYMMdd000000000(cal);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Calendar cal2 = getCalendarYYYYMMdd000000000(cal);

        float textSize = calculateValidTextSize(textPaint, "99", ((float) getWidth()) / ((toX - fromX) / unitX), ((float) getHeight()) * 0.05f);
        textPaint.setTextSize(textSize);
        float textWidth = textPaint.measureText("99");

        float textY = height * 0.05f + getHeight() * 0.05f * 0.5f + textSize * 0.5f - 3f; // -3fは誤差
        while (cal2.getTime().getTime() <= toX) {
            // 列に色をつける
            float x0 = ((float) (cal1.getTime().getTime() - fromX)) / (toX - fromX) * getWidth();
            float x1 = ((float) (cal2.getTime().getTime() - fromX)) / (toX - fromX) * getWidth();
            float y0 = height * 0.05f;
            float y1 = height;
            int dayOfWeek = cal1.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == 7) {
                canvas.drawRect(x0, y0, x1, y1, saturdayColumnPaint);
            } else if (dayOfWeek == 1) {
                canvas.drawRect(x0, y0, x1, y1, sundayColumnPaint);
            } else if (dayOfWeek == 3 || dayOfWeek == 5) {
                canvas.drawRect(x0, y0, x1, y1, evenColumnPaint);
            }

            // 日付を表示する
            long middle = (cal1.getTime().getTime() + cal2.getTime().getTime()) / 2;
            float middleX = ((float) (middle - fromX)) / (toX - fromX) * getWidth();
            String text = String.valueOf(cal1.get(Calendar.DAY_OF_MONTH));
            float textX = text.length() == 1 ? middleX - (textWidth / 2 / 2) : middleX - (textWidth / 2);
            canvas.drawText(text, textX, textY, textPaint);

            cal1.add(Calendar.DAY_OF_MONTH, 1);
            cal2.add(Calendar.DAY_OF_MONTH, 1);
        }

        // 月を表示
        cal = Calendar.getInstance();
        cal.setTime(new Date(fromX));
        cal1 = getCalendarYYYYMM01000000000(cal);
        cal.add(Calendar.MONTH, 1);
        cal2 = getCalendarYYYYMM01000000000(cal);
        textY = textY - height * 0.05f;
        while (cal1.getTime().getTime() <= toX) {
            // 列に色をつける
            int month = cal1.get(Calendar.MONTH);
            float x0 = ((float) (cal1.getTime().getTime() - fromX)) / (toX - fromX) * width;
            float x1 = ((float) (cal2.getTime().getTime() - fromX)) / (toX - fromX) * width;
            float y0 = 0f;
            float y1 = height * 0.05f;
            if (month % 2 == 0) {
                canvas.drawRect(x0, y0, x1, y1, evenColumnPaint);
            }

            // 縦線
            canvas.drawLine(x1, 0, x1, height, framePaint);

            // 月を表示する。
            long middle = (cal1.getTime().getTime() + cal2.getTime().getTime()) / 2;
            float middleX = ((float) (middle - fromX)) / (toX - fromX) * getWidth();
            String text = String.valueOf(cal1.get(Calendar.MONTH) + 1);
            float textX = text.length() == 1 ? middleX - (textWidth / 2 / 2) : middleX - (textWidth / 2);
            canvas.drawText(text, textX, textY, textPaint);

            cal1.add(Calendar.MONTH, 1);
            cal2.add(Calendar.MONTH, 1);
        }

        canvas.drawLine(0, 0, width , 0, framePaint);
        canvas.drawLine(0, (float) (parentHeight * 0.05), width, (float) (parentHeight * 0.05), framePaint);
        canvas.drawLine(0, (float) (parentHeight * 0.10), width, (float) (parentHeight * 0.10), framePaint);
        canvas.drawLine(0, (float) (parentHeight * 1.00), width, (float) (parentHeight * 1.00), framePaint);
    }

    private boolean is000000000(Calendar cal) {
//        return cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0 && cal.get(Calendar.SECOND) == 0 && cal.get(Calendar.MILLISECOND) == 0;
        int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        int milliseconds = cal.get(Calendar.MILLISECOND);
        return hourOfDay == 0 && minute == 0 && second == 0 && milliseconds == 0;
    }

//    private void drawLabel(Canvas canvas) {
//        int length = (int) ((toX - fromX) / unitX);
//        float columnWidth = ((float) getWidth()) / length;
//        drawDate(canvas);
//        drawDay(canvas);
//    }

//    private void drawDate(Canvas canvas) {
//        int length = (int) ((toX - fromX) / unitX);
//        float columnWidth = ((float) getWidth()) / length;
//
//        // 初めだけtextSizeを計測
//        float textSize = calculateValidTextSize(textPaint, "99", ((float) getWidth()) / ((toX - fromX) / unitX), ((float) getHeight()) * 0.05f);
//        textPaint.setTextSize(textSize);
//        float textWidth = textPaint.measureText("99");
//
//        float y = getHeight() * 0.05f * 0.5f + textSize * 0.5f - 3f; // -3fは誤差
//        for (int i = 0; i < length; i++) {
//            long date = fromX + unitX * i;
//            String text = getFormat(unitX).format(new Date(date));
//            float tempX = ((float) (date - fromX)) / (toX - fromX) * getWidth();
//            float paddingLeft = text.length() == 2 ? (columnWidth - textWidth) / 2f : (columnWidth - textWidth * 0.5f) / 2f;
//            float x  = tempX + paddingLeft;
//            canvas.drawText(text, x, y, textPaint);
//        }
//    }

    private float calculateValidTextSize(Paint paint, String text, float width, float height) {
        String message = String.format("### %s, %f, %f", text, width, height);
        float textSize = 1.0f;
        Paint tempPaint = new Paint(paint);
        tempPaint.setTextSize(textSize);
        float textWidth = tempPaint.measureText(text);
        while (textSize < height && textWidth < width) {
            textSize++;
            tempPaint.setTextSize(textSize);
            textWidth = tempPaint.measureText(text);
        }
        return --textSize;
    }

    private SimpleDateFormat getFormat(long unitX) {
        switch ((int) (unitX / (1000 * 60 * 60 * 24))) {
            case 1 :
                return new SimpleDateFormat("d");
            case 31 :
                return new SimpleDateFormat("M");
            default:
                return null;
        }
    }
    private SimpleDateFormat getFormat2(long unitX) {
        switch ((int) (unitX / (1000 * 60 * 60 * 24))) {
            case 1 :
                return new SimpleDateFormat("E");
            case 31 :
                return new SimpleDateFormat("E");
            default:
                return null;
        }
    }
//    private void drawDay(Canvas canvas) {
//        int length = (int) ((toX - fromX) / unitX);
//        float columnWidth = ((float) getWidth()) / length;
//
//        // 初めだけtextSizeを計測
//        String tempStr = getFormat2(unitX).format(new Date(fromX));
//        float textSize = calculateValidTextSize(textPaint, tempStr, ((float) getWidth()) / ((toX - fromX) / unitX), ((float) getHeight()) * 0.05f);
//        textPaint.setTextSize(textSize);
//        float textWidth = textPaint.measureText(tempStr);
//
//        float y = getHeight() * 0.05f + getHeight() * 0.05f * 0.5f + textSize * 0.5f - 3f; // - 3fは調整用
//        for (int i = 0; i < length; i++) {
//            long date = fromX + unitX * i;
//            String text = getFormat2(unitX).format(new Date(date));
//            float x = ((float) (date - fromX)) / (toX - fromX) * getWidth() + (columnWidth - textWidth) / 2f;
//            canvas.drawText(text, x, y, textPaint);
//        }
//    }

    public void drawDottedLine(Canvas canvas, float x1, float y1, float x2, float y2, Paint paint, float length) {
        float r = (float) (length / Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));
        float xa = x1;
        float ya = y1;
        float xb;
        float yb;
        int i = 0;
        while ((x2 - x1) * (x2 - xa) >= 0 && (y2 - y1) * (y2 - ya) >= 0) {
            xa = x1 + (x2 - x1) * i * r;
            ya = y1 + (y2 - y1) * i * r;
            xb = x1 + (x2 - x1) * (i + 1) * r;
            yb = y1 + (y2 - y1) * (i + 1) * r;
            canvas.drawLine(xa, ya, xb, yb, paint);
            i += 2;
        }
    }

    private void setUpPaint() {
        framePaint = new Paint();
        framePaint.setAntiAlias(true);
        framePaint.setStrokeWidth(3);
        framePaint.setColor(Color.rgb(128, 128, 128));
        framePaint.setStyle(Paint.Style.STROKE);

        linePaints = new ArrayList<>();
        pointPaints = new ArrayList<>();
        for (Data data : dataList) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(7.5f);
            paint.setColor(data.getColor());
            paint.setStyle(Paint.Style.STROKE);
            linePaints.add(paint);

            Paint pointPaint = new Paint();
            pointPaint.setAntiAlias(true);
            pointPaint.setStrokeWidth(0);
            pointPaint.setColor(data.getColor());
            pointPaint.setStyle(FILL);
            pointPaints.add(pointPaint);
        }

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStrokeWidth(0);
        textPaint.setColor(Color.rgb(96, 96, 96));
        textPaint.setStyle(FILL);
        textPaint.setTextSize(30f); // 初期値は30

        evenColumnPaint = new Paint();
        evenColumnPaint.setStyle(FILL);
        evenColumnPaint.setColor(Color.rgb(237, 237, 237)); // e0e0e0 -> eeeeee

        saturdayColumnPaint = new Paint();
        saturdayColumnPaint.setStyle(FILL);
        saturdayColumnPaint.setColor(Color.rgb(227, 242, 253)); // 82b1ff->FCE4EC

        sundayColumnPaint = new Paint();
        sundayColumnPaint.setStyle(FILL);
        sundayColumnPaint.setColor(Color.rgb(252, 228, 236)); // ff80ab->E3F2FD
    }

    private void calculateYMaxAndMin() {
        for (Data data : dataList) {
            if (data.isLeftYAxis()) {
                float initValue = (data.getPoin2s().size() == 0) ? 0 : data.getPoin2s().get(0).getValue();
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
                float initValue = (data.getPoin2s().size() == 0) ? 0 : data.getPoin2s().get(0).getValue();
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

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        Point displaySize = new Point();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getSize(displaySize);
        int displayWidth = displaySize.x;

        setMeasuredDimension(displayWidth * viewToScreenRatio, heightSize); // TODO:displaywidthを使わない、もっと綺麗な方法がないか調べる。
    }




}
