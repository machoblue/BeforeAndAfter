package org.macho.beforeandafter.preference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yuukimatsushima on 2017/08/13.
 */

public class PreferenceAdapter extends BaseAdapter {
    private Context context;
    private List<PreferenceItem> items;
    private LayoutInflater layoutInflater;
    public PreferenceAdapter(Context context, List<PreferenceItem> items) {
        this.context = context;
        this.items = items;
        this.layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
//        return items.size(); // バグレポートによるとここでNullPointerExceptionが発生する。
        return items == null ? 0 : items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        }
        PreferenceItem currentItem = items.get(i);
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(currentItem.getTitle());
        ((TextView) convertView.findViewById(android.R.id.text2)).setText(currentItem.getDescription());
        return convertView;
    }
}
