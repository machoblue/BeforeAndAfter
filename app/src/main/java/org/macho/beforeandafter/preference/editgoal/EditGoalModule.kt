package org.macho.beforeandafter.preference.editgoal

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.di.FragmentScoped

@Module
abstract class EditGoalModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun editGoalFragment(): EditGoalFragment

    @ActivityScoped
    @Binds
    abstract fun editGoalPresenter(editGoalPresenter: EditGoalPresenter): EditGoalContract.Presenter
}