package org.macho.beforeandafter.preference.backup

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.di.FragmentScoped

@Module
abstract class BackupModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun backupFragment(): BackupFragment

    @ActivityScoped
    @Binds
    abstract fun backupPresenter(backupPresenter: BackupPresenter): BackupContract.Presenter
}
