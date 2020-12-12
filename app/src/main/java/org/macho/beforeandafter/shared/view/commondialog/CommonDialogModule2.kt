package org.macho.beforeandafter.shared.view.commondialog

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.shared.di.FragmentScoped

@Module
abstract class CommonDialogModule2 {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun commonDialog2(): CommonDialog2
}