package com.refreshing.learnenglishwords.domain.catalog

import androidx.room.withTransaction
import com.refreshing.learnenglishwords.core.time.TimeProvider
import com.refreshing.learnenglishwords.data.catalog.CatalogValidator
import com.refreshing.learnenglishwords.data.catalog.mapper.CatalogMapper
import com.refreshing.learnenglishwords.data.catalog.source.BundledCatalogSource
import com.refreshing.learnenglishwords.data.local.VocabularyDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BootstrapCatalog @Inject constructor(
    private val bundledCatalogSource: BundledCatalogSource,
    private val catalogValidator: CatalogValidator,
    private val database: VocabularyDatabase,
    private val mapper: CatalogMapper,
    private val timeProvider: TimeProvider,
) {
    /**
     * Imports the bundled catalog into Room on first launch.
     * Safe to call on every launch; does nothing if a catalog already exists.
     * Throws on validation or import failure — the caller must handle this as a retryable error.
     */
    suspend fun run(): BootstrapResult {
        if (database.catalogDao().getCatalogState() != null) {
            return BootstrapResult.AlreadyImported
        }

        val manifest = bundledCatalogSource.readManifest()
        val topicFiles = manifest.topics.map { bundledCatalogSource.readTopic(it.key) }
        catalogValidator.validate(manifest, topicFiles)

        val savedAt = timeProvider.currentTimeMillis()

        database.withTransaction {
            // Languages
            database.catalogDao().deleteCatalogLanguages()
            database.catalogDao().upsertCatalogLanguages(mapper.mapLanguages(manifest))

            // Topics, subtopics, words
            manifest.topics.forEachIndexed { topicIndex, manifestTopic ->
                val topicEntity = mapper.mapTopic(manifestTopic, topicIndex)
                database.topicDao().upsertTopic(topicEntity)
                database.topicDao().upsertTopicTitles(
                    mapper.mapTopicTitles(manifestTopic.key, manifestTopic.title),
                )

                val topicFile = topicFiles.first { it.topic.key == manifestTopic.key }
                topicFile.topic.subtopics.forEachIndexed { subtopicIndex, subtopicDto ->
                    val subtopicEntity = mapper.mapSubtopic(subtopicDto, manifestTopic.key, subtopicIndex)
                    database.subtopicDao().upsertSubtopic(subtopicEntity)
                    database.subtopicDao().upsertSubtopicTitles(
                        mapper.mapSubtopicTitles(subtopicEntity.subtopicUid, subtopicDto.title),
                    )

                    subtopicDto.words.forEachIndexed { wordIndex, wordDto ->
                        val wordEntity = mapper.mapWord(
                            word = wordDto,
                            subtopicUid = subtopicEntity.subtopicUid,
                            topicKey = manifestTopic.key,
                            subtopicKey = subtopicDto.key,
                            position = wordIndex,
                        )
                        database.wordDao().upsertWord(wordEntity)
                        database.wordDao().upsertWordTranslations(
                            mapper.mapWordTranslations(wordEntity.wordUid, wordDto.translations),
                        )
                    }
                }
            }

            // Catalog state written last — marks the import as complete
            database.catalogDao().upsertCatalogState(
                mapper.mapCatalogState(manifest, savedAt),
            )
        }

        return BootstrapResult.Imported
    }
}

enum class BootstrapResult { Imported, AlreadyImported }
