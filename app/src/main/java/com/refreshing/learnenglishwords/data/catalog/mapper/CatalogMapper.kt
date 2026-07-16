package com.refreshing.learnenglishwords.data.catalog.mapper

import com.refreshing.learnenglishwords.core.model.subtopicUid
import com.refreshing.learnenglishwords.core.model.wordUid
import com.refreshing.learnenglishwords.data.catalog.dto.ManifestDto
import com.refreshing.learnenglishwords.data.catalog.dto.ManifestTopicDto
import com.refreshing.learnenglishwords.data.catalog.dto.SubtopicDto
import com.refreshing.learnenglishwords.data.catalog.dto.WordDto
import com.refreshing.learnenglishwords.data.local.entity.CatalogLanguageEntity
import com.refreshing.learnenglishwords.data.local.entity.CatalogStateEntity
import com.refreshing.learnenglishwords.data.local.entity.SubtopicEntity
import com.refreshing.learnenglishwords.data.local.entity.SubtopicTitleEntity
import com.refreshing.learnenglishwords.data.local.entity.TopicEntity
import com.refreshing.learnenglishwords.data.local.entity.TopicTitleEntity
import com.refreshing.learnenglishwords.data.local.entity.WordEntity
import com.refreshing.learnenglishwords.data.local.entity.WordTranslationEntity
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogMapper @Inject constructor() {

    fun mapCatalogState(manifest: ManifestDto, savedAt: Long): CatalogStateEntity =
        CatalogStateEntity(
            id = 1,
            schemaVersion = manifest.schemaVersion,
            catalogRevision = 0L,
            catalogVersion = manifest.catalogVersion,
            generatedAt = Instant.parse(manifest.generatedAt).toEpochMilli(),
            savedAt = savedAt,
            source = SOURCE_ASSET,
        )

    fun mapLanguages(manifest: ManifestDto): List<CatalogLanguageEntity> {
        val languages = mutableListOf<CatalogLanguageEntity>()
        languages += CatalogLanguageEntity(
            languageCode = manifest.mainLanguage,
            isMain = true,
            position = 0,
        )
        manifest.additionalLanguages.forEachIndexed { index, code ->
            languages += CatalogLanguageEntity(
                languageCode = code,
                isMain = false,
                position = index + 1,
            )
        }
        return languages
    }

    fun mapTopic(manifestTopic: ManifestTopicDto, position: Int): TopicEntity =
        TopicEntity(
            topicKey = manifestTopic.key,
            remoteId = manifestTopic.id,
            position = position,
            sourceFile = "${manifestTopic.key}.json",
            topicRevision = 1,
            contentHash = null,
            expectedWordCount = manifestTopic.size,
            isActive = true,
            lastSeenCatalogRevision = 0L,
        )

    fun mapTopicTitles(topicKey: String, title: Map<String, String>): List<TopicTitleEntity> =
        title.map { (lang, text) -> TopicTitleEntity(topicKey = topicKey, language = lang, text = text) }

    fun mapSubtopic(subtopic: SubtopicDto, topicKey: String, position: Int): SubtopicEntity {
        val uid = subtopicUid(topicKey, subtopic.key)
        return SubtopicEntity(
            subtopicUid = uid,
            topicKey = topicKey,
            remoteId = subtopic.id,
            subtopicKey = subtopic.key,
            position = position,
            isActive = true,
            lastSeenCatalogRevision = 0L,
        )
    }

    fun mapSubtopicTitles(subtopicUid: String, title: Map<String, String>): List<SubtopicTitleEntity> =
        title.map { (lang, text) -> SubtopicTitleEntity(subtopicUid = subtopicUid, language = lang, text = text) }

    fun mapWord(word: WordDto, subtopicUid: String, topicKey: String, subtopicKey: String, position: Int): WordEntity {
        val uid = wordUid(topicKey, subtopicKey, word.id)
        return WordEntity(
            wordUid = uid,
            subtopicUid = subtopicUid,
            remoteId = word.id,
            position = position,
            learningRevision = 1,
            isActive = true,
            lastSeenCatalogRevision = 0L,
        )
    }

    fun mapWordTranslations(wordUid: String, translations: Map<String, String>): List<WordTranslationEntity> =
        translations.map { (lang, text) -> WordTranslationEntity(wordUid = wordUid, language = lang, text = text) }

    companion object {
        const val SOURCE_ASSET = "ASSET"
        const val SOURCE_REMOTE = "REMOTE"
    }
}
