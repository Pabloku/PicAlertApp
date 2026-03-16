package com.pabloku.picalertsapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pabloku.picalertsapp.feature.onboarding.domain.GetTutorEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

data class MainUiState(
    val isLoading: Boolean = true,
    val hasTutorEmail: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    getTutorEmailUseCase: GetTutorEmailUseCase
) : ViewModel() {

    val uiState: StateFlow<MainUiState> = getTutorEmailUseCase()
        .map { storedEmail ->
            val hasTutorEmail = !storedEmail.isNullOrBlank()
            Timber.tag(TAG).i("Resolved app start route hasTutorEmail=%s", hasTutorEmail)
            MainUiState(
                isLoading = false,
                hasTutorEmail = hasTutorEmail
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MainUiState()
        )

    private companion object {
        const val TAG = "PicAlertsMonitor"
    }
}
