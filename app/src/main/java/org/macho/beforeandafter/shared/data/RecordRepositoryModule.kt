package org.macho.beforeandafter.shared.data

import dagger.Module
import dagger.Provides
import org.macho.beforeandafter.shared.util.AppExecutors
import javax.inject.Singleton

@Module
class RecordRepositoryModule {

    @Singleton
    @Provides
    fun provideAppExecutor(): AppExecutors = AppExecutors()

    @Singleton
    @Provides
    fun provideRecordDao(): RecordDao = RecordDaoImpl()

    @Singleton
    @Provides
    fun provideRecordRepository(recordRepository: RecordRepositoryImpl): RecordRepository = recordRepository
}