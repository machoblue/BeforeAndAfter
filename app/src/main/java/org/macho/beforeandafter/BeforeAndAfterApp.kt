package org.macho.beforeandafter

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.realm.Realm
import io.realm.RealmConfiguration
import org.macho.beforeandafter.shared.di.DaggerAppComponent


class BeforeAndAfterApp: DaggerApplication() {
//    @Inject
//    lateinit var recordRepository: RecordRepository

    override fun onCreate() {
        super.onCreate()

        configureRealm()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
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