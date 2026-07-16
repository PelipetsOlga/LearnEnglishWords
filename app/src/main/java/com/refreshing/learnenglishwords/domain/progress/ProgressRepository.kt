package com.refreshing.learnenglishwords.domain.progress

interface ProgressRepository {
    suspend fun resetTopic(topicKey: String)
    suspend fun resetSubtopic(subtopicUid: String)
}
