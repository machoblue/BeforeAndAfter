package org.macho.beforeandafter.record

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
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
import org.macho.beforeandafter.shared.GlideApp
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.AdUtil
import java.io.File
import java.util.*
import javax.inject.Inject

@ActivityScoped
class RecordFragment @Inject constructor() : DaggerFragment(), RecordContract.View {
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

            GlideApp.with(this@RecordFragment)
                    .load(Uri.fromFile(File(context.filesDir, currentRecord.frontImagePath ?: "")))
                    .sizeMultiplier(.4f)
                    .thumbnail(.1f)
                    .error(ColorDrawable(Color.LTGRAY))
                    .into(holder.frontImage)

            GlideApp.with(this@RecordFragment)
                    .load(Uri.fromFile(File(context.filesDir, currentRecord.sideImagePath ?: "")))
                    .sizeMultiplier(.4f)
                    .thumbnail(.1f)
                    .error(ColorDrawable(Color.LTGRAY))
                    .into(holder.sideImage)

            holder.date.text = "%1\$tF %1\$tH:%1\$tM:%1\$tS".format(Date(currentRecord.date))
            holder.weight.text = "%.2fkg".format(currentRecord.weight)
            holder.rate.text = "%.2f％".format(currentRecord.rate)
            holder.memo.text = currentRecord.memo
        }

        inner class RecordItemViewHolder(view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
            val frontImage: ImageView = view.findViewById(R.id.frontImage)
            val sideImage: ImageView = view.findViewById(R.id.sideImage)
            val date: TextView = view.findViewById(R.id.date)
            val weight: TextView = view.findViewById(R.id.weight)
            val rate: TextView = view.findViewById(R.id.rate)
            val memo: TextView = view.findViewById(R.id.memo)

            init {
                view.setOnClickListener {_ ->
                    presenter.openEditRecord(this@RecordAdapter.records.get(adapterPosition).date)
                }
            }
        }
    }
}