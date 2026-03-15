package com.pabloku.picalertsapp.feature.alerting.data

import com.pabloku.picalertsapp.core.network.ResendRetrofit
import com.pabloku.picalertsapp.feature.alerting.domain.AlertingRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object AlertingApiModule {

    @Provides
    @Singleton
    fun provideResendApi(
        @ResendRetrofit retrofit: Retrofit
    ): ResendApi = retrofit.create(ResendApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AlertingDataModule {

    @Binds
    @Singleton
    abstract fun bindAlertingRepository(
        repository: ResendAlertingRepository
    ): AlertingRepository
}
