package org.macho.beforeandafter.graphe2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.macho.beforeandafter.R;
import org.macho.beforeandafter.RecordDao;
import org.macho.beforeandafter.record.Record;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by yuukimatsushima on 2017/08/13.
 */

/**
 * 1week
 * 1month
 * 3month
 * 6month
 * 1year
 * 3year
 */
public class GrapheFragment extends Fragment {
    private static final int unSelected = Color.rgb(196, 196, 196);
    private static final int selected = Color.rgb(96, 96, 96);
    private static final String WEIGHT_FORMAT = "%2.1fkg";
    private static final String RATE_FORMAT = "%2.1f%% ";

    private LinearLayout linearLayout;
    private FrameLayout frameLayout;
    private HorizontalScrollView scrollView;
    private LineGrapheView2 grapheView;

    private ImageView weekButton;
    private ImageView monthButton;
    private ImageView yearButton;

    private TextView currentDate;
    private SimpleDateFormat format;
    private TextView currentWeight;
    private TextView currentRate;

    private long fromX;
    private long toX;
    private long unitX;
    private List<Data> dataList = new ArrayList<>();

    private GrapheMode mode = GrapheMode.MONTH;

    private boolean postLazyScroll;

//    private final View.OnClickListener onWeekButtonClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            if (mode == GrapheMode.WEEK) {
//                return;
//            }
//            mode = GrapheMode.WEEK;
//            refresh();
//            weekButton.setColorFilter(selected);
//            monthButton.setColorFilter(unSelected);
//            yearButton.setColorFilter(unSelected);
//        }
//    };
//
//    private final View.OnClickListener onMonthButtonClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            if (mode == GrapheMode.MONTH) {
//                return;
//            }
//            mode = GrapheMode.MONTH;
//            refresh();
//            weekButton.setColorFilter(unSelected);
//            monthButton.setColorFilter(selected);
//            yearButton.setColorFilter(unSelected);
//        }
//    };
//
//    private final View.OnClickListener onYearButtonClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            if (mode == GrapheMode.YEAR) {
//                return;
//            }
//            mode = GrapheMode.YEAR;
//            refresh();
//            weekButton.setColorFilter(unSelected);
//            monthButton.setColorFilter(unSelected);
//            yearButton.setColorFilter(selected);
//        }
//    };

    public static Fragment getInstance() {
        Fragment fragment = new GrapheFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_graphe2, container, false);
        String lang = Locale.getDefault().getLanguage();
        if ("ja".equals(lang)) {
            format = new SimpleDateFormat("yyyy年MM月dd日");
        } else {
            format = new SimpleDateFormat("MM/dd/yyyy");
        }
//        insertData();
        loadData();

        toX = System.currentTimeMillis() + mode.getSpan() / 2;
        fromX = toX - mode.getSpan();

        return view;
    }

    /*
    public void insertData() {
        long current = new Date().getTime() + 1000L * 60 *60 * 24 * 15;
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*31, 60.0f, 25f  , null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*30, 59.7f, 25.2f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*29, 59.3f, 25.0f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*28, 59.5f, 24.8f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*27, 59.2f, 24.6f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*26, 59.0f, 24.8f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*25, 58.7f, 24.4f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*24, 58.8f, 24.0f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*23, 58.4f, 24.2f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*22, 58.5f, 24.2f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*21, 58.2f, 24.0f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*20, 57.6f, 23.8f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*19, 57.2f, 23.6f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*18, 57.5f, 23.5f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*17, 57.1f, 23.3f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*16, 56.9f, 23.1f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*15, 56.7f, 23.5f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*14, 56.3f, 23.0f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*13, 56.1f, 22.8f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*12, 56.5f, 22.8f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*11, 56.2f, 22.5f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24*10, 56.1f, 22.4f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24* 9, 56.0f, 22.3f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24* 8, 55.8f, 22.5f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24* 7, 55.6f, 22.6f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24* 6, 55.6f, 22.2f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24* 5, 55.9f, 22.0f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24* 4, 55.2f, 21.9f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24* 3, 54.8f, 21.6f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24* 2, 54.4f, 21.7f, null, null, null));
        RecordDao.getInstance().register(new Record(current - 1000L*60*60*24* 1, 54.0f, 21.5f, null, null, null));
    }
    */

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        linearLayout = (LinearLayout) view.findViewById(R.id.linear_layout);
        frameLayout = new FrameLayout(getContext());

        refresh();

        linearLayout.addView(frameLayout);

        currentDate = (TextView) view.findViewById(R.id.current_date);
        currentDate.setText(format.format(new Date()));
        currentWeight = (TextView) view.findViewById(R.id.current_weight);
        currentRate = (TextView) view.findViewById(R.id.current_rate);
    }

    private void refresh() {
        unitX = mode.getUnit();

//        loadData(); // 最初だけ読み込む

        frameLayout.removeAllViews();
        scrollView = new CustomHorizontalScrollView(getContext());
        grapheView = new LineGrapheView2(getContext(), fromX, toX, unitX, dataList);
        scrollView.addView(grapheView);
        frameLayout.addView(scrollView);
        frameLayout.addView(new MyView(getContext(), dataList));

        // scrollview.post(scrollView::scrollTo(x, y))だと、ガクンとなるため以下の実装
        frameLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                scrollView.getViewTreeObserver().removeOnPreDrawListener(this);

                int initialPositionX = (grapheView.getWidth() - scrollView.getWidth()) / 2;
                scrollView.setScrollX(initialPositionX);
                return false;
            }
        });
    }

    private void lazyScroll(long currentDateTime) {
        fromX = currentDateTime - mode.getSpan() / 2;
        toX = currentDateTime + mode.getSpan() / 2;
        postLazyScroll = true;
        refresh();
    }

    private void loadData() {
        dataList = new ArrayList<>();
        Data weightData = new Data();
        weightData.setColor(Color.rgb(229, 57, 53));
        weightData.setLeftYAxis(true);
        List<Poin2> weightPoints = new ArrayList<>();

        Data rateData = new Data();
        rateData.setColor(Color.rgb(67, 160, 71));
        rateData.setLeftYAxis(false);
        List<Poin2> ratePoints = new ArrayList<>();

        for (Record record : RecordDao.getInstance().findAll()) {
            if (record.getWeight() > 0) {
                weightPoints.add(new Poin2(record.getDate(), record.getWeight()));
            }
            if (record.getRate() > 0) {
                ratePoints.add(new Poin2(record.getDate(), record.getRate()));
            }
        }

        weightData.setPoin2s(weightPoints);
        dataList.add(weightData);

        rateData.setPoin2s(ratePoints);
        dataList.add(rateData);

        Data weightGoalData = new Data();
        weightGoalData.setColor(Color.rgb(229, 83, 80));
        weightGoalData.setLeftYAxis(true);
        List<Poin2> weightGoalPoints = new ArrayList<>();

        Data rateGoalData = new Data();
        rateGoalData.setColor(Color.rgb(129, 199, 132));
        rateGoalData.setLeftYAxis(false);
        List<Poin2> rateGoalPoints = new ArrayList<>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        float goal1 = preferences.getFloat("GOAL_WEIGHT", 50);
        float goal2 = preferences.getFloat("GOAL_RATE", 20);

        long goalFromX = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 25;
        long goalToX = System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365 * 25;
        weightGoalPoints.add(new Poin2(goalFromX, goal1));
        weightGoalPoints.add(new Poin2(goalToX, goal1));
        weightGoalData.setPoin2s(weightGoalPoints);
        weightGoalData.setDottedLine(true);
        dataList.add(weightGoalData);

        rateGoalPoints.add(new Poin2(goalFromX, goal2));
        rateGoalPoints.add(new Poin2(goalToX, goal2));
        rateGoalData.setPoin2s(rateGoalPoints);
        rateGoalData.setDottedLine(true);
        dataList.add(rateGoalData);
    }

    public enum GrapheMode {
        WEEK(1000L * 60 * 60 * 24 * 7 * 3, 1000L * 60 * 60 * 24),
        MONTH(1000L * 60 * 60 * 24 * 31 * 3, 1000L * 60 * 60 * 24),
        YEAR(1000L * 60 * 60 * 24 * 365 * 3, 1000L * 60 * 60 * 24 * 31);
        private GrapheMode(long span, long unit) {
            this.span = span;
            this.unit = unit;
        }
        private long span;
        private long unit;
        public long getSpan() {
            return span;
        }
        public long getUnit() {
            return unit;
        }
    }

    public class CustomHorizontalScrollView extends HorizontalScrollView {
        public CustomHorizontalScrollView(Context context) {
            super(context);
        }

        @Override
        public void onScrollChanged(int newX, int newY, int oldX, int oldY) {
            View child = getChildAt(0);
            if (child == null) {
                return;
            }

            int position = newX + getWidth() / 2;
            final long dateTime = (long) (fromX + (toX - fromX) * (((float) position) / child.getWidth()));

            if (postLazyScroll) {
                postLazyScroll = false;
//            } else if ((newX == 1 || newX == child.getWidth() - getWidth() - 1)) {
            } else if ((newX == 0 || newX == child.getWidth() - getWidth())) {
                scrollView.post(new Runnable() {
                    public void run() {
                        lazyScroll(dateTime); // postにしないと、HorizontalScrollView.onTouchEvent内のVelocityTracker.clearでNullPo
                    }
                });
            }

            final String text = format.format(new Date(dateTime));
            currentDate.setText(text);

            float currentWeightValue = 0f;
            Data weightData = dataList.get(0);
            List<Poin2> weightPoints = weightData.getPoin2s();
            for (int m = 0; m < weightPoints.size(); m++) {
                Poin2 point = weightPoints.get(m);
                if (dateTime == point.getDateTime()) {
                    currentWeightValue = point.getValue();
                }
                if (m < weightPoints.size() - 1) {
                    Poin2 point2 = weightPoints.get(m + 1);
                    if (point.getDateTime() < dateTime && dateTime < point2.getDateTime()) {
                        currentWeightValue = point.getValue() + (point2.getValue() - point.getValue()) * (dateTime - point.getDateTime()) / (point2.getDateTime() - point.getDateTime());
                    }
                }
            }
            currentWeight.setText(String.format(WEIGHT_FORMAT, currentWeightValue));

            float currentRateValue = 0f;
            Data rateData = dataList.get(1);
            List<Poin2> ratePoints = rateData.getPoin2s();
            for (int m = 0; m < ratePoints.size(); m++) {
                Poin2 point = ratePoints.get(m);
                if (dateTime == point.getDateTime()) {
                    currentRateValue = point.getValue();
                }
                if (m < ratePoints.size() - 1) {
                    Poin2 point2 = ratePoints.get(m + 1);
                    if (point.getDateTime() < dateTime && dateTime < point2.getDateTime()) {
                        currentRateValue = point.getValue() + (point2.getValue() - point.getValue()) * (dateTime - point.getDateTime()) / (point2.getDateTime() - point.getDateTime());
                    }
                }
            }
            currentRate.setText(String.format(RATE_FORMAT, currentRateValue));
        }

//        @Override
//        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//            View child = getChildAt(0);
//            if (child == null) {
//                return;
//            }
////            scrollTo((child.getWidth() - getWidth()) / 2, 0);
//            scrollTo(500, 100);
//        }
    }

}
