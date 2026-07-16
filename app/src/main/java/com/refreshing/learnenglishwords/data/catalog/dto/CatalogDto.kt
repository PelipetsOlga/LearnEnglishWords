package com.refreshing.learnenglishwords.data.catalog.dto

import kotlinx.serialization.Serializable

@Serializable
data class ManifestDto(
    val schemaVersion: Int,
    val catalogVersion: String,
    val generatedAt: String,
    val mainLanguage: String,
    val additionalLanguages: List<String>,
    val topics: List<ManifestTopicDto>,
)

@Serializable
data class ManifestTopicDto(
    val id: Int,
    val key: String,
    val size: Int,
    val title: Map<String, String>,
)

@Serializable
data class TopicFileDto(
    val schemaVersion: Int,
    val topic: TopicDto,
)

@Serializable
data class TopicDto(
    val id: Int,
    val key: String,
    val title: Map<String, String>,
    val subtopics: List<SubtopicDto>,
)

@Serializable
data class SubtopicDto(
    val id: Int,
    val key: String,
    val title: Map<String, String>,
    val words: List<WordDto>,
)

@Serializable
data class WordDto(
    val id: Int,
    val translations: Map<String, String>,
)
