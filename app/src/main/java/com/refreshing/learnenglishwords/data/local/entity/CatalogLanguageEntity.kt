package com.refreshing.learnenglishwords.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catalog_language")
data class CatalogLanguageEntity(
    @PrimaryKey val languageCode: String,
    val isMain: Boolean,
    val position: Int,
)
