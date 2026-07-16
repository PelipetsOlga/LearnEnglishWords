package com.refreshing.learnenglishwords.data.catalog

import com.refreshing.learnenglishwords.data.catalog.dto.ManifestDto
import com.refreshing.learnenglishwords.data.catalog.dto.TopicFileDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogValidator @Inject constructor() {

    /**
     * Validates the manifest and all topic files against the rules defined in the spec.
     * Throws [CatalogValidationException] on the first rule violation found.
     */
    @Throws(CatalogValidationException::class)
    fun validate(manifest: ManifestDto, topicFiles: List<TopicFileDto>) {
        check(manifest.schemaVersion == SUPPORTED_SCHEMA_VERSION) {
            "Unsupported manifest schema version: ${manifest.schemaVersion}"
        }
        for (topicFile in topicFiles) {
            check(topicFile.schemaVersion == SUPPORTED_SCHEMA_VERSION) {
                "Unsupported schema version in topic '${topicFile.topic.key}': ${topicFile.schemaVersion}"
            }
            val manifestTopic = manifest.topics.find { it.key == topicFile.topic.key }
                ?: validationError("Topic '${topicFile.topic.key}' not found in manifest")

            check(topicFile.topic.id == manifestTopic.id) {
                "Topic ID mismatch for '${topicFile.topic.key}': " +
                    "manifest=${manifestTopic.id}, file=${topicFile.topic.id}"
            }
            check(topicFile.topic.key.isNotBlank()) {
                "Topic key is blank for topic id=${topicFile.topic.id}"
            }

            val wordCount = topicFile.topic.subtopics.sumOf { it.words.size }
            check(wordCount == manifestTopic.size) {
                "Word count mismatch for '${topicFile.topic.key}': manifest=${manifestTopic.size}, actual=$wordCount"
            }

            for (subtopic in topicFile.topic.subtopics) {
                check(subtopic.key.isNotBlank()) {
                    "Blank subtopic key in topic '${topicFile.topic.key}' subtopic id=${subtopic.id}"
                }

                val wordIds = subtopic.words.map { it.id }
                check(wordIds.size == wordIds.toSet().size) {
                    "Duplicate word IDs in '${topicFile.topic.key}/${subtopic.key}'"
                }

                for (word in subtopic.words) {
                    check(manifest.mainLanguage in word.translations) {
                        "Missing '${manifest.mainLanguage}' translation for word ${word.id} " +
                            "in '${topicFile.topic.key}/${subtopic.key}'"
                    }
                    for ((lang, text) in word.translations) {
                        check(text.isNotBlank()) {
                            "Blank '$lang' translation for word ${word.id} " +
                                "in '${topicFile.topic.key}/${subtopic.key}'"
                        }
                    }
                }
            }
        }
    }

    private fun check(condition: Boolean, message: () -> String) {
        if (!condition) validationError(message())
    }

    private fun validationError(message: String): Nothing =
        throw CatalogValidationException(message)

    companion object {
        const val SUPPORTED_SCHEMA_VERSION = 1
    }
}
