package com.pabloku.picalertsapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pabloku.picalertsapp.feature.history.data.AlertDao
import com.pabloku.picalertsapp.feature.history.data.AlertEntity

@Database(
    entities = [DatabaseMetadataEntity::class, AlertEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alertDao(): AlertDao
}
