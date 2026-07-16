package com.refreshing.learnenglishwords.core.model

data class Subtopic(
    val subtopicUid: String,
    val topicKey: String,
    val title: String,
    val wordCount: Int,
    val progressPercent: Int,
)
