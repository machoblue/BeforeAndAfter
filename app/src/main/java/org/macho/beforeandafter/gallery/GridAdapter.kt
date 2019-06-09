package org.macho.beforeandafter.gallery

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import org.macho.beforeandafter.BeforeAndAfterConst
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.GlideApp
import java.io.File

class GridAdapter(val fragment: Fragment, val items: List<String>): RecyclerView.Adapter<GridAdapter.ViewHolder>() {
    private val layoutInflater: LayoutInflater
    private val context: Context
    init {
        this.layoutInflater = LayoutInflater.from(fragment.context)
        this.context = fragment.context!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.grid_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val path = items.get(position)
        GlideApp.with(fragment)
                .load(Uri.fromFile(File(BeforeAndAfterConst.PATH, path ?: "")))
                .thumbnail(.1f)
                .error(ColorDrawable(Color.GRAY))
                .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(val parent: View): RecyclerView.ViewHolder(parent) {
        val imageView: ImageView
        init {
            parent.setOnClickListener { view ->
                val intent = Intent(context, PhotoActivity::class.java)
                intent.putExtra("INDEX", adapterPosition)
                intent.putExtra("PATHS", items.toTypedArray())
                fragment.startActivity(intent)
            }
            imageView = parent.findViewById(R.id.imageView)
        }
    }
}