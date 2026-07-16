package com.refreshing.learnenglishwords.data.catalog.source

import com.refreshing.learnenglishwords.data.catalog.dto.ManifestDto
import com.refreshing.learnenglishwords.data.catalog.dto.TopicFileDto

interface BundledCatalogSource {
    suspend fun readManifest(): ManifestDto
    suspend fun readTopic(topicKey: String): TopicFileDto
}
