package com.pabloku.picalertsapp.feature.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pabloku.picalertsapp.feature.onboarding.domain.GetTutorEmailUseCase
import com.pabloku.picalertsapp.feature.onboarding.domain.SaveTutorEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    getTutorEmailUseCase: GetTutorEmailUseCase,
    private val saveTutorEmailUseCase: SaveTutorEmailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getTutorEmailUseCase().collect { storedEmail ->
                _uiState.update { currentState ->
                    currentState.copy(
                        email = storedEmail ?: currentState.email
                    )
                }
            }
        }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { currentState ->
            currentState.copy(
                email = email,
                isEmailInvalid = false
            )
        }
    }

    fun onConfirm() {
        viewModelScope.launch {
            val isSaved = saveTutorEmailUseCase(uiState.value.email)
            _uiState.update { currentState ->
                currentState.copy(
                    isEmailInvalid = !isSaved,
                    isCompleted = isSaved
                )
            }
        }
    }

    fun onNavigationHandled() {
        _uiState.update { currentState ->
            currentState.copy(isCompleted = false)
        }
    }
}
