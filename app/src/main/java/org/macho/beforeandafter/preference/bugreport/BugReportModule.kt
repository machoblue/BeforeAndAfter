package org.macho.beforeandafter.preference.bugreport

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.shared.di.FragmentScoped

@Module
abstract class BugReportModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bugReportFragment(): BugReportFragment
}