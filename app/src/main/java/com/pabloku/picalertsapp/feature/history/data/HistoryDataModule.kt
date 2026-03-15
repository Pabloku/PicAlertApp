package com.pabloku.picalertsapp.feature.history.data

import com.pabloku.picalertsapp.core.database.AppDatabase
import com.pabloku.picalertsapp.feature.history.domain.HistoryRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HistoryRoomModule {

    @Provides
    fun provideAlertDao(appDatabase: AppDatabase): AlertDao = appDatabase.alertDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class HistoryDataModule {

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(
        repository: RoomHistoryRepository
    ): HistoryRepository
}
