package com.pabloku.picalertsapp.feature.history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pabloku.picalertsapp.feature.history.data.AlertEntity
import com.pabloku.picalertsapp.feature.history.domain.ClearHistoryUseCase
import com.pabloku.picalertsapp.feature.history.domain.GetAlertHistoryUseCase
import com.pabloku.picalertsapp.feature.history.presentation.model.AlertHistoryItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getAlertHistoryUseCase: GetAlertHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = getAlertHistoryUseCase()
        .map { alerts ->
            HistoryUiState(
                alerts = alerts.map(::toUiModel)
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = HistoryUiState()
        )

    fun onClearHistoryClick() {
        viewModelScope.launch {
            clearHistoryUseCase()
        }
    }

    private fun toUiModel(alert: AlertEntity): AlertHistoryItemUiModel {
        val normalizedCategory = alert.category.lowercase(Locale.ROOT)
        return AlertHistoryItemUiModel(
            id = alert.id,
            imageUri = alert.imageUri,
            categoryLabel = categoryLabelFor(normalizedCategory),
            summary = summaryFor(normalizedCategory),
            timestampLabel = timestampLabelFor(alert.timestamp)
        )
    }

    private fun categoryLabelFor(category: String): String = when {
        "violence" in category -> "VIOLENCE"
        "sexual" in category || "adult" in category -> "ADULT CONTENT"
        else -> "SUSPICIOUS"
    }

    private fun summaryFor(category: String): String = when {
        "violence" in category ->
            "Potential harmful content detected in a recent WhatsApp media attachment."
        "sexual" in category || "adult" in category ->
            "Sensitive image detected in a WhatsApp chat and an alert email was sent."
        else ->
            "Potentially suspicious media was detected in WhatsApp and logged for review."
    }

    private fun timestampLabelFor(timestamp: Long): String {
        val zoneId = ZoneId.systemDefault()
        val alertDate = Instant.ofEpochMilli(timestamp).atZone(zoneId).toLocalDate()
        val today = Instant.now().atZone(zoneId).toLocalDate()
        val prefix = when {
            alertDate == today -> "TODAY"
            alertDate == today.minusDays(1) -> "YESTERDAY"
            else -> alertDate.format(DATE_FORMATTER)
        }
        val time = Instant.ofEpochMilli(timestamp).atZone(zoneId).format(TIME_FORMATTER)
        return "$prefix, $time"
    }

    private companion object {
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.US)
        val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.US)
    }
}
