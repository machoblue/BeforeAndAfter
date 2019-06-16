package org.macho.beforeandafter.shared.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.MainActivity
import org.macho.beforeandafter.record.RecordModule

@Module
abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = [RecordModule::class])
    abstract fun mainActivity(): MainActivity
}