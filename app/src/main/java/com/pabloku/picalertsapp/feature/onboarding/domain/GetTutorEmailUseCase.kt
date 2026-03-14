package com.pabloku.picalertsapp.feature.onboarding.domain

import com.pabloku.picalertsapp.feature.onboarding.data.TutorEmailLocalDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetTutorEmailUseCase @Inject constructor(
    private val tutorEmailLocalDataSource: TutorEmailLocalDataSource
) {
    operator fun invoke(): Flow<String?> = tutorEmailLocalDataSource.getTutorEmail()
}
