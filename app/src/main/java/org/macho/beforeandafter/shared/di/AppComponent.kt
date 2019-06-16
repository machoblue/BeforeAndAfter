package org.macho.beforeandafter.shared.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import org.macho.beforeandafter.BeforeAndAfterApp
import org.macho.beforeandafter.shared.data.RecordRepositoryModule
import javax.inject.Singleton

@Singleton
@Component(modules = [RecordRepositoryModule::class,
    ApplicationModule::class,
    ActivityBindingModule::class,
    AndroidSupportInjectionModule::class])
interface AppComponent: AndroidInjector<BeforeAndAfterApp> {

//    fun getRecordRepository(recordRepositoryImpl: RecordRepositoryImpl): RecordRepository

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): AppComponent.Builder

        fun build(): AppComponent
    }
}