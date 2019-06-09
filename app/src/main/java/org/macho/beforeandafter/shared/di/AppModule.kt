package org.macho.beforeandafter.shared.di

import dagger.Module
import dagger.Provides
import org.macho.beforeandafter.shared.data.RecordDao
import org.macho.beforeandafter.shared.data.RecordDaoRealm
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideRecordDao(): RecordDao = RecordDaoRealm()
}