package org.macho.beforeandafter

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration


class BeforeAndAfterApp: Application() {
    override fun onCreate() {
        super.onCreate()

        configureRealm()
    }

    private fun configureRealm() {
        Realm.init(applicationContext)
        val config = RealmConfiguration.Builder()
                .schemaVersion(1)
                .migration { realm, oldVersion, newVersion ->
                    val schema = realm.schema
                    if (oldVersion == 0L) {
                        schema.get("RecordDto").addField("memo", String::class.java)
                    }
                }
                .build()
        Realm.setDefaultConfiguration(config)
    }
}