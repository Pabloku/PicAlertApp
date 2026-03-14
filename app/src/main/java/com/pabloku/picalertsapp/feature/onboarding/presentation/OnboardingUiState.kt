package com.pabloku.picalertsapp.feature.onboarding.presentation

data class OnboardingUiState(
    val email: String = "",
    val isEmailInvalid: Boolean = false,
    val isCompleted: Boolean = false
)
