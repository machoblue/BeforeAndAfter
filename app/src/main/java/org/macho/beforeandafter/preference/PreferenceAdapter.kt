package org.macho.beforeandafter.preference

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import org.macho.beforeandafter.R

class PreferenceAdapter(val context: Context, private val items: List<PreferenceElement>): BaseAdapter() {
    private val layoutInflater = LayoutInflater.from(context)

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
        when (val item = items.get(i)) {
            is PreferenceItem -> {
                val view = layoutInflater.inflate(android.R.layout.simple_list_item_2, parent, false)
                val text1 = view.findViewById<TextView>(android.R.id.text1)
                text1.setText(item.title)
                text1.setTextColor(ContextCompat.getColor(context, R.color.dark_gray_text))
                val text2 = view.findViewById<TextView>(android.R.id.text2)
                text2.setText(item.description)
                text2.setTextColor(ContextCompat.getColor(context, R.color.light_gray_text))
                return view
            }

            is SectionHeader -> {
                val view = layoutInflater.inflate(R.layout.list_preference_section_header, parent, false)
                view.findViewById<TextView>(R.id.sectionHeaderTitle).setText(item.title)
                return view
            }

            is PreferenceFooter -> {
                val view = layoutInflater.inflate(R.layout.list_preference_footer, parent, false)
                view.findViewById<TextView>(R.id.sectionFooter).setText(item.appVersion)
                return view
            }

            is CheckboxPreferenceItem -> {
                val view = layoutInflater.inflate(R.layout.list_preference_checkbox_item, parent, false)
                view.findViewById<TextView>(R.id.preferenceItemTitle).setText(item.title)
                view.findViewById<TextView>(R.id.preferenceItemDescription).setText(item.description)
                val switch = view.findViewById<Switch>(R.id.pinSwitch)
                switch.isChecked = item.isOn
                switch.setOnCheckedChangeListener { checkbox, isChecked ->
                    item.action(isChecked)
                    checkbox.isChecked = !isChecked // 切り替えをキャンセル。ここでは切り替えず、暗証番号設定の変更が完了した時に切り替える。
                }
                return view
            }

            else -> {
                throw RuntimeException("This line shouldn't be reached.")
            }
        }
    }
}