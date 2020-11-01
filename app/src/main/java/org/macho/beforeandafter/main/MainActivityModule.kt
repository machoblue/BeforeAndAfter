package org.macho.beforeandafter.main

import dagger.Binds
import dagger.Module
import org.macho.beforeandafter.shared.di.ActivityScoped

@Module
abstract class MainActivityModule {
    @ActivityScoped
    @Binds
    abstract fun mainPresenter(mainPresenter: MainPresenter): MainContract.Presenter
}