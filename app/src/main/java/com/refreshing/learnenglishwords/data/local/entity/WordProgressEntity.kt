package com.refreshing.learnenglishwords.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "word_progress",
    primaryKeys = ["wordUid", "sourceLanguage", "targetLanguage"],
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["wordUid"],
            childColumns = ["wordUid"],
            onDelete = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [Index("wordUid")],
)
data class WordProgressEntity(
    val wordUid: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val learningRevision: Int,
    val failureCount: Int = 0,
    val correctStreak: Int = 0,
    val isLearned: Boolean = false,
    val firstAnsweredAt: Long? = null,
    val lastAnsweredAt: Long? = null,
    val learnedAt: Long? = null,
)
