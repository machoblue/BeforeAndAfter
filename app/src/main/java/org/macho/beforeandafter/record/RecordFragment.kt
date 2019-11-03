package org.macho.beforeandafter.record

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.MobileAds
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.record_frag.*
import org.macho.beforeandafter.shared.util.ImageUtil
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.AdUtil
import java.io.File
import java.util.*
import javax.inject.Inject

@ActivityScoped
class RecordFragment @Inject constructor() : DaggerFragment(), RecordContract.View {

    companion object {
        const val EDIT_REQUEST_CODE = 98
    }

    @Inject
    override lateinit var presenter: RecordContract.Presenter

    private lateinit var recordAdapter: RecordAdapter
    private val imageCache = ImageCache()

//    private lateinit var firebaseAnalytics: FirebaseAnalytics

    // MARK: - Lifecycle
    override fun onCreateView(layoutInflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.record_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        fab.setOnClickListener { _ ->
            presenter.openAddRecord()
        }

        MobileAds.initialize(context, getString(R.string.admob_app_id))
        AdUtil.loadBannerAd(adView, context!!)

//        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)

//        firebaseAnalytics.setCurrentScreen(activity!!, "Records", null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        imageCache.clear()
        presenter.dropView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // do nothing
    }


    override fun showItems(items: List<Record>) {
        recordAdapter = RecordAdapter(this.context!!, items, 100, imageCache)
        listView.adapter = recordAdapter
    }

    override fun showAddRecordUI() {
//        val bundle = Bundle()
//        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "myId")
//        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "add")
//        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
//        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

        val action = RecordFragmentDirections.actionRecordFragmentToEditAddRecordFragment()
        findNavController().navigate(action)
    }

    override fun showEditRecordUI(date: Long) {
        val action = RecordFragmentDirections.actionRecordFragmentToEditAddRecordFragment(date)
        findNavController().navigate(action)
    }

    override fun hideEmptyView() {
        emptyView.visibility = View.GONE
        listView.visibility = View.VISIBLE
    }

    override fun showEmptyView() {
        emptyView.visibility = View.VISIBLE
        listView.visibility = View.GONE
    }

    inner class RecordAdapter(val context: Context, val records: List<Record>, val viewHeight: Int, val imageCache: ImageCache)
        : androidx.recyclerview.widget.RecyclerView.Adapter<RecordAdapter.RecordItemViewHolder>() {
        val layoutInflater = LayoutInflater.from(context)

        override fun getItemCount(): Int {
            return records.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordItemViewHolder {
            return RecordItemViewHolder(layoutInflater.inflate(R.layout.list_item_record, parent, false))
        }

        override fun onBindViewHolder(holder: RecordItemViewHolder, position: Int) {
            val currentRecord = records.get(position)

            val frontImageFile = "${context.filesDir}/${currentRecord.frontImagePath}"
            val frontImageBitmap = imageCache.get(frontImageFile)
            if (frontImageBitmap == null) {
                val task = ImageLoadTask(position, frontImageFile, viewHeight, this@RecordAdapter)
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)
                holder.frontImage.setImageDrawable(null)
            } else {
                holder.frontImage.setImageBitmap(frontImageBitmap)
            }

            val sideImageFile = "${context.filesDir}/${currentRecord.sideImagePath}"
            val sideImageBitmap = imageCache.get(sideImageFile)
            if (sideImageBitmap == null) {
                val task = ImageLoadTask(position, sideImageFile, viewHeight, this@RecordAdapter)
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)
                holder.sideImage.setImageDrawable(null)
            } else {
                holder.sideImage.setImageBitmap(sideImageBitmap)
            }

            holder.date.text = "%1\$tF %1\$tH:%1\$tM:%1\$tS".format(Date(currentRecord.date))
            holder.weight.text = "%.2fkg".format(currentRecord.weight)
            holder.rate.text = "%.2f％".format(currentRecord.rate)
            holder.memo.text = currentRecord.memo
        }

        inner class RecordItemViewHolder(view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
            val parent: View = view
            val frontImage: ImageView
            val sideImage: ImageView
            val date: TextView
            val weight: TextView
            val rate: TextView
            val memo: TextView
            init {
                frontImage = view.findViewById(R.id.frontImage)
                sideImage = view.findViewById(R.id.sideImage)
                date = view.findViewById(R.id.date)
                weight = view.findViewById(R.id.weight)
                rate = view.findViewById(R.id.rate)
                memo = view.findViewById(R.id.memo)
                parent.setOnClickListener {_ ->
                    presenter.openEditRecord(this@RecordAdapter.records.get(adapterPosition).date)
                }
            }
        }

        private inner class ImageLoadTask(val position: Int, val filePath: String, val viewHeight: Int, val adapter: androidx.recyclerview.widget.RecyclerView.Adapter<RecordItemViewHolder>)
            : AsyncTask<Void, Void, Boolean>() {

            override fun doInBackground(vararg p0: Void?): Boolean {
                val file = File(filePath)

                if (!file.exists()) {
                    return false
                }

                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true

                BitmapFactory.decodeFile(filePath, options)
                val imageHeight = options.outHeight

                // 縮小率を計算する。1:等倍。2:は1辺が1/2になる
                // 2の累乗以外の数字を指定した場合には、その値以下の2の累乗にまとめられる
                // 3の場合には2、6の場合には4など
                var inSampleSize = 1
                if (imageHeight > viewHeight) {
                    inSampleSize = Math.round(imageHeight.toFloat() / viewHeight.toFloat())
                }

                options.inJustDecodeBounds = false
                options.inSampleSize = inSampleSize

                val tempBitmap = BitmapFactory.decodeFile(filePath, options)
                val orientationModifiedBitmap = ImageUtil.getOrientationModifiedBitmap(tempBitmap, file)

                this@RecordAdapter.imageCache.put(filePath, orientationModifiedBitmap)
                return true
            }

            override fun onPostExecute(result: Boolean?) {
                if (result == null) {
                    return
                }

                if (result) {
                    adapter.notifyItemChanged(position)
                }
            }
        }
    }

}