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
import org.macho.beforeandafter.shared.util.WeightScale
import java.io.File
import java.text.DateFormat
import java.util.*
import kotlin.math.roundToInt

typealias PhotoData = GalleryPhoto

class DashboardPhotoSummaryView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)

    init {
        LayoutInflater.from(context).inflate(R.layout.dashboard_photo_summary_view, this, true)
    }

    fun update(title: String, weightUnit: String, firstPhotoData: PhotoData?, bestPhotoData: PhotoData?, latestPhotoData: PhotoData?) {
        photoSummaryTitle.text = title

        val photoDataList = listOf(firstPhotoData, bestPhotoData, latestPhotoData)
        val imageViews = listOf(firstPhoto, bestPhoto, latestPhoto)
        val dateTexts = listOf(dateText1, dateText2, dateText3)
        val weightAndRateTexts = listOf(weightAndRateText1, weightAndRateText2, weightAndRateText3)

        imageViews[0].layoutParams.height = getPhotoHeight()

        val weightScale = WeightScale(context)

        for ((i, photoData) in photoDataList.withIndex()) {
            imageViews[i].loadImage(context, Uri.fromFile(File(context.filesDir, photoData?.fileName ?: "")))
            imageViews[i].setOnClickListener {
                openPhotoActivity(i, photoDataList.map { it ?: PhotoData("", Date(0L), 0f, 0f) })
            }

            dateTexts[i].text = photoData?.dateTime?.let {
                dateFormat.format(it)
            } ?: "----/--/-- --:--"

            weightAndRateTexts[i].text = "${format(photoData?.weight?.let { weightScale.convertFromKg(it) })}$weightUnit/${format(photoData?.rate)}%"
        }
    }

    private fun openPhotoActivity(index: Int, photoDataList: List<PhotoData>) {
        val intent = Intent(context, PhotoActivity::class.java)
        intent.putExtra(PhotoActivity.INDEX, index)
        intent.putExtra(PhotoActivity.PATHS, photoDataList.toTypedArray())
        context.startActivity(intent)
    }

    private fun getPhotoHeight(): Int {
        val cardViewMarginInDp = 12
        val photosSpanInDp = 1
        val displayMetrics = context.resources.displayMetrics
        val displayWidthInDp = displayMetrics.widthPixels / displayMetrics.density
        val photoWidthInDp = (displayWidthInDp - (cardViewMarginInDp * 2 + photosSpanInDp * 2)) / 3
        val photoHeightWidthRatio = 1.25f
        val photoHeightInDp = photoWidthInDp * photoHeightWidthRatio
        val photoHeightInPx = photoHeightInDp * displayMetrics.density
        return photoHeightInPx.toInt()
    }

    private fun format(value: Float?): String {
        return value?.let {
            val roundedValue = (it * 10).roundToInt() / 10f
            String.format("%.1f", roundedValue)
        } ?: "-"
    }
}