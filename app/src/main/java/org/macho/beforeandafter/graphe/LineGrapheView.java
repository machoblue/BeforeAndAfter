package org.macho.beforeandafter.graphe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

import org.macho.beforeandafter.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by yuukimatsushima on 2017/08/18.
 */

public class LineGrapheView extends View {
    private Context context;
    private long from;
    private long to;
    private List<DateLongValueFloat> data1;
    private float goal1;
    private String unit1;
    private List<DateLongValueFloat> data2;
    private float goal2;
    private String unit2;
    private Paint paintXAxis;
    private Paint paintYAxis;
    private Paint paintData1;
    private Paint paintGoal1;
    private Paint paintData2;
    private Paint paintGoal2;
    private Paint paintText;
    private Paint paintScale;
    private Paint paintUnit1;
    private Paint paintUnit2;

    private float parentX;
    private float parentY;
    private float parentWidth;
    private float parentHeight;
    public LineGrapheView(Context context,
                          List<DateLongValueFloat> data1, float goal1, String unit1,
                          List<DateLongValueFloat> data2, float goal2, String unit2,
                          long from, long to) {
        super(context);
        this.context = context;
        this.data1 = data1;
        this.goal1 = goal1;
        this.unit1 = unit1;
        this.data2 = data2;
        this.goal2 = goal2;
        this.unit2 = unit2;
        this.from = from;
        this.to = to;

        paintXAxis = new Paint();
//        paintXAxis.setColor(Color.argb(128, 0, 0 , 0));
        paintXAxis.setColor(ContextCompat.getColor(getContext(), R.color.colorFrame));
        paintXAxis.setStyle(Paint.Style.STROKE);
        paintXAxis.setStrokeWidth(10);
        paintYAxis = new Paint();
//        paintYAxis.setColor(Color.argb(128, 0, 0 , 0));
        paintYAxis.setColor(ContextCompat.getColor(getContext(), R.color.colorFrame));
        paintYAxis.setStyle(Paint.Style.STROKE);
        paintYAxis.setStrokeWidth(5);
        paintYAxis.setStrokeCap(Paint.Cap.ROUND);
        paintYAxis.setStrokeJoin(Paint.Join.ROUND);
        paintData1 = new Paint();
//        paintData1.setColor(Color.argb(128, 255, 0 , 0));
        paintData1.setColor(ContextCompat.getColor(getContext(), R.color.colorWeight));
        paintData1.setStyle(Paint.Style.STROKE);
        paintData1.setStrokeWidth(10);
        paintData1.setStrokeCap(Paint.Cap.ROUND);
        paintData1.setStrokeJoin(Paint.Join.ROUND);
        paintData2 = new Paint();
//        paintData2.setColor(Color.argb(128, 0, 255 , 0));
        paintData2.setColor(ContextCompat.getColor(getContext(), R.color.colorRate));
        paintData2.setStyle(Paint.Style.STROKE);
        paintData2.setStrokeWidth(10);
        paintData2.setStrokeCap(Paint.Cap.ROUND);
        paintData2.setStrokeJoin(Paint.Join.ROUND);
        paintGoal1 = new Paint();
//        paintGoal1.setColor(Color.argb(128, 196, 48, 48));
        paintGoal1.setColor(ContextCompat.getColor(getContext(), R.color.colorWeightGoal));
        paintGoal1.setStyle(Paint.Style.STROKE);
        paintGoal1.setStrokeWidth(10);
        paintGoal1.setPathEffect(new DashPathEffect(new float[]{ 5.0f, 5.0f }, 0));
        paintGoal2 = new Paint();
//        paintGoal2.setColor(Color.argb(128, 48, 196, 48));
        paintGoal2.setColor(ContextCompat.getColor(getContext(), R.color.colorRateGoal));
        paintGoal2.setStyle(Paint.Style.STROKE);
        paintGoal2.setStrokeWidth(10);
        paintGoal2.setPathEffect(new DashPathEffect(new float[]{ 5.0f, 5.0f }, 0));
        paintText = new Paint();
//        paintText.setColor(Color.argb(128, 0, 0, 0));
        paintText.setColor(ContextCompat.getColor(getContext(), R.color.colorText));
        paintText.setStyle(Paint.Style.FILL);
        paintText.setStrokeWidth(5);
        paintText.setAntiAlias(true);
        paintScale = new Paint();
//        paintScale.setColor(Color.argb(128, 0, 0, 0));
        paintScale.setColor(ContextCompat.getColor(getContext(), R.color.colorFrame));
        paintScale.setStyle(Paint.Style.STROKE);
        paintScale.setStrokeWidth(5);
        paintScale.setAntiAlias(true);
        paintUnit1 = new Paint();
//        paintUnit1.setColor(Color.argb(255, 255, 0, 0));
        paintUnit1.setColor(ContextCompat.getColor(getContext(), R.color.colorWeight));
        paintUnit1.setStyle(Paint.Style.FILL_AND_STROKE);
        paintUnit1.setStrokeWidth(2.5f);
        paintUnit1.setAntiAlias(true);
        paintUnit2 = new Paint();
//        paintUnit2.setColor(Color.argb(255, 0, 255, 0));
        paintUnit2.setColor(ContextCompat.getColor(getContext(), R.color.colorRate));
        paintUnit2.setStyle(Paint.Style.FILL_AND_STROKE);
        paintUnit2.setStrokeWidth(2.5f);
        paintUnit2.setAntiAlias(true);
    }
    @Override
    public void onDraw(Canvas canvas) {
        ViewGroup parent = (ViewGroup) getParent();
        parentX = parent.getX();
        parentY = parent.getY();
        parentWidth = parent.getWidth();
        parentHeight = parent.getHeight();


        List<Point> points2 = toPoints(data2);
        for(DateLongValueFloat d : data2) {
            System.out.println(d);
        }
        float min2 = min(minY(points2), goal2) - 5;
        float max2 = max(maxY(points2), goal2) + 5;
        drawLines(canvas, paintData2, from, to, min2, max2, points2);
        drawLines(canvas, paintGoal2, from, to, min2, max2, Arrays.asList(new Point(from, goal2), new Point(to, goal2)));

        List<Point> points1 = toPoints(data1);
        float min1 = min(minY(points1), goal1) - 5;
        float max1 = max(maxY(points1), goal1) + 5;
        drawLines(canvas, paintData1, from, to, min1, max1, points1);
        drawLines(canvas, paintGoal1, from, to, min1, max1, Arrays.asList(new Point(from, goal1), new Point(to, goal1)));

        drawFrame(canvas);

        drawScaleAndTextYAxis(canvas, paintScale, paintText, from, to);
        drawScaleAndTextXAxis1(canvas, paintScale, paintText, min1, max1);
        drawScaleAndTextXAxis2(canvas, paintScale, paintText, min2, max2);

        drawUnit1(canvas, paintUnit1, unit1);
        drawUnit2(canvas, paintUnit2, unit2);
    }

    private void drawFrame(Canvas canvas) {
        canvas.drawLine(x(parentWidth * 0.1), y(parentHeight * 0.1), x(parentWidth * 0.9), y(parentHeight * 0.1), paintXAxis);
        canvas.drawLine(x(parentWidth * 0.1), y(parentHeight * 0.1), x(parentWidth * 0.1), y(parentHeight * 0.9), paintYAxis);
        canvas.drawLine(x(parentWidth * 0.9), y(parentHeight * 0.1), x(parentWidth * 0.9), y(parentHeight * 0.9), paintYAxis);
    }

    private void drawLines(Canvas canvas, Paint paint, float minX, float maxX, float minY, float maxY, List<Point> points) {
        if (points == null || points.size() == 0) {
            return;
        }
        float[] pts = new float[4 * (points.size() - 1)];
        for (int i = 0; i < points.size() - 1; i++) {
            pts[4 * i] =     x((points.get(i).getX() - minX) / (maxX - minX) * parentWidth * 0.8 + parentWidth * 0.1);
            pts[4 * i + 1] = y((points.get(i).getY() - minY) / (maxY - minY) * parentHeight * 0.8 + parentHeight * 0.1);
            pts[4 * i + 2] = x((points.get(i + 1).getX() - minX) / (maxX - minX) * parentWidth * 0.8 + parentWidth * 0.1);
            pts[4 * i + 3] = y((points.get(i + 1).getY() - minY) / (maxY - minY) * parentHeight * 0.8 + parentHeight * 0.1);
        }
        canvas.drawLines(pts, paint);
    }

    private float y(double y) {
        return (float) (parentY + parentHeight - y);
    }

    private float x(double x) {
        return (float) (parentX + x);
    }

    private List<Point> toPoints(List<DateLongValueFloat> datas) {
        List<Point> points = new ArrayList<>();
        if (datas == null) {
            return points;
        }
        for (DateLongValueFloat data: datas) {
            points.add(new Point(data.getDate(), data.getValue()));
        }
        return points;
    }

    private float minY(List<Point> points) {
        if (points == null || points.size() == 0) {
            return 0;
        }
        float min = points.get(0).getY();
        for (int i = 1; i < points.size(); i++) {
            float value = points.get(i).getY();
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    private float maxY(List<Point> points) {
        if (points == null || points.size() == 0) {
            return 0;
        }
        float min = points.get(0).getY();
        for (int i = 1; i < points.size(); i++) {
            float value = points.get(i).getY();
            if (min < value) {
                min = value;
            }
        }
        return min;
    }

    private float min(float v1, float v2) {
        return v1 < v2 ? v1 : v2;
    }

    private float max(float v1, float v2) {
        return v1 > v2 ? v1 : v2;
    }

    private void drawScaleAndTextYAxis(Canvas canvas, Paint paintScale, Paint paintText, long from, long to) {
        final long span = to - from;
        final long ONE_WEEK = 1000L * 60  *60 * 24 * 7;
        final long THIRTY_DAYS = 1000L * 60  *60 * 24 * 30;
        final long NINETY_DAYS = 1000L * 60  *60 * 24 * 90;
        final long HALF_YEAR = 1000L * 60  *60 * 24 * 180;
        final long ONE_YEAR = 1000L * 60 * 60 * 24 * 365;
        final long THREE_YEAR = 1000L * 60 * 60 * 24 * 365 * 3;

        Calendar first = Calendar.getInstance();
        first.setTime(new Date(from));
        first.add(Calendar.DAY_OF_MONTH, 1);
        first.set(Calendar.HOUR_OF_DAY, 0);
        first.set(Calendar.MINUTE, 0);
        first.set(Calendar.SECOND, 0);
        first.set(Calendar.MILLISECOND, 0);

        if (span == ONE_WEEK) {
            while(first.getTime().getTime() <= to) {
                long time = first.getTime().getTime();
                float x = x( ((float)(time - from)) / (to - from) * parentWidth * 0.8 + parentWidth * 0.1);
                float y = y(parentHeight * 0.1);
                float y2 = y(parentHeight * 0.125);
                canvas.drawLine(x, y, x, y2, paintScale);
                paintText.setTextSize((float) (parentHeight * 0.1 * 0.33)); // 余白の3分の1
                String text = new SimpleDateFormat("M/d").format(first.getTime());
                float width = paintText.measureText(text);
                canvas.drawText(text, x - (width / 2), y + (float) (parentHeight * 0.1 * 0.33), paintText);
                first.add(Calendar.DAY_OF_MONTH, 1);
            }
        } else if (span == THIRTY_DAYS) {
            Calendar cal = Calendar.getInstance();
            while(first.getTime().getTime() <= to) {
                long time = first.getTime().getTime();
                cal.setTime(new Date(time));
                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
                if(dayOfMonth == 1 || (dayOfMonth % 5 == 0 && dayOfMonth < 30)) {
                    float x = x( ((float)(time - from)) / (to - from) * parentWidth * 0.8 + parentWidth * 0.1);
                    float y = y(parentHeight * 0.1);
                    float y2 = y(parentHeight * 0.125);
                    canvas.drawLine(x, y, x, y2, paintScale);
                    paintText.setTextSize((float) (parentHeight * 0.1 * 0.33)); // 余白の3分の1
                    String text = new SimpleDateFormat("M/d").format(first.getTime());
                    float width = paintText.measureText(text);
                    canvas.drawText(text, x - (width / 2), y + (float) (parentHeight * 0.1 * 0.33), paintText);
                }
                first.add(Calendar.DAY_OF_MONTH, 1);
            }
        } else if (span == NINETY_DAYS) {
            Calendar cal = Calendar.getInstance();
            while(first.getTime().getTime() <= to) {
                long time = first.getTime().getTime();
                cal.setTime(new Date(time));
                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
                if(dayOfMonth == 1 || (dayOfMonth % 10 == 0 && dayOfMonth < 30)) {
                    float x = x( ((float)(time - from)) / (to - from) * parentWidth * 0.8 + parentWidth * 0.1);
                    float y = y(parentHeight * 0.1);
                    float y2 = y(parentHeight * 0.125);
                    canvas.drawLine(x, y, x, y2, paintScale);
                    paintText.setTextSize((float) (parentHeight * 0.1 * 0.33)); // 余白の3分の1
                    String text = new SimpleDateFormat("M/d").format(first.getTime());
                    float width = paintText.measureText(text);
                    canvas.drawText(text, x - (width / 2), y + (float) (parentHeight * 0.1 * 0.33), paintText);
                }
                first.add(Calendar.DAY_OF_MONTH, 1);
            }
        } else if (span == HALF_YEAR) {
            first.add(Calendar.MONTH, 1);
            first.set(Calendar.DAY_OF_MONTH, 1);
            while(first.getTime().getTime() <= to) {
                long time = first.getTime().getTime();
                float x = x( ((float)(time - from)) / (to - from) * parentWidth * 0.8 + parentWidth * 0.1);
                float y = y(parentHeight * 0.1);
                float y2 = y(parentHeight * 0.125);
                canvas.drawLine(x, y, x, y2, paintScale);
                paintText.setTextSize((float) (parentHeight * 0.1 * 0.33)); // 余白の3分の1
                String text = new SimpleDateFormat("M/d").format(first.getTime());
                float width = paintText.measureText(text);
                canvas.drawText(text, x - (width / 2), y + (float) (parentHeight * 0.1 * 0.33), paintText);
                first.add(Calendar.MONTH, 1);
            }
        } else if (span == ONE_YEAR) {
            first.add(Calendar.MONTH, 1);
            first.set(Calendar.DAY_OF_MONTH, 1);
            while(first.getTime().getTime() <= to) {
                long time = first.getTime().getTime();
                float x = x( ((float)(time - from)) / (to - from) * parentWidth * 0.8 + parentWidth * 0.1);
                float y = y(parentHeight * 0.1);
                float y2 = y(parentHeight * 0.125);
                canvas.drawLine(x, y, x, y2, paintScale);
                paintText.setTextSize((float) (parentHeight * 0.1 * 0.33)); // 余白の3分の1
                String text = new SimpleDateFormat("M").format(first.getTime());
                float width = paintText.measureText(text);
                canvas.drawText(text, x - (width / 2), y + (float) (parentHeight * 0.1 * 0.33), paintText);
                first.add(Calendar.MONTH, 1);
            }
        } else if (span == THREE_YEAR) {
            first.add(Calendar.MONTH, 1);
            first.set(Calendar.DAY_OF_MONTH, 1);
            while(first.getTime().getTime() <= to) {
                if (first.get(Calendar.MONTH) % 3 == 1) {
                    long time = first.getTime().getTime();
                    float x = x(((float) (time - from)) / (to - from) * parentWidth * 0.8 + parentWidth * 0.1);
                    float y = y(parentHeight * 0.1);
                    float y2 = y(parentHeight * 0.125);
                    canvas.drawLine(x, y, x, y2, paintScale);
                    paintText.setTextSize((float) (parentHeight * 0.1 * 0.25)); // 余白の3分の1
                    String text = new SimpleDateFormat("yy/M").format(first.getTime());
                    float width = paintText.measureText(text);
                    canvas.drawText(text, x - (width / 2), y + (float) (parentHeight * 0.1 * 0.25), paintText);
                }
                first.add(Calendar.MONTH, 1);
            }
        }
    }

    private void drawScaleAndTextXAxis1(Canvas canvas, Paint paintScale, Paint paintText, float min, float max) {
        int value = (int) min + 1;
        while (value < max) {
            float x1 = (float) (parentWidth * 0.1);
            float x2 = (float) (parentWidth * 0.11);
            float y = y(( (value - min) / (max - min)) * parentHeight * 0.8 + parentHeight * 0.1);
            canvas.drawLine(x1, y, x2, y, paintScale);
            float size = (float) (parentHeight * 0.1 * 0.33); // 余白の3分の1
            paintText.setTextSize(size); // 余白の3分の1
            float width = paintText.measureText(String.valueOf(value));
            if (value % 5 == 0) {
                float x3 = (float) (parentWidth * 0.12);
                canvas.drawLine(x2, y, x3, y, paintScale); // メモリを伸ばす
                canvas.drawText(String.valueOf(value), (float) (x1 - width * 1.5), (float) (y + size * 0.5), paintText);
            }
//            canvas.drawText(String.valueOf(value), x, y, paintText);
            value++;
        }
    }

    private void drawScaleAndTextXAxis2(Canvas canvas, Paint paintScale, Paint paintText, float min, float max) {
        int value = (int) min + 1;
        while (value < max) {
            float x1 = (float) (parentWidth * 0.9);
            float x2 = (float) (parentWidth * 0.89);
            float y = y(( (value - min) / (max - min)) * parentHeight * 0.8 + parentHeight * 0.1);
            canvas.drawLine(x1, y, x2, y, paintScale);
            float size = (float) (parentHeight * 0.1 * 0.33); // 余白の3分の1
            paintText.setTextSize(size); // 余白の3分の1
            float width = paintText.measureText(String.valueOf(value));
            if (value % 5 == 0) {
                float x3 = (float) (parentWidth * 0.88);
                canvas.drawLine(x2, y, x3, y, paintScale); // メモリを伸ばす。
                canvas.drawText(String.valueOf(value), (float) (x1 + width * 0.5), (float) (y + size * 0.5), paintText);
            }
//            canvas.drawText(String.valueOf(value), x, y, paintText);
            value++;
        }
    }

    public void drawUnit1(Canvas canvas, Paint paint, String unit) {
        float size = (float) (parentHeight * 0.1 * 0.33);
        paint.setTextSize(size);
        String text = "(" + unit + ")";
        float width = paint.measureText(text);
        canvas.drawText(text, (float) (parentWidth * 0.1 * 0.5 - width * 0.5), (float) (parentY + parentHeight * 0.1 * 0.5 + size * 0.5), paint);
    }

    public void drawUnit2(Canvas canvas, Paint paint, String unit) {
        float size = (float) (parentHeight * 0.1 * 0.33);
        paint.setTextSize(size);
        String text = "(" + unit + ")";
        float width = paint.measureText(text);
        canvas.drawText(text, (float) (parentWidth * 0.9 + parentHeight * 0.1 * 0.5 - width * 0.5), (float) (parentY + parentHeight * 0.1 * 0.5 + size * 0.5), paint);
    }


}
