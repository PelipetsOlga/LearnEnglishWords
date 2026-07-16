package com.refreshing.learnenglishwords.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "word_translation",
    primaryKeys = ["wordUid", "language"],
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["wordUid"],
            childColumns = ["wordUid"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("wordUid")],
)
data class WordTranslationEntity(
    val wordUid: String,
    val language: String,
    val text: String,
)
