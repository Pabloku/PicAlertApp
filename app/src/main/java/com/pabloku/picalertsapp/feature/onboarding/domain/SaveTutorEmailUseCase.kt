package com.pabloku.picalertsapp.feature.onboarding.domain

import com.pabloku.picalertsapp.feature.onboarding.data.TutorEmailLocalDataSource
import javax.inject.Inject

class SaveTutorEmailUseCase @Inject constructor(
    private val tutorEmailLocalDataSource: TutorEmailLocalDataSource
) {

    suspend operator fun invoke(email: String): Boolean {
        val normalizedEmail = email.trim()
        if (!EMAIL_REGEX.matches(normalizedEmail)) {
            return false
        }

        tutorEmailLocalDataSource.saveTutorEmail(normalizedEmail)
        return true
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
