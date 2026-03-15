package com.pabloku.picalertsapp.feature.monitoring.data

import com.pabloku.picalertsapp.core.network.OpenAiRetrofit
import com.pabloku.picalertsapp.feature.monitoring.data.remote.OpenAiApi
import com.pabloku.picalertsapp.feature.monitoring.data.remote.OpenAiMonitoringRepository
import com.pabloku.picalertsapp.feature.monitoring.domain.MonitoringRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object MonitoringApiModule {

    @Provides
    @Singleton
    fun provideOpenAiApi(
        @OpenAiRetrofit retrofit: Retrofit
    ): OpenAiApi = retrofit.create(OpenAiApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class MonitoringDataModule {

    @Binds
    @Singleton
    abstract fun bindMonitoringRepository(
        repository: OpenAiMonitoringRepository
    ): MonitoringRepository
}
