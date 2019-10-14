package org.macho.beforeandafter.preference.restore

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.di.FragmentScoped

@Module
abstract class RestoreModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun restoreFragment(): RestoreFragment

    @ActivityScoped
    @Binds
    abstract fun restorePresenter(restorePresenter: RestorePresenter): RestoreContract.Presenter
}
