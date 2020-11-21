package org.macho.beforeandafter.dashboard

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.di.FragmentScoped

@Module
abstract class DashboardModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun dashboardFragment(): DashboardFragment

    @ActivityScoped
    @Binds
    abstract fun dashboardPresenter(dashboardPresenter: DashboardPresenter): DashboardContract.Presenter
}