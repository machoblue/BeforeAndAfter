package org.macho.beforeandafter.preference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.macho.beforeandafter.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuukimatsushima on 2017/08/13.
 */

public class PreferenceFragment extends Fragment {
    private ListView listView;
    private List<PreferenceItem> items;
    private Fragment fragment = this;
    public static Fragment newFragment(final Activity activity) {
        final PreferenceFragment fragment = new PreferenceFragment();
        List<PreferenceItem> items = new ArrayList<>();
        items.add(new PreferenceItem(R.string.goal_title, R.string.goal_description, new PreferenceItem.PreferenceAction() {
            @Override
            public void doPreferenceAction() {
                Intent intent = new Intent(activity.getApplicationContext(), EditGoalFragment.class);
                activity.startActivity(intent);
            }
        }));
        items.add(new PreferenceItem(R.string.delete_all_title, R.string.delete_all_description, new PreferenceItem.PreferenceAction() {
            @Override
            public void doPreferenceAction() {
                DeleteAllDialog.newInstance().show(fragment.getFragmentManager(), "");
            }
        }));
        fragment.setItems(items);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_preference, container, false);
        listView = (ListView) view.findViewById(R.id.preference_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                items.get(i).getAction().doPreferenceAction();
            }
        });
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        listView.setAdapter(new PreferenceAdapter(getContext(), items));
    }
    public void setItems(List<PreferenceItem> items) {
        this.items = items;
    }
}
