package com.refreshing.learnenglishwords.data.catalog

import com.refreshing.learnenglishwords.core.model.Subtopic
import com.refreshing.learnenglishwords.core.model.Topic
import com.refreshing.learnenglishwords.core.model.WordEntry
import com.refreshing.learnenglishwords.data.local.dao.CatalogDao
import com.refreshing.learnenglishwords.data.local.dao.SubtopicDao
import com.refreshing.learnenglishwords.data.local.dao.TopicDao
import com.refreshing.learnenglishwords.data.local.dao.WordDao
import com.refreshing.learnenglishwords.data.local.entity.SubtopicTitleEntity
import com.refreshing.learnenglishwords.data.local.entity.TopicTitleEntity
import com.refreshing.learnenglishwords.domain.catalog.CatalogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRepositoryImpl @Inject constructor(
    private val topicDao: TopicDao,
    private val subtopicDao: SubtopicDao,
    private val wordDao: WordDao,
    private val catalogDao: CatalogDao,
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
                title = titlesByTopic[entity.topicKey].resolveTopicTitle(),
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
                title = titlesBySubtopic[entity.subtopicUid].resolveSubtopicTitle(),
                wordCount = countBySubtopic[entity.subtopicUid] ?: 0,
                progressPercent = 0,
            )
        }
    }

    override fun observeSubtopicTitle(subtopicUid: String): Flow<String> =
        subtopicDao.observeSubtopicTitles(subtopicUid).map { it.resolveSubtopicTitle() }

    override fun observeMainLanguage(): Flow<String> =
        catalogDao.observeMainLanguageCode().map { it ?: "en" }

    override fun observeWordEntries(subtopicUid: String): Flow<List<WordEntry>> =
        wordDao.observeActiveWordsWithTranslations(subtopicUid).map { list ->
            list.map { wt ->
                WordEntry(
                    wordUid = wt.word.wordUid,
                    position = wt.word.position,
                    translations = wt.translations.associate { it.language to it.text },
                )
            }
        }
}

@JvmName("resolveTopicTitle")
private fun List<TopicTitleEntity>?.resolveTopicTitle(locale: String = "en"): String =
    this?.firstOrNull { it.language == locale }?.text
        ?: this?.firstOrNull()?.text
        ?: ""

@JvmName("resolveSubtopicTitle")
private fun List<SubtopicTitleEntity>?.resolveSubtopicTitle(locale: String = "en"): String =
    this?.firstOrNull { it.language == locale }?.text
        ?: this?.firstOrNull()?.text
        ?: ""
