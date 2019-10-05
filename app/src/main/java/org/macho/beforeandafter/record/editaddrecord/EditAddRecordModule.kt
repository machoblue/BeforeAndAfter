package org.macho.beforeandafter.record.editaddrecord

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.di.FragmentScoped

@Module
abstract class EditAddRecordModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun editAddRecordFragment(): EditAddRecordFragment

    @ActivityScoped
    @Binds
    abstract fun editAddRecordPresenter(editAddRecordPresenter: EditAddRecordPresenter): EditAddRecordContract.Presenter
}
