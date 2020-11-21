package org.macho.beforeandafter.dashboard.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.dashboard_photo_summary_view.view.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.gallery.GalleryPhoto
import org.macho.beforeandafter.gallery.PhotoActivity
import org.macho.beforeandafter.shared.extensions.loadImage
import java.io.File
import java.text.DateFormat
import java.util.*

typealias PhotoData = GalleryPhoto

class DashboardPhotoSummaryView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)

    init {
        LayoutInflater.from(context).inflate(R.layout.dashboard_photo_summary_view, this, true)
    }

    fun update(title: String, firstPhotoData: PhotoData?, bestPhotoData: PhotoData?, latestPhotoData: PhotoData?) {
        photoSummaryTitle.text = title

        val photoDataList = listOf(firstPhotoData, bestPhotoData, latestPhotoData)
        val imageViews = listOf(firstPhoto, bestPhoto, latestPhoto)
        val dateTexts = listOf(dateText1, dateText2, dateText3)
        val weightAndRateTexts = listOf(weightAndRateText1, weightAndRateText2, weightAndRateText3)

        for ((i, photoData) in photoDataList.withIndex()) {
            imageViews[i].loadImage(context, Uri.fromFile(File(context.filesDir, photoData?.fileName ?: "")))
            imageViews[i].setOnClickListener {
                openPhotoActivity(i, photoDataList.map { it ?: PhotoData("", Date(0L), 0f, 0f) })
            }

            dateTexts[i].text = photoData?.dateTime?.let {
                dateFormat.format(it)
            } ?: "----/--/-- --:--"

            weightAndRateTexts[i].text = "${photoData?.weight ?: "-"}kg/${photoData?.rate ?: "-"}%"
        }
    }

    private fun openPhotoActivity(index: Int, photoDataList: List<PhotoData>) {
        val intent = Intent(context, PhotoActivity::class.java)
        intent.putExtra(PhotoActivity.INDEX, index)
        intent.putExtra(PhotoActivity.PATHS, photoDataList.toTypedArray())
        context.startActivity(intent)
    }
}