package com.pabloku.picalertsapp.feature.history.domain

import com.pabloku.picalertsapp.feature.history.data.AlertEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAlertHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    operator fun invoke(): Flow<List<AlertEntity>> = historyRepository.observeAlertHistory()
}
