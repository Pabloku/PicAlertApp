package com.pabloku.picalertsapp.feature.onboarding.data

import kotlinx.coroutines.flow.Flow

interface TutorEmailLocalDataSource {
    fun getTutorEmail(): Flow<String?>

    suspend fun saveTutorEmail(email: String)
}
