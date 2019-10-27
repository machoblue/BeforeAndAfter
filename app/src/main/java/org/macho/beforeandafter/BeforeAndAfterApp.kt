package org.macho.beforeandafter

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.realm.FieldAttribute
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
                .schemaVersion(2)
                .migration { realm, oldVersion, newVersion ->
                    var currentVersion = oldVersion
                    val schema = realm.schema
                    if (currentVersion == 0L) {
                        schema.get("RecordDto")?.addField("memo", String::class.java)
                        currentVersion++

                    }

                    if (currentVersion == 1L) {
                        schema.get("RecordDto")?.setNullable("memo", false)?.transform { obj ->
                            obj.set("memo", if (obj.isNull("memo")) "" else obj.getString("memo"))
                        }
                        currentVersion++
                    }

                }
                .build()
        Realm.setDefaultConfiguration(config)
    }
}