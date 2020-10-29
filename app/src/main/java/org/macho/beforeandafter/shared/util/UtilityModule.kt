package org.macho.beforeandafter.shared.util

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UtilityModule {

    @Singleton
    @Provides
    fun mailAppLauncher() = MailAppLauncher()
}