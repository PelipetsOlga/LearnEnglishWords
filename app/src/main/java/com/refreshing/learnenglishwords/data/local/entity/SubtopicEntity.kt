package com.refreshing.learnenglishwords.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subtopic",
    foreignKeys = [
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["topicKey"],
            childColumns = ["topicKey"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("topicKey"),
        Index(value = ["topicKey", "remoteId"], unique = true),
        Index(value = ["topicKey", "subtopicKey"], unique = true),
    ],
)
data class SubtopicEntity(
    @PrimaryKey val subtopicUid: String,
    val topicKey: String,
    val remoteId: Int,
    val subtopicKey: String,
    val position: Int,
    val isActive: Boolean,
    val lastSeenCatalogRevision: Long,
)
