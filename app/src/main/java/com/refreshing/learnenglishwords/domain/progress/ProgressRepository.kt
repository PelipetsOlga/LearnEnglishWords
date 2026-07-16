package com.refreshing.learnenglishwords.domain.progress

import com.refreshing.learnenglishwords.data.local.entity.WordProgressEntity

interface ProgressRepository {
    suspend fun getProgressForSubtopic(subtopicUid: String): List<WordProgressEntity>
    suspend fun upsertProgress(progress: WordProgressEntity)
    suspend fun resetTopic(topicKey: String)
    suspend fun resetSubtopic(subtopicUid: String)
}
