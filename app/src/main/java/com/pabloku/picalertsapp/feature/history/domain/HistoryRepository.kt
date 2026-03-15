package com.pabloku.picalertsapp.feature.history.domain

import com.pabloku.picalertsapp.feature.history.data.AlertEntity
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun observeAlertHistory(): Flow<List<AlertEntity>>

    suspend fun saveAlert(imageUri: String, category: String, timestamp: Long)

    suspend fun clearHistory()
}
