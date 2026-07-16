package com.refreshing.learnenglishwords.data.progress

import com.refreshing.learnenglishwords.data.local.dao.ProgressDao
import com.refreshing.learnenglishwords.domain.progress.ProgressRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepositoryImpl @Inject constructor(
    private val progressDao: ProgressDao,
) : ProgressRepository {

    override suspend fun resetTopic(topicKey: String) =
        progressDao.deleteProgressForTopic(topicKey)

    override suspend fun resetSubtopic(subtopicUid: String) =
        progressDao.deleteProgressForSubtopic(subtopicUid)
}
