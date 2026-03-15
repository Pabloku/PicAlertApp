package com.pabloku.picalertsapp.feature.history.presentation.model

data class AlertHistoryItemUiModel(
    val id: Long,
    val imageUri: String,
    val categoryLabel: String,
    val summary: String,
    val timestampLabel: String
)
