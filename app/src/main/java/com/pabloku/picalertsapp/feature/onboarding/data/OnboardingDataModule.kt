package com.pabloku.picalertsapp.feature.onboarding.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnboardingDataModule {

    @Provides
    @Singleton
    fun provideTutorEmailLocalDataSource(
        tutorPreferences: TutorPreferences
    ): TutorEmailLocalDataSource = tutorPreferences
}
