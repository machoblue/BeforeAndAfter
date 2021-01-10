package org.macho.beforeandafter.preference.editscale

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.di.FragmentScoped

@Module
abstract class EditScaleModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun editScaleFragment(): EditScaleFragment

    @ActivityScoped
    @Binds
    abstract fun editScalePresenter(editScalePresenter: EditScalePresenter): EditScaleContract.Presenter
}