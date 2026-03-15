package com.pabloku.picalertsapp.feature.history.data

import com.pabloku.picalertsapp.feature.history.domain.HistoryRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class RoomHistoryRepository @Inject constructor(
    private val alertDao: AlertDao
) : HistoryRepository {

    override fun observeAlertHistory(): Flow<List<AlertEntity>> = alertDao.observeAll()

    override suspend fun saveAlert(imageUri: String, category: String, timestamp: Long) {
        alertDao.insert(
            AlertEntity(
                imageUri = imageUri,
                category = category,
                timestamp = timestamp
            )
        )
    }

    override suspend fun clearHistory() {
        alertDao.clearAll()
    }
}
