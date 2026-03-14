package com.pabloku.picalertsapp.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @ApplicationContext
    fun provideApplicationContext(
        @ApplicationContext context: Context
    ): Context = context
}
