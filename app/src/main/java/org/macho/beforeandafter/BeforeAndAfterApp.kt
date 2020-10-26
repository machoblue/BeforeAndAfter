package org.macho.beforeandafter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
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
        createNotificationChannel()
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
                        schema.get("RecordDto")?.addField("otherImagePath1", String::class.java)
                        schema.get("RecordDto")?.addField("otherImagePath2", String::class.java)
                        schema.get("RecordDto")?.addField("otherImagePath3", String::class.java)
                        @Suppress("UNUSED_CHANGED_VALUE")
                        currentVersion++
                    }
                }
                .build()
        Realm.setDefaultConfiguration(config)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val id = getString(R.string.channel_id)
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}