package com.refreshing.learnenglishwords.core.model

data class Topic(
    val topicKey: String,
    val title: String,
    val wordCount: Int,
    val progressPercent: Int,
)
