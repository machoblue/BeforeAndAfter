package org.macho.beforeandafter.preference.dashboard

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class DashboardItemDetailsLookup(private val recyclerView: RecyclerView): ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        return recyclerView.findChildViewUnder(event.x, event.y)?.let {
            (recyclerView.getChildViewHolder(it) as DashboardItemAdapter.ViewHolder).getItemDetails()
        }
    }
}