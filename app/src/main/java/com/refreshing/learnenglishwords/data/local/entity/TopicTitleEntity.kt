package com.refreshing.learnenglishwords.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "topic_title",
    primaryKeys = ["topicKey", "language"],
    foreignKeys = [
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["topicKey"],
            childColumns = ["topicKey"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("topicKey")],
)
data class TopicTitleEntity(
    val topicKey: String,
    val language: String,
    val text: String,
)
