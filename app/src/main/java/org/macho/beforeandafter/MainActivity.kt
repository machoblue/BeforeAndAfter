package org.macho.beforeandafter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.macho.beforeandafter.record.RecordFragment
import org.macho.beforeandafter.shared.util.AdUtil
import org.macho.beforeandafter.shared.util.LogUtil
import javax.inject.Inject

class MainActivity: DaggerAppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    @Inject
    lateinit var recordFragment: RecordFragment

    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        LogUtil.i(this, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            setUpBottomNavigationBar()
        }

        configureAd()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        setUpBottomNavigationBar()
    }

    private fun setUpBottomNavigationBar() {
        val navGraphIds = listOf(R.navigation.records, R.navigation.gallery, R.navigation.graphe, R.navigation.settings)

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottom_nav.setupWithNavController(
                navGraphIds = navGraphIds,
                fragmentManager = supportFragmentManager,
                containerId = R.id.nav_host_container,
                intent = intent
        )

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, Observer { navController ->
            setupActionBarWithNavController(navController)
        })
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    private fun configureAd() {
        AdUtil.requestConsentInfoUpdateIfNeed(applicationContext)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}