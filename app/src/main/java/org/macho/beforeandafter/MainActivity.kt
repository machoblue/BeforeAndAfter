package org.macho.beforeandafter

import android.content.Intent
import android.os.Bundle
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

    @Inject
    lateinit var recordFragment: RecordFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.records -> {
                    supportFragmentManager.beginTransaction().replace(R.id.content, recordFragment).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.gallery -> {
                    supportFragmentManager.beginTransaction().replace(R.id.content, GalleryFragment.getInstance()).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.graphe -> {
                    supportFragmentManager.beginTransaction().replace(R.id.content, GrapheFragment.getInstance()).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.settings -> {
                    supportFragmentManager.beginTransaction().replace(R.id.content, PreferenceFragment.newFragment(this)).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    return@setOnNavigationItemSelectedListener false
                }
            }
        }

        supportFragmentManager.beginTransaction().replace(R.id.content, recordFragment).commit()

        configureAd()
    }

    private fun configureAd() {
        AdUtil.requestConsentInfoUpdateIfNeed(applicationContext)

        MobileAds.initialize(this, getString(R.string.admob_app_id))

        AdUtil.loadBannerAd(adView, applicationContext)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}