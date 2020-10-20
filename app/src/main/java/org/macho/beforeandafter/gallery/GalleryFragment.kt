package org.macho.beforeandafter.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.gallery_frag.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.util.AdUtil


class GalleryFragment(): androidx.fragment.app.Fragment() {

    companion object {
        val IMAGE_PATHS = "IMAGE_PATHS"

        fun newInstance(imagePaths: List<GalleryPhoto>): GalleryFragment {
            var galleryFragment = GalleryFragment()
//            galleryFragment.imagePaths = imagePaths
            galleryFragment.arguments = Bundle().also {
//                it.putStringArray(IMAGE_PATHS, imagePaths.toTypedArray())
//                val list = mutableListOf<String>()
                it.putSerializable(IMAGE_PATHS, imagePaths.toTypedArray())
            }
            return galleryFragment
        }
    }

    var imagePaths: List<GalleryPhoto> = mutableListOf()
        set(imagePaths) {
            field = imagePaths
            adapter?.items = imagePaths
            adapter?.notifyDataSetChanged()
        }

    private var adapter: GridAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.imagePaths = (arguments?.getSerializable(IMAGE_PATHS) as Array<GalleryPhoto>).toList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.gallery_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.setHasFixedSize(true)

        this.adapter = GridAdapter(this)
        this.adapter?.items = imagePaths
        recyclerView.adapter = this.adapter

        AdUtil.loadBannerAd(adView, context!!)
        adLayout.visibility = if (AdUtil.isBannerAdHidden(context!!)) View.GONE else View.VISIBLE
    }
}