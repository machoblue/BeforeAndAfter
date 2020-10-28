package org.macho.beforeandafter.alarmsettingdialog

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.di.FragmentScoped

@Module
abstract class AlarmSettingModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun alarmSettingDialog(): AlarmSettingDialog

    @ActivityScoped
    @Binds
    abstract fun alarmSettingPresenter(alarmSettingPresenter: AlarmSettingPresenter): AlarmSettingContract.Presenter
}