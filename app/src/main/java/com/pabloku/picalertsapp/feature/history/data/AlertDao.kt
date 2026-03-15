package com.pabloku.picalertsapp.feature.history.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: AlertEntity)

    @Query("SELECT * FROM alerts ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<AlertEntity>>

    @Query("DELETE FROM alerts")
    suspend fun clearAll()
}
