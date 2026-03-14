package com.pabloku.picalertsapp.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "database_metadata")
data class DatabaseMetadataEntity(
    @PrimaryKey val key: String,
    val value: String
)
