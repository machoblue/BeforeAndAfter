package org.macho.beforeandafter.shared.view.ratingdialog

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.shared.di.FragmentScoped


@Module
abstract class RatingDialogModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun ratingDialog(): RatingDialog
}