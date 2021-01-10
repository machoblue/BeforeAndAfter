package org.macho.beforeandafter.gallery

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.GlideApp
import org.macho.beforeandafter.shared.util.WeightScale
import java.io.File
import java.text.DateFormat

class GridAdapter(val fragment: androidx.fragment.app.Fragment, val weightScale: WeightScale): androidx.recyclerview.widget.RecyclerView.Adapter<GridAdapter.ViewHolder>() {
    private val layoutInflater = LayoutInflater.from(fragment.context)
    private val context = fragment.context!!
    var items: List<GalleryPhoto> = mutableListOf()

    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.grid_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        if (item.fileName.isEmpty()) {
            holder.imageView.setImageDrawable(ColorDrawable(Color.GRAY))
            return
        }

        GlideApp.with(fragment)
                .load(Uri.fromFile(File(context.filesDir, item.fileName)))
                .thumbnail(.1f)
                .error(ColorDrawable(Color.GRAY))
                .into(holder.imageView)

        holder.dateText.text = dateFormat.format(item.dateTime)
        val weightText = "${item.weight?.let { String.format("%.1f", it) } ?: "-"}${weightScale.weightUnitText}"
        val rateText = "${item.rate?.let { String.format("%.1f", it) } ?: "-"}%"
        holder.weightAndRateText.text = "${weightText}/${rateText}"
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(val parent: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(parent) {
        val imageView: ImageView
        val dateText: TextView
        val weightAndRateText: TextView
        init {
            parent.setOnClickListener { view ->
                val intent = Intent(context, PhotoActivity::class.java)
                intent.putExtra(PhotoActivity.INDEX, adapterPosition)
                intent.putExtra(PhotoActivity.PATHS, items.toTypedArray())
                fragment.startActivity(intent)
            }
            imageView = parent.findViewById(R.id.imageView)
            dateText = parent.findViewById(R.id.dateText)
            weightAndRateText = parent.findViewById(R.id.weightAndRateText)
        }
    }
}