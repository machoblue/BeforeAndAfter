package org.macho.beforeandafter.shared.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.InitialSettingsActivity
import org.macho.beforeandafter.main.MainActivity
import org.macho.beforeandafter.alarmsettingdialog.AlarmSettingModule
import org.macho.beforeandafter.dashboard.DashboardModule
import org.macho.beforeandafter.graph.GraphModule
import org.macho.beforeandafter.main.MainActivityModule
import org.macho.beforeandafter.preference.PreferenceModule
import org.macho.beforeandafter.preference.alarm.AlarmModule
import org.macho.beforeandafter.preference.backup.BackupModule
import org.macho.beforeandafter.preference.dashboard.DashboardSettingModule
import org.macho.beforeandafter.preference.editgoal.EditGoalModule
import org.macho.beforeandafter.preference.editscale.EditScaleModule
import org.macho.beforeandafter.preference.height.EditHeightModule
import org.macho.beforeandafter.preference.restore.RestoreModule
import org.macho.beforeandafter.record.RecordModule
import org.macho.beforeandafter.record.editaddrecord.EditAddRecordModule
import org.macho.beforeandafter.shared.view.commondialog.CommonDialogModule
import org.macho.beforeandafter.shared.view.commondialog.CommonDialogModule2
import org.macho.beforeandafter.shared.view.ratingdialog.RatingDialogModule

@Module
abstract class ActivityBindingModule {
    // メモ: @ContributesAndroidInjectorにより、MainActivityに対応するComponentが自動生成される。いちいちSubComponentを作らなくてよくなる。
    //　　　　そのComponentにmodulesが設定される。
    @ActivityScoped
    @ContributesAndroidInjector(modules = [
        RecordModule::class,
        EditAddRecordModule::class,
        GraphModule::class,
        PreferenceModule::class,
        EditGoalModule::class,
        EditHeightModule::class,
        DashboardSettingModule::class,
        BackupModule::class,
        RestoreModule::class,
        AlarmModule::class,
        AlarmSettingModule::class,
        CommonDialogModule::class,
        CommonDialogModule2::class,
        MainActivityModule::class,
        DashboardModule::class,
        EditScaleModule::class,
        RatingDialogModule::class
    ])
    abstract fun mainActivity(): MainActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [
        EditScaleModule::class
    ])
    abstract fun initialSettingsActivity(): InitialSettingsActivity
}