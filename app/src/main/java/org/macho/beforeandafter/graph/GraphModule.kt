package org.macho.beforeandafter.graph

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.macho.beforeandafter.shared.di.FragmentScoped

@Module
abstract class GraphModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun graphFragment(): GraphFragment
}