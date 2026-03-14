package com.pabloku.picalertsapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DatabaseMetadataEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase()
