package org.macho.beforeandafter.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.galleries_frag.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.data.record.Record
import org.macho.beforeandafter.shared.data.record.RecordDao
import org.macho.beforeandafter.shared.data.record.RecordDaoImpl
import java.util.*

class GalleriesFragment: Fragment() {

    private val titles = arrayOf(
        R.string.front,
        R.string.side,
        R.string.other1,
        R.string.other2,
        R.string.other3
    )

    private var recordDao: RecordDao = RecordDaoImpl() // TODO: take from Dagger

    private lateinit var pagerAdapter: GalleryPagerAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.galleries_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagerAdapter = GalleryPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = context?.getString(titles[position]) ?: ""
        }.attach()
    }

    override fun onResume() {
        super.onResume()

        pagerAdapter.records = recordDao.findAll()
            .filter { (it.frontImagePath?.isNotEmpty() ?: false) || (it.sideImagePath?.isNotEmpty() ?: false) }
            .sortedBy { -it.date }

        pagerAdapter.notifyDataSetChanged()
    }
}

class GalleryPagerAdapter(val fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {

    var records: List<Record> = mutableListOf()

    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return GalleryFragment.newInstance(getImagePaths(position))
    }

    // Workaround: notifyDataSetChanged doesn't refresh fragment.
    // https://stackoverflow.com/a/63591029/8834586
    override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
        val tag = "f${holder.itemId}"
        val fragment = fragmentManager.findFragmentByTag(tag) as? GalleryFragment
        if (fragment != null) {
            fragment.imagePaths = getImagePaths(position)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    private fun getImagePaths(position: Int): List<GalleryPhoto> {
        return when(position) {
            0 -> records.map { GalleryPhoto(it.frontImagePath ?: "", Date(it.date), it.weight, it.rate) }.filter { it.fileName.isNotEmpty() }
            1 -> records.map { GalleryPhoto(it.sideImagePath ?: "", Date(it.date), it.weight, it.rate) }.filter { it.fileName.isNotEmpty() }
            2 -> records.map { GalleryPhoto(it.otherImagePath1 ?: "", Date(it.date), it.weight, it.rate) }.filter { it.fileName.isNotEmpty() }
            3 -> records.map { GalleryPhoto(it.otherImagePath2 ?: "", Date(it.date), it.weight, it.rate) }.filter { it.fileName.isNotEmpty() }
            4 -> records.map { GalleryPhoto(it.otherImagePath3 ?: "", Date(it.date), it.weight, it.rate) }.filter { it.fileName.isNotEmpty() }
            else -> throw RuntimeException("This line shouldn't be reached.")
        }
    }
}

