package org.macho.beforeandafter

import android.app.Application
import org.macho.beforeandafter.shared.di.DaggerAppComponent


class BeforeAndAfterApp: Application() {
    val component = DaggerAppComponent.create()

    override fun onCreate() {
        super.onCreate()
    }
}