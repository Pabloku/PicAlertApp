package com.pabloku.picalertsapp.feature.history.presentation

import com.pabloku.picalertsapp.feature.history.presentation.model.AlertHistoryItemUiModel

data class HistoryUiState(
    val isMonitoringActive: Boolean = true,
    val tutorEmail: String = "",
    val alerts: List<AlertHistoryItemUiModel> = emptyList()
) {
    val isEmpty: Boolean
        get() = alerts.isEmpty()
}
