package org.macho.beforeandafter.preference.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.util.LogUtil

class DashboardItemAdapter(private val context: Context): RecyclerView.Adapter<DashboardItemAdapter.ViewHolder>() {

    var list: List<DashboardSettingItem> = arrayListOf()
    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        tracker?.let {
            holder.bind(item, it.isSelected(position.toLong()))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.dashboard_setting_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = list.size

    override fun getItemId(position: Int): Long = position.toLong()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var text: TextView = view.findViewById(R.id.text)

        fun bind(value: DashboardSettingItem, isActivated: Boolean = false) {
            text.text = context.getString(value.labelStringKey)
            itemView.isActivated = isActivated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
                object : ItemDetailsLookup.ItemDetails<Long>() {
                    override fun getPosition(): Int = adapterPosition
                    override fun getSelectionKey(): Long? = itemId
                    override fun inSelectionHotspot(e: MotionEvent): Boolean = true
                }
    }
}