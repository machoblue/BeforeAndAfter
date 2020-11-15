package org.macho.beforeandafter.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.dashboard_frag.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.dashboard.view.DashboardProgressView
import org.macho.beforeandafter.dashboard.view.DashboardProgressViewListener
import org.macho.beforeandafter.dashboard.view.DashboardSummaryView
import org.macho.beforeandafter.shared.di.FragmentScoped
import org.macho.beforeandafter.shared.extensions.setText
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.shared.util.LogUtil
import org.macho.beforeandafter.shared.view.commondialog.CommonDialog
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

@FragmentScoped
class DashboardFragment @Inject constructor(): DaggerFragment(), DashboardContract.View {

    @Inject
    override lateinit var presenter: DashboardContract.Presenter

    @Inject
    lateinit var dialog: CommonDialog

    private var currentNativeAd: UnifiedNativeAd? = null

    // MARK: - Lifecycle
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dashboard_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshAd()

        AdUtil.initializeMobileAds(context!!)
        AdUtil.loadBannerAd(adView, context!!)
        adLayout.visibility = if (AdUtil.isBannerAdHidden(context!!)) View.GONE else View.VISIBLE

        setHeightButton.setOnClickListener {
            val action = DashboardFragmentDirections.actionDashboardFragmentToEditHeightFragment()
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentNativeAd?.destroy()
        presenter.dropView()
    }

    override fun toggleEmptyView(show: Boolean) {
        emptyView.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun updateWeightSummary(show: Boolean, firstWeight: Float?, bestWeight: Float?, latestWeight: Float?, goalWeight: Float?) {
        if (!show) {
            linearLayout.findViewById<CardView>(R.id.weight_summary_card_id)?.let {
                linearLayout.removeView(it)
            }
            return
        }

        val weightSummaryView = linearLayout.findViewById<DashboardSummaryView>(R.id.weight_summary_view_id) ?: DashboardSummaryView(context!!).also {
            addCardView(it, R.id.weight_summary_view_id, R.id.weight_summary_card_id)
        }

        weightSummaryView.update(latestWeight, firstWeight, bestWeight, goalWeight, (goalWeight ?: 0f) == 0f) {
            val action = DashboardFragmentDirections.actionDashboardFragmentToEditGoalFragment2()
            findNavController().navigate(action)
        }
    }

    override fun updateWeightProgress(show: Boolean, elapsedDay: Int, firstWeight: Float?, bestWeight: Float?, latestWeight: Float?, goalWeight: Float?) {
        if (!show) {
            linearLayout.findViewById<CardView>(R.id.weight_progress_card_id)?.let {
                linearLayout.removeView(it)
            }
            return
        }

        val weightProgressView = linearLayout.findViewById<DashboardProgressView>(R.id.weight_progress_view_id) ?: DashboardProgressView(context!!).also {
            addCardView(it, R.id.weight_progress_view_id, R.id.weight_progress_card_id)
        }

        weightProgressView.update(elapsedDay, firstWeight, bestWeight, latestWeight, goalWeight, goalWeight == 0f, object: DashboardProgressViewListener {
            override fun onSetGoalButtonClicked() {
                val action = DashboardFragmentDirections.actionDashboardFragmentToEditGoalFragment2()
                findNavController().navigate(action)
            }

            override fun onElapsedDayHelpButtonClicked() {
                dialog.show(parentFragmentManager, 0, getString(R.string.elapsed_days_help_message), getString(R.string.ok))
            }

            override fun onAchieveExpectHelpButtonClicked() {
                dialog.show(parentFragmentManager, 0, getString(R.string.archive_expect_days_help_message), getString(R.string.ok))
            }
        })
    }

    override fun updateBMI(show: Boolean, showSetHeightButton: Boolean, bmi: Float?, bmiClass: String?, idealWeight: Float?) {
        bmiCardView.visibility = if (show) View.VISIBLE else View.GONE
        setHeightButton.visibility = if (showSetHeightButton) View.VISIBLE else View.GONE
        bmiTextView.text = bmi?.let { String.format("%.1f", it) } ?: "--.-"
        bmiView.update(bmi ?: 0f)
        bmiClassTextView.text = String.format("( %s )", bmiClass ?: "--")
        val idealWeightString = idealWeight?.let { String.format("%.1f", it) } ?: "--.--"
        idealWeightTextView.text = String.format("%s kg", idealWeightString)
    }

    private fun addCardView(cardContentView: View, contentViewId: Int, cardId: Int) {
        cardContentView.id = contentViewId
        val cardView = CardView(context!!)
        cardView.id = cardId
        cardView.addView(cardContentView, ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        val childCount = linearLayout.childCount
        linearLayout.addView(cardView, max(0, childCount - 1), LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).also {
            val marginInPx = convertDpToPx(context!!, 12)
            it.setMargins(marginInPx, marginInPx, marginInPx, 0)
        })
    }

    private fun convertDpToPx(context: Context, dp: Int): Int {
        val d: Float = context.resources.displayMetrics.density
        return (dp * d + 0.5).toInt()
    }

    private fun refreshAd() {
        val builder = AdLoader.Builder(requireContext(), getString(R.string.admob_unit_id_native))

        builder.forUnifiedNativeAd { unifiedNativeAd ->
            if (isDetached) {
                unifiedNativeAd.destroy()
                return@forUnifiedNativeAd
            }

            currentNativeAd?.destroy()
            currentNativeAd = unifiedNativeAd
            val adView = layoutInflater
                    .inflate(R.layout.ad_unified, null) as UnifiedNativeAdView
            populateUnifiedNativeAdView(unifiedNativeAd, adView)
            ad_frame.removeAllViews()
            ad_frame.addView(adView)
        }

        val adOptions = NativeAdOptions.Builder().build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(p0: Int) {
                LogUtil.e(this@DashboardFragment, "Failed to load ad. code: $p0")
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateUnifiedNativeAdView(nativeAd: UnifiedNativeAd, adView: UnifiedNativeAdView) {
        // Set the media view.
        adView.mediaView = adView.findViewById<MediaView>(R.id.ad_media)

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView.setMediaContent(nativeAd.mediaContent)

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon.drawable
            )
            adView.iconView.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)
    }
}