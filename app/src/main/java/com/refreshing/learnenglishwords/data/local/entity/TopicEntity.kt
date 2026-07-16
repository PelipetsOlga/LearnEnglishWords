package com.refreshing.learnenglishwords.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topic")
data class TopicEntity(
    @PrimaryKey val topicKey: String,
    val remoteId: Int,
    val position: Int,
    val sourceFile: String,
    val topicRevision: Int,
    val contentHash: String?,
    val expectedWordCount: Int,
    val isActive: Boolean,
    val lastSeenCatalogRevision: Long,
)
