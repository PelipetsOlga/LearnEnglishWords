package com.refreshing.learnenglishwords.domain.catalog

import com.refreshing.learnenglishwords.core.model.Subtopic
import com.refreshing.learnenglishwords.core.model.Topic
import com.refreshing.learnenglishwords.core.model.WordEntry
import kotlinx.coroutines.flow.Flow

interface CatalogRepository {
    fun observeTopics(): Flow<List<Topic>>
    fun observeSubtopics(topicKey: String): Flow<List<Subtopic>>
    fun observeSubtopicTitle(subtopicUid: String): Flow<String>
    fun observeMainLanguage(): Flow<String>
    fun observeWordEntries(subtopicUid: String): Flow<List<WordEntry>>
}
