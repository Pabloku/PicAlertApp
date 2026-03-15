package com.pabloku.picalertsapp.feature.history.domain

import javax.inject.Inject

class ClearHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke() {
        historyRepository.clearHistory()
    }
}
