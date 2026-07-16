package com.refreshing.learnenglishwords.data.progress

import com.refreshing.learnenglishwords.data.local.dao.ProgressDao
import com.refreshing.learnenglishwords.data.local.entity.WordProgressEntity
import com.refreshing.learnenglishwords.domain.progress.ProgressRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepositoryImpl @Inject constructor(
    private val progressDao: ProgressDao,
) : ProgressRepository {

    override fun observeAllActiveProgress(): Flow<List<WordProgressEntity>> =
        progressDao.observeAllActiveProgress()

    override suspend fun getProgressForSubtopic(subtopicUid: String): List<WordProgressEntity> =
        progressDao.getProgressForSubtopic(subtopicUid)

    override suspend fun getProgressForTopic(topicKey: String): List<WordProgressEntity> =
        progressDao.getProgressForTopic(topicKey)

    override suspend fun upsertProgress(progress: WordProgressEntity) =
        progressDao.upsertProgress(progress)

    override suspend fun resetAll() = progressDao.deleteAllProgress()

    override suspend fun resetTopic(topicKey: String) =
        progressDao.deleteProgressForTopic(topicKey)

    override suspend fun resetSubtopic(subtopicUid: String) =
        progressDao.deleteProgressForSubtopic(subtopicUid)
}
