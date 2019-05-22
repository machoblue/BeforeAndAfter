package org.macho.beforeandafter.preference

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class PreferenceAdapter(context: Context, val items: List<PreferenceItem>): BaseAdapter() {
    private val layoutInflater: LayoutInflater
    init {
        layoutInflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getItem(i: Int): Any {
        return items.get(i)
    }

    override fun getView(i: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: layoutInflater.inflate(android.R.layout.simple_list_item_2, parent, false)
        val currentItem = items.get(i)
        view.findViewById<TextView>(android.R.id.text1).setText(currentItem.title)
        view.findViewById<TextView>(android.R.id.text2).setText(currentItem.description)
        return view
    }
}