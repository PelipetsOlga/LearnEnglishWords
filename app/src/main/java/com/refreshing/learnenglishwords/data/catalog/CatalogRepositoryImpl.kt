package com.refreshing.learnenglishwords.data.catalog

import com.refreshing.learnenglishwords.core.model.Subtopic
import com.refreshing.learnenglishwords.core.model.Topic
import com.refreshing.learnenglishwords.data.local.dao.SubtopicDao
import com.refreshing.learnenglishwords.data.local.dao.TopicDao
import com.refreshing.learnenglishwords.data.local.entity.SubtopicTitleEntity
import com.refreshing.learnenglishwords.data.local.entity.TopicTitleEntity
import com.refreshing.learnenglishwords.domain.catalog.CatalogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRepositoryImpl @Inject constructor(
    private val topicDao: TopicDao,
    private val subtopicDao: SubtopicDao,
) : CatalogRepository {

    override fun observeTopics(): Flow<List<Topic>> = combine(
        topicDao.observeActiveTopics(),
        topicDao.observeAllActiveTopicTitles(),
        topicDao.observeWordCountsPerTopic(),
    ) { topics, allTitles, wordCounts ->
        val titlesByTopic = allTitles.groupBy { it.topicKey }
        val countByTopic = wordCounts.associate { it.topicKey to it.wordCount }
        topics.map { entity ->
            Topic(
                topicKey = entity.topicKey,
                title = titlesByTopic[entity.topicKey].resolveTitle(),
                wordCount = countByTopic[entity.topicKey] ?: 0,
                progressPercent = 0,
            )
        }
    }

    override fun observeSubtopics(topicKey: String): Flow<List<Subtopic>> = combine(
        subtopicDao.observeActiveSubtopics(topicKey),
        subtopicDao.observeSubtopicTitlesForTopic(topicKey),
        subtopicDao.observeWordCountsForTopic(topicKey),
    ) { subtopics, allTitles, wordCounts ->
        val titlesBySubtopic = allTitles.groupBy { it.subtopicUid }
        val countBySubtopic = wordCounts.associate { it.subtopicUid to it.wordCount }
        subtopics.map { entity ->
            Subtopic(
                subtopicUid = entity.subtopicUid,
                topicKey = entity.topicKey,
                title = titlesBySubtopic[entity.subtopicUid].resolveTitle(),
                wordCount = countBySubtopic[entity.subtopicUid] ?: 0,
                progressPercent = 0,
            )
        }
    }
}

@JvmName("resolveTopicTitle")
private fun List<TopicTitleEntity>?.resolveTitle(locale: String = "en"): String =
    this?.firstOrNull { it.language == locale }?.text
        ?: this?.firstOrNull()?.text
        ?: ""

@JvmName("resolveSubtopicTitle")
private fun List<SubtopicTitleEntity>?.resolveTitle(locale: String = "en"): String =
    this?.firstOrNull { it.language == locale }?.text
        ?: this?.firstOrNull()?.text
        ?: ""
