package org.macho.beforeandafter

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.realm.FieldAttribute
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmList
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
                .schemaVersion(4)
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

                    if (currentVersion == 2L) {
                        schema.create("RestoreImageDto")
                            .addField("imageFileName", String::class.java, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
                            .addField("driveFileId", String::class.java, FieldAttribute.REQUIRED)
                            .addField("status", Int::class.java, FieldAttribute.REQUIRED)
                        currentVersion++
                    }

                    if (currentVersion == 3L) {
                        schema.create("RealmString").addField("value", String::class.java, FieldAttribute.REQUIRED)
                        schema.get("RecordDto")?.addRealmListField("otherImagePaths", schema.get("RealmString"))
                        @Suppress("UNUSED_CHANGED_VALUE")
                        currentVersion++
                    }
                }
                .build()
        Realm.setDefaultConfiguration(config)
    }
}