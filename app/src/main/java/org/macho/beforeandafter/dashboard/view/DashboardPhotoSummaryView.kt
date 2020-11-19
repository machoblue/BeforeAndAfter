package org.macho.beforeandafter.dashboard.view

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.dashboard_photo_summary_view.view.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.gallery.GalleryPhoto
import org.macho.beforeandafter.shared.extensions.loadImage
import java.io.File
import java.text.DateFormat

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

        firstPhoto.loadImage(context, Uri.fromFile(File(context.filesDir, firstPhotoData?.fileName ?: "")))
        dateText1.text = firstPhotoData?.dateTime?.let {
            dateFormat.format(it)
        } ?: "----/--/-- --:--"
        weightAndRateText1.text = "${firstPhotoData?.weight ?: "-"}kg/${firstPhotoData?.rate ?: "-"}%"

        bestPhoto.loadImage(context, Uri.fromFile(File(context.filesDir, bestPhotoData?.fileName ?: "")))
        dateText2.text = bestPhotoData?.dateTime?.let {
            dateFormat.format(it)
        } ?: "----/--/-- --:--"
        weightAndRateText2.text = "${bestPhotoData?.weight ?: "-"}kg/${bestPhotoData?.rate ?: "-"}%"

        latestPhoto.loadImage(context, Uri.fromFile(File(context.filesDir, latestPhotoData?.fileName ?: "")))
        dateText3.text = latestPhotoData?.dateTime?.let {
            dateFormat.format(it)
        } ?: "----/--/-- --:--"
        weightAndRateText3.text = "${latestPhotoData?.weight ?: "-"}kg/${latestPhotoData?.rate ?: "-"}%"
    }
}