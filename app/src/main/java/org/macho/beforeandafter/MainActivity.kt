package org.macho.beforeandafter

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.gms.ads.MobileAds
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.macho.beforeandafter.gallery.GalleryFragment
import org.macho.beforeandafter.graphe2.GrapheFragment
import org.macho.beforeandafter.preference.PreferenceFragment
import org.macho.beforeandafter.record.RecordFragment
import org.macho.beforeandafter.shared.util.AdUtil
import javax.inject.Inject

class MainActivity: DaggerAppCompatActivity() {

    private var selectedImageButton: ImageButton? = null
    private var selectedTextView: TextView? = null

    private var colorSelected = 0

//    @Inject
//    lateinit var recordFragmentProvider: Lazy<RecordFragment>
    @Inject
    lateinit var recordFragment: RecordFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        colorSelected = ContextCompat.getColor(this, R.color.colorBottomNaviItemPressed)

        uncheckCurrentButtonAndCheck(item0ImageButton, item0TextView)

//        supportFragmentManager.beginTransaction().replace(R.id.content, recordFragmentProvider.get()).commit()
        supportFragmentManager.beginTransaction().replace(R.id.content, recordFragment).commit()

        configureAd()
    }

    private fun uncheckCurrentButtonAndCheck(imageButton: ImageButton, textView: TextView) {
        selectedImageButton?.clearColorFilter()
        selectedTextView?.setTextColor(Color.rgb(255, 255, 255))

        selectedImageButton = imageButton
        selectedTextView = textView

        selectedImageButton?.setColorFilter(colorSelected)
        selectedTextView?.setTextColor(colorSelected)
    }

    private fun configureAd() {
        AdUtil.requestConsentInfoUpdateIfNeed(applicationContext)

        MobileAds.initialize(this, getString(R.string.admob_app_id))

        AdUtil.loadBannerAd(adView, applicationContext)
    }

    fun onItem0Click(view: View) {
        uncheckCurrentButtonAndCheck(item0ImageButton, item0TextView)

//        supportFragmentManager.beginTransaction().replace(R.id.content, recordFragmentProvider.get()).commit()
        supportFragmentManager.beginTransaction().replace(R.id.content, recordFragment).commit()
    }

    fun onItem1Click(view: View) {
        uncheckCurrentButtonAndCheck(item1ImageButton, item1TextView)
        supportFragmentManager.beginTransaction().replace(R.id.content, GalleryFragment.getInstance()).commit()
    }

    fun onItem2Click(view: View) {
        uncheckCurrentButtonAndCheck(item2ImageButton, item2TextView)
        supportFragmentManager.beginTransaction().replace(R.id.content, GrapheFragment.getInstance()).commit()
    }

    fun onItem3Click(view: View) {
        uncheckCurrentButtonAndCheck(item3ImageButton, item3TextView)
        supportFragmentManager.beginTransaction().replace(R.id.content, PreferenceFragment.newFragment(this)).commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}