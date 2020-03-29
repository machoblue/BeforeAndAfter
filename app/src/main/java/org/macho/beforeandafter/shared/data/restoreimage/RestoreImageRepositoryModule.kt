package org.macho.beforeandafter.shared.data.restoreimage

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RestoreImageRepositoryModule {
    @Singleton
    @Provides
    fun provideRestoreImageDao(): RestoreImageDao = RestoreImageDaoImpl()

    @Singleton
    @Provides
    fun provideRestoreImageRepository(restoreImageRepository: RestoreImageRepositoryImpl): RestoreImageRepository = restoreImageRepository
}