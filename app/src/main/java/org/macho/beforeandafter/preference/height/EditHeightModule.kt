package org.macho.beforeandafter.preference.height

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.di.FragmentScoped

@Module
abstract class EditHeightModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun editHeightFragment(): EditHeightFragment

    @ActivityScoped
    @Binds
    abstract  fun editHeightPresenter(editHeightPresenter: EditHeightPresenter): EditHeightContract.Presenter
}