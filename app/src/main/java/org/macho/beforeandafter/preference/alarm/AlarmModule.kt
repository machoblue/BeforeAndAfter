package org.macho.beforeandafter.preference.alarm

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.di.FragmentScoped

@Module
abstract class AlarmModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun alarmFragment(): AlarmFragment

    @ActivityScoped
    @Binds
    abstract fun alarmPresenter(alarmPresenter: AlarmPresenter): AlarmContract.Presenter
}