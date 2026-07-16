package com.refreshing.learnenglishwords.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catalog_state")
data class CatalogStateEntity(
    @PrimaryKey val id: Int = 1,
    val schemaVersion: Int,
    val catalogRevision: Long,
    val catalogVersion: String,
    val generatedAt: Long,
    val savedAt: Long,
    val source: String,
)
