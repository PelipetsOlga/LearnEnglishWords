package com.refreshing.learnenglishwords.domain.catalog

import com.refreshing.learnenglishwords.core.model.Subtopic
import com.refreshing.learnenglishwords.core.model.Topic
import kotlinx.coroutines.flow.Flow

interface CatalogRepository {
    fun observeTopics(): Flow<List<Topic>>
    fun observeSubtopics(topicKey: String): Flow<List<Subtopic>>
}
