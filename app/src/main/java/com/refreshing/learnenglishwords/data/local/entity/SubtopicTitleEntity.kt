package com.refreshing.learnenglishwords.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "subtopic_title",
    primaryKeys = ["subtopicUid", "language"],
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
data class SubtopicTitleEntity(
    val subtopicUid: String,
    val language: String,
    val text: String,
)
