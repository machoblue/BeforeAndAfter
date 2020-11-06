package org.macho.beforeandafter.dashboard

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.dashboard_frag.*
import kotlinx.android.synthetic.main.dashboard_frag.emptyView
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.data.record.Record
import org.macho.beforeandafter.shared.di.FragmentScoped
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.shared.util.LogUtil
import javax.inject.Inject
import kotlinx.android.synthetic.main.dashboard_frag.emptyView as emptyView1

@FragmentScoped
class DashboardFragment @Inject constructor(): DaggerFragment(), DashboardContract.View {

    @Inject
    override lateinit var presenter: DashboardContract.Presenter

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

    // MARK: - DashboardContract.View
    override fun updateDashboard(firstRecord: Record?, bestRecord: Record?, latestRecord: Record?, goalWeight: Float, currentBMI: Float) {
        if (firstRecord == null) {
            emptyView.visibility = View.VISIBLE
            return
        }

        emptyView.visibility = View.GONE

        setWeight(firstRecord.weight, firstWeightTextView)
        setWeight(bestRecord?.weight ?: 0f, bestWeightTextView)
        setWeight(latestRecord?.weight ?: 0f, currentWeightTextView)
        setWeight(goalWeight, goalWeightTextView)

        setGoalButton.visibility = if (goalWeight == 0f) View.VISIBLE else View.GONE
    }

    // MARK: Private
    private fun setWeight(weight: Float, textView: TextView) {
        val weightUnit = "kg"
        val weightTemplate = "%s $weightUnit"

        val numberText = if (weight == 0f) "--.--" else weight.toString()
        val formattedText = String.format(weightTemplate, numberText)
        val numberIndex = formattedText.indexOf(numberText)
        textView.text = SpannableString(formattedText).also {
            it.setSpan(
                    RelativeSizeSpan(0.66f),
                    numberIndex + numberText.length,
                    formattedText.length,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
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