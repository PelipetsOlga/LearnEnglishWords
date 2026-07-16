package com.refreshing.learnenglishwords.data.catalog

import com.refreshing.learnenglishwords.data.catalog.dto.ManifestDto
import com.refreshing.learnenglishwords.data.catalog.dto.TopicFileDto
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CatalogParserTest {

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = false
        coerceInputValues = false
        explicitNulls = false
    }

    private val validator = CatalogValidator()

    private lateinit var manifest: ManifestDto
    private lateinit var topicFiles: List<TopicFileDto>

    @Before
    fun setUp() {
        manifest = json.decodeFromString(loadResource("catalog/manifest.json"))
        topicFiles = manifest.topics.map { manifestTopic ->
            json.decodeFromString(loadResource("catalog/${manifestTopic.key}.json"))
        }
    }

    // --- Manifest metadata ---

    @Test
    fun schemaVersion_is_1() {
        assertEquals(1, manifest.schemaVersion)
    }

    @Test
    fun catalogVersion_is_correct() {
        assertEquals("2026.07.16.1", manifest.catalogVersion)
    }

    @Test
    fun mainLanguage_is_en() {
        assertEquals("en", manifest.mainLanguage)
    }

    @Test
    fun additionalLanguages_are_uk_ru_pl() {
        assertEquals(listOf("uk", "ru", "pl"), manifest.additionalLanguages)
    }

    // --- Topic / subtopic / word counts ---

    @Test
    fun topicCount_is_3() {
        assertEquals(3, manifest.topics.size)
    }

    @Test
    fun totalSubtopicCount_is_28() {
        val total = topicFiles.sumOf { it.topic.subtopics.size }
        assertEquals(28, total)
    }

    @Test
    fun totalWordCount_is_550() {
        val total = topicFiles.sumOf { tf -> tf.topic.subtopics.sumOf { it.words.size } }
        assertEquals(550, total)
    }

    @Test
    fun human_has_11_subtopics_and_208_words() {
        val topic = topicFiles.first { it.topic.key == "human" }
        assertEquals(11, topic.topic.subtopics.size)
        assertEquals(208, topic.topic.subtopics.sumOf { it.words.size })
    }

    @Test
    fun place_of_residence_has_10_subtopics_and_196_words() {
        val topic = topicFiles.first { it.topic.key == "place_of_residence" }
        assertEquals(10, topic.topic.subtopics.size)
        assertEquals(196, topic.topic.subtopics.sumOf { it.words.size })
    }

    @Test
    fun education_has_7_subtopics_and_146_words() {
        val topic = topicFiles.first { it.topic.key == "education" }
        assertEquals(7, topic.topic.subtopics.size)
        assertEquals(146, topic.topic.subtopics.sumOf { it.words.size })
    }

    // --- Per-word invariants ---

    @Test
    fun everyWord_has_en_uk_ru_pl_translations() {
        val required = setOf("en", "uk", "ru", "pl")
        for (topicFile in topicFiles) {
            for (subtopic in topicFile.topic.subtopics) {
                for (word in subtopic.words) {
                    assertTrue(
                        "Word ${word.id} in ${topicFile.topic.key}/${subtopic.key} " +
                            "missing translations (has ${word.translations.keys})",
                        word.translations.keys.containsAll(required),
                    )
                }
            }
        }
    }

    @Test
    fun manifestSize_matchesWordCountPerTopic() {
        for (manifestTopic in manifest.topics) {
            val topicFile = topicFiles.first { it.topic.key == manifestTopic.key }
            val actual = topicFile.topic.subtopics.sumOf { it.words.size }
            assertEquals(
                "Word count mismatch for topic '${manifestTopic.key}'",
                manifestTopic.size,
                actual,
            )
        }
    }

    @Test
    fun wordIds_areUniqueWithinEachSubtopic() {
        for (topicFile in topicFiles) {
            for (subtopic in topicFile.topic.subtopics) {
                val ids = subtopic.words.map { it.id }
                assertEquals(
                    "Duplicate word IDs in ${topicFile.topic.key}/${subtopic.key}",
                    ids.size,
                    ids.toSet().size,
                )
            }
        }
    }

    // --- Validator ---

    @Test
    fun validator_passesForRealCatalogFiles() {
        validator.validate(manifest, topicFiles)
    }

    // --- Helpers ---

    private fun loadResource(path: String): String =
        checkNotNull(ClassLoader.getSystemResourceAsStream(path)) {
            "Test resource not found: $path"
        }.bufferedReader().use { it.readText() }
}
