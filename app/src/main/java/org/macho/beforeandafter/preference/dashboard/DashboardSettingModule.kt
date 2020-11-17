package org.macho.beforeandafter.preference.dashboard

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.di.FragmentScoped

@Module
abstract class DashboardSettingModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun dashboardSettingFragment(): DashboardSettingFragment

    @Binds
    @ActivityScoped
    abstract fun dashboardSettingPresenter(presenter: DashboardSettingPresenter): DashboardSettingContract.Presenter
}