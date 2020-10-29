package org.macho.beforeandafter.shared.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.MainActivity
import org.macho.beforeandafter.alarmsettingdialog.AlarmSettingModule
import org.macho.beforeandafter.graph.GraphModule
import org.macho.beforeandafter.preference.PreferenceModule
import org.macho.beforeandafter.preference.alarm.AlarmModule
import org.macho.beforeandafter.preference.backup.BackupModule
import org.macho.beforeandafter.preference.editgoal.EditGoalModule
import org.macho.beforeandafter.preference.restore.RestoreModule
import org.macho.beforeandafter.record.RecordModule
import org.macho.beforeandafter.record.editaddrecord.EditAddRecordModule

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
        BackupModule::class,
        RestoreModule::class,
        AlarmModule::class,
        AlarmSettingModule::class
    ])
    abstract fun mainActivity(): MainActivity
}