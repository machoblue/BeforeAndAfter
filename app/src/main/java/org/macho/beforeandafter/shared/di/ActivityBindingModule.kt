package org.macho.beforeandafter.shared.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.MainActivity
import org.macho.beforeandafter.preference.backup.BackupActivity
import org.macho.beforeandafter.preference.backup.BackupModule
import org.macho.beforeandafter.preference.editgoal.EditGoalActivity
import org.macho.beforeandafter.preference.editgoal.EditGoalModule
import org.macho.beforeandafter.preference.restore.RestoreActivity
import org.macho.beforeandafter.preference.restore.RestoreModule
import org.macho.beforeandafter.record.RecordModule
import org.macho.beforeandafter.record.editaddrecord.EditAddRecordActivity
import org.macho.beforeandafter.record.editaddrecord.EditAddRecordModule

@Module
abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = [RecordModule::class, EditAddRecordModule::class])
    abstract fun mainActivity(): MainActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [EditGoalModule::class])
    abstract fun editGoalActivity(): EditGoalActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [EditAddRecordModule::class])
    abstract fun editAddRecordActivity(): EditAddRecordActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [BackupModule::class])
    abstract fun backupActivity(): BackupActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [RestoreModule::class])
    abstract fun restoreActivity(): RestoreActivity

}