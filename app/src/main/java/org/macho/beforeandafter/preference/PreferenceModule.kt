package org.macho.beforeandafter.preference

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.shared.di.FragmentScoped

@Module
abstract class PreferenceModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun preferenceFragment(): PreferenceFragment

    // TODO: implements Presenter
//    @ActivityScoped
//    @Binds
//    abstract fun preferencePresenter(preferencePresenter: PreferencePresenter): PreferenceContract.Presenter
}