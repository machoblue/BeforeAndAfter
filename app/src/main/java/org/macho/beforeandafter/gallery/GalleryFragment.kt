package org.macho.beforeandafter.gallery

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_gallery.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.RecordDao


class GalleryFragment: Fragment() {
    companion object {
        fun getInstance(): Fragment {
            return GalleryFragment()
        }
    }

    private var frontImagePaths: MutableList<String> = mutableListOf()
    private var sideImagePaths: MutableList<String> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tabHost.setup()

        var tab1 = tabHost.newTabSpec("tab1")
        tab1.setIndicator(resources.getString(R.string.front))
        tab1.setContent(R.id.tab1)
        tabHost.addTab(tab1)

        var tab2 = tabHost.newTabSpec("tab2")
        tab2.setIndicator(resources.getString(R.string.side))
        tab2.setContent(R.id.tab2)
        tabHost.addTab(tab2)

        frontGridView.layoutManager = GridLayoutManager(context, 3)
        frontGridView.setHasFixedSize(true)

        sideGridView.layoutManager = GridLayoutManager(context, 3)
        sideGridView.setHasFixedSize(true)
    }

    override fun onStart() {
        super.onStart()
        for (record in RecordDao.findAll()) {
            frontImagePaths.add(record.frontImagePath ?: "")
            sideImagePaths.add(record.sideImagePath ?: "")
        }
        frontGridView.adapter = GridAdapter(this, frontImagePaths)
        sideGridView.adapter = GridAdapter(this, sideImagePaths)
    }

    override fun onStop() {
        super.onStop()
        frontImagePaths.clear()
        sideImagePaths.clear()
    }
}