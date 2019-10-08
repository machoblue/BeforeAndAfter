package org.macho.beforeandafter.record

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.di.FragmentScoped

@Module
abstract class RecordModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun recordFragment(): RecordFragment

    @ActivityScoped
    @Binds
    abstract fun recordPresenter(recordPresenter: RecordPresenter): RecordContract.Presenter
}