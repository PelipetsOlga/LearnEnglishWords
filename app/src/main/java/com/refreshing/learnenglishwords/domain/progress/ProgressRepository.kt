package com.refreshing.learnenglishwords.domain.progress

import com.refreshing.learnenglishwords.data.local.entity.WordProgressEntity
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun observeAllActiveProgress(): Flow<List<WordProgressEntity>>
    suspend fun getProgressForSubtopic(subtopicUid: String): List<WordProgressEntity>
    suspend fun getProgressForTopic(topicKey: String): List<WordProgressEntity>
    suspend fun upsertProgress(progress: WordProgressEntity)
    suspend fun resetAll()
    suspend fun resetTopic(topicKey: String)
    suspend fun resetSubtopic(subtopicUid: String)
}
