package org.macho.beforeandafter.graphe;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabHost;

import org.macho.beforeandafter.R;
import org.macho.beforeandafter.RecordDao;
import org.macho.beforeandafter.record.Record;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private TabHost tabHost;
    private LinearLayout layout1;
    private LinearLayout layout2;
    private LinearLayout layout3;
    private LinearLayout layout4;
    private LinearLayout layout5;
    private LinearLayout layout6;
    private List<DateLongValueFloat> data1;
    private List<DateLongValueFloat> data2;
    private float goal1;
    private float goal2;
    private String unit1;
    private String unit2;
    private TabHost.OnTabChangeListener onTabChangeListener = new TabHost.OnTabChangeListener() {
        @Override
        public void onTabChanged(String tag) {
            long now = new Date().getTime();
            switch(tag) {
                case "tab1":
                    find(now - 1000L * 60 * 60 * 24 * 7, now);
                    layout1.removeAllViews();
                    layout1.addView(new LineGrapheView(getContext(),
                            data1, goal1, unit1,
                            data2, goal2, unit2,
                            now - 1000L * 60 * 60 * 24 * 7, now));
                    break;
                case "tab2":
                    long from = now - (1000L * 60 * 60 * 24 * 30);
                    long to = now;
                    ;
                    find(now - 1000L * 60 * 60 * 24 * 30, now);
                    layout2.removeAllViews();
                    layout2.addView(new LineGrapheView(getContext(),
                            data1, goal1, unit1,
                            data2, goal2, unit2,
                            now - 1000L * 60 * 60 * 24 * 30, now));
                    break;
                case "tab3":
                    find(now - 1000L * 60 * 60 * 24 * 90, now);
                    layout3.removeAllViews();
                    layout3.addView(new LineGrapheView(getContext(),
                            data1, goal1, unit1,
                            data2, goal2, unit2,
                            now - 1000L * 60 * 60 * 24 * 90, now));
                    break;
                case "tab4":
                    find(now - 1000L * 60 * 60 * 24 * 180, now);
                    layout4.removeAllViews();
                    layout4.addView(new LineGrapheView(getContext(),
                            data1, goal1, unit1,
                            data2, goal2, unit2,
                            now - 1000L * 60 * 60 * 24 * 180, now));
                    break;
                case "tab5":
                    find(now - 1000L * 60 * 60 * 24 * 365, now);
                    layout5.removeAllViews();
                    layout5.addView(new LineGrapheView(getContext(),
                            data1, goal1, unit1,
                            data2, goal2, unit2,
                            now - 1000L * 60 * 60 * 24 * 365, now));
                    break;
                case "tab6":
                    find(now - 1000L * 60 * 60 * 24 * 365 * 3, now);
                    layout6.removeAllViews();
                    layout6.addView(new LineGrapheView(getContext(),
                            data1, goal1, unit1,
                            data2, goal2, unit2,
                            now - 1000L * 60 * 60 * 24 * 365 * 3, now));
                    break;

            }
        }
    };
    public static Fragment getInstance() {
        Fragment fragment = new GrapheFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_graphe, container, false);

        tabHost = (TabHost) view.findViewById(android.R.id.tabhost);
        tabHost.setup();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("tab1");
        tab1.setIndicator(getResources().getString(R.string.one_week));
        tab1.setContent(R.id.tab1);
        tabHost.addTab(tab1);
        TabHost.TabSpec tab2 = tabHost.newTabSpec("tab2");
        tab2.setIndicator(getResources().getString(R.string.one_month));
        tab2.setContent(R.id.tab2);
        tabHost.addTab(tab2);
        TabHost.TabSpec tab3 = tabHost.newTabSpec("tab3");
        tab3.setIndicator(getResources().getString(R.string.three_month));
        tab3.setContent(R.id.tab3);
        tabHost.addTab(tab3);
        TabHost.TabSpec tab4 = tabHost.newTabSpec("tab4");
        tab4.setIndicator(getResources().getString(R.string.six_month));
        tab4.setContent(R.id.tab4);
        tabHost.addTab(tab4);
        TabHost.TabSpec tab5 = tabHost.newTabSpec("tab5");
        tab5.setIndicator(getResources().getString(R.string.one_year));
        tab5.setContent(R.id.tab5);
        tabHost.addTab(tab5);
        TabHost.TabSpec tab6 = tabHost.newTabSpec("tab6");
        tab6.setIndicator(getResources().getString(R.string.three_year));
        tab6.setContent(R.id.tab6);
        tabHost.addTab(tab6);
        tabHost.setOnTabChangedListener(onTabChangeListener);
        layout1 = (LinearLayout) view.findViewById(R.id.tab1);
        layout2 = (LinearLayout) view.findViewById(R.id.tab2);
        layout3 = (LinearLayout) view.findViewById(R.id.tab3);
        layout4 = (LinearLayout) view.findViewById(R.id.tab4);
        layout5 = (LinearLayout) view.findViewById(R.id.tab5);
        layout6 = (LinearLayout) view.findViewById(R.id.tab6);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        goal1 = preferences.getFloat("GOAL_WEIGHT", 50);
        goal2 = preferences.getFloat("GOAL_RATE", 20);
        unit1 = "kg";
        unit2 = "％";

        long now = new Date().getTime();
        find(now - 1000L * 60 * 60 * 24 * 7, now);
        layout1.removeAllViews();
        layout1.addView(new LineGrapheView(getContext(),
                data1, goal1, unit1,
                data2, goal2, unit2,
                now - 1000L * 60 * 60 * 24 * 7, now));

        return view;
    }

    public void find(long from, long to) {
        data1 = new ArrayList<>();
        data2 = new ArrayList<>();
        for (Record record : RecordDao.getInstance().find(from, to)) {
            // 0以下の場合描画しない
            if (record.getWeight() > 0) {
                data1.add(new DateLongValueFloat(record.getDate(), record.getWeight()));
            }
            if (record.getRate() > 0) {
                data2.add(new DateLongValueFloat(record.getDate(), record.getRate()));
            }
        }
    }

}
