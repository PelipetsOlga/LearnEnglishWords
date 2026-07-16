package com.refreshing.learnenglishwords.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "word",
    foreignKeys = [
        ForeignKey(
            entity = SubtopicEntity::class,
            parentColumns = ["subtopicUid"],
            childColumns = ["subtopicUid"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("subtopicUid")],
)
data class WordEntity(
    @PrimaryKey val wordUid: String,
    val subtopicUid: String,
    val remoteId: Int,
    val position: Int,
    val learningRevision: Int,
    val isActive: Boolean,
    val lastSeenCatalogRevision: Long,
)
