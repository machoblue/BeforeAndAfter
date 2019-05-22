package org.macho.beforeandafter.record

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.macho.beforeandafter.BeforeAndAfterConst
import org.macho.beforeandafter.ImageUtil
import org.macho.beforeandafter.R
import org.macho.beforeandafter.RecordDao
import java.io.File
import java.util.*

class RecordFragment: Fragment() {

    companion object {
        const val EDIT_REQUEST_CODE = 98
        fun getInstance(): Fragment {
            return RecordFragment()
        }
    }

    private lateinit var listView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private var items: MutableList<Record> = mutableListOf()
    private lateinit var recordAdapter: RecordAdapter
    private val imageCache = ImageCache()


    override fun onCreateView(layoutInflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("*** RecordFragment.onViewCreated ***")
        listView = view.findViewById(R.id.record_list_view)
        listView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fab = view.findViewById(R.id.fab)
        fab.setOnClickListener { _ ->
            val intent = Intent(context, EditActivity::class.java)
            startActivityForResult(intent, EDIT_REQUEST_CODE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        imageCache.clear()
        loaderManager.destroyLoader(1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode != EDIT_REQUEST_CODE) {
            return
        }

        if (data == null) {
            return
        }

        when (data.getIntExtra("TYPE", 0)) {
            1 -> {
                val index = data.getIntExtra("INDEX", 0)
                items.removeAt(index)
                recordAdapter.notifyItemRemoved(index)
            }
            2 -> {
                if (data.getBooleanExtra("ISNEW", false)) {
                    val date = data.getLongExtra("DATE", 0)
                    val record = RecordDao.find(date)!!
                    items.add(record)
                    recordAdapter.notifyItemInserted(items.size - 1)

                    val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
                    val reviewed = preferences.getBoolean("REVIEWED", false)
                    if (!reviewed) {
                        val firstRecord = RecordDao.findAll().get(0)
                        val diff = firstRecord.weight - record.weight
                        if (diff > 1f) {
                            ReviewDialog.newInstance().show(fragmentManager, "REVIEW_DIALOG")
                        }
                    }

                } else {
                    val index = data.getIntExtra("INDEX", 0)
                    val record = items.get(index)
                    val after = RecordDao.find(record.date)!!

                    record.weight = after.weight
                    record.rate = after.rate
                    record.memo = after.memo
                    record.frontImagePath = after.frontImagePath
                    record.sideImagePath = after.sideImagePath
                    recordAdapter.notifyItemChanged(index)
                }

            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loaderManager.restartLoader(1, null, object: LoaderManager.LoaderCallbacks<MutableList<Record>> {
            override fun onCreateLoader(id: Int, args: Bundle?): Loader<MutableList<Record>> {
                val loader = RecordLoader(this@RecordFragment.context!!)
                loader.forceLoad()
                return loader
            }

            override fun onLoadFinished(loader: Loader<MutableList<Record>>, data: MutableList<Record>?) {
                println("*** RecordFragment.onActivityCreated.LoaderCAllback.onLoadFinish ***")
                items = data ?: mutableListOf()
                recordAdapter = RecordAdapter(this@RecordFragment.context!!, items, 100, imageCache)
                listView.adapter = recordAdapter
            }

            override fun onLoaderReset(loader: Loader<MutableList<Record>>) {
                recordAdapter.clear()
            }
        })
    }

    class RecordLoader(context: Context): AsyncTaskLoader<MutableList<Record>>(context) {
        override fun loadInBackground(): MutableList<Record>? {
            return mutableListOf(*(RecordDao.findAll().toTypedArray()))
        }
    }

    inner class RecordAdapter(val context: Context, val records: MutableList<Record>, val viewHeight: Int, val imageCache: ImageCache)
        : RecyclerView.Adapter<RecordAdapter.RecordItemViewHolder>() {
        val layoutInflater = LayoutInflater.from(context)

        override fun getItemCount(): Int {
            return records.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordItemViewHolder {
            return RecordItemViewHolder(layoutInflater.inflate(R.layout.list_item_record, parent, false))
        }

        override fun onBindViewHolder(holder: RecordItemViewHolder, position: Int) {
            val currentRecord = records.get(position)

            val frontImageFile = "${BeforeAndAfterConst.PATH}/${currentRecord.frontImagePath}"
            val frontImageBitmap = imageCache.get(frontImageFile)
            if (frontImageBitmap == null) {
                val task = ImageLoadTask(position, frontImageFile, viewHeight, this@RecordAdapter)
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null)
                holder.frontImage.setImageDrawable(null)
            } else {
                holder.frontImage.setImageBitmap(frontImageBitmap)
            }

            val sideImageFile = "${BeforeAndAfterConst.PATH}/${currentRecord.sideImagePath}"
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

        fun clear() {
            records.clear()
            notifyDataSetChanged()
        }

        inner class RecordItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
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
                    val intent = Intent(this@RecordAdapter.context, EditActivity::class.java)
                    intent.putExtra("INDEX", adapterPosition)
                    intent.putExtra("DATE", this@RecordAdapter.records.get(adapterPosition).date)
                    this@RecordFragment.startActivityForResult(intent, EDIT_REQUEST_CODE)
                }
            }
        }

        private inner class ImageLoadTask(val position: Int, val filePath: String, val viewHeight: Int, val adapter: RecyclerView.Adapter<RecordItemViewHolder>)
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