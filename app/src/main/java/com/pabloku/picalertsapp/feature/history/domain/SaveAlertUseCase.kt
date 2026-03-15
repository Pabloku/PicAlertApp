package com.pabloku.picalertsapp.feature.history.domain

import javax.inject.Inject

class SaveAlertUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(
        imageUri: String,
        category: String,
        timestamp: Long
    ) {
        historyRepository.saveAlert(
            imageUri = imageUri,
            category = category,
            timestamp = timestamp
        )
    }
}
