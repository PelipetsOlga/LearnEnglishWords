package com.refreshing.learnenglishwords.domain.progress

import com.refreshing.learnenglishwords.core.model.TranslationDirection
import com.refreshing.learnenglishwords.core.model.WordEntry
import com.refreshing.learnenglishwords.data.local.entity.WordProgressEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressAggregatorTest {

    private val mainLang = "en"
    private val selectedLangs = listOf("uk", "pl")
    private val direction = TranslationDirection.MAIN_TO_ADDITIONAL

    private fun word(uid: String, langs: List<String> = listOf("en", "uk", "pl")) = WordEntry(
        wordUid = uid,
        position = 0,
        translations = langs.associateWith { "${it}_${uid.substringAfterLast("/")}"},
    )

    private fun learnedProgress(wordUid: String, src: String, tgt: String) =
        WordProgressEntity(wordUid, src, tgt, 1, isLearned = true)

    private fun compute(
        words: List<WordEntry>,
        progressMap: Map<Triple<String, String, String>, WordProgressEntity> = emptyMap(),
        dir: TranslationDirection = direction,
    ) = ProgressAggregator.compute(words, progressMap, mainLang, selectedLangs, dir)

    // 1. Untouched words count in denominator but not in numerator
    @Test
    fun `untouched words count in denominator`() {
        val words = listOf(word("t/s/0"), word("t/s/1"))
        // Only one direction for one word is learned
        val progressMap = mapOf(
            Triple("t/s/0", "en", "uk") to learnedProgress("t/s/0", "en", "uk"),
        )
        val stats = compute(words, progressMap)
        assertEquals(1, stats.learnedCount)
        assertEquals(4, stats.totalCount) // 2 words × 2 langs
        assertEquals(25, stats.percent)
    }

    // 2. Inactive words do not count (caller filters them; aggregator only sees active ones)
    @Test
    fun `only passed words are counted`() {
        // One active word; one "inactive" word excluded by caller
        val activeWords = listOf(word("t/s/0"))
        val stats = compute(activeWords)
        assertEquals(2, stats.totalCount) // 1 word × 2 selected langs
    }

    // 3. Reset subtopic does not affect another subtopic
    @Test
    fun `stats of one subtopic unaffected when other has no progress`() {
        val wordS0 = word("t/s0/0")
        val wordS1 = word("t/s1/0")
        // s1 has learned progress; s0 has none (simulates s0 being reset)
        val progressMap = mapOf(
            Triple("t/s1/0", "en", "uk") to learnedProgress("t/s1/0", "en", "uk"),
        )
        val stats = compute(listOf(wordS0, wordS1), progressMap)
        // Total: 2 words × 2 langs = 4; learned: 1 (s1 en→uk only)
        assertEquals(4, stats.totalCount)
        assertEquals(1, stats.learnedCount)
    }

    // 4. Reset topic does not affect another topic
    @Test
    fun `progress from different topics are independent`() {
        val wordA = word("topicA/s/0")
        val wordB = word("topicB/s/0")
        val progressMap = mapOf(
            Triple("topicA/s/0", "en", "uk") to learnedProgress("topicA/s/0", "en", "uk"),
        )
        val perTopic = ProgressAggregator.computePerTopic(
            listOf(wordA, wordB), progressMap, mainLang, selectedLangs, direction,
        )
        assertEquals(1, perTopic["topicA"]?.learnedCount)
        assertEquals(0, perTopic["topicB"]?.learnedCount)
        assertEquals(2, perTopic["topicA"]?.totalCount)
        assertEquals(2, perTopic["topicB"]?.totalCount)
    }

    // 5. Reset all clears progress but not content
    @Test
    fun `empty progress map keeps same total but zero learned`() {
        val words = listOf(word("t/s/0"), word("t/s/1"))
        val withProgress = compute(
            words,
            mapOf(Triple("t/s/0", "en", "uk") to learnedProgress("t/s/0", "en", "uk")),
        )
        val afterReset = compute(words, emptyMap())
        assertEquals(0, afterReset.learnedCount)
        assertEquals(withProgress.totalCount, afterReset.totalCount)
    }

    @Test
    fun `BOTH direction generates two tasks per word per language`() {
        val words = listOf(word("t/s/0"))
        val stats = compute(words, dir = TranslationDirection.BOTH)
        // en→uk, uk→en, en→pl, pl→en = 4 tasks
        assertEquals(4, stats.totalCount)
    }

    @Test
    fun `direction stats show correct per-direction breakdown`() {
        val words = listOf(word("t/s/0"), word("t/s/1"))
        val progressMap = mapOf(
            Triple("t/s/0", "en", "uk") to learnedProgress("t/s/0", "en", "uk"),
            Triple("t/s/1", "en", "uk") to learnedProgress("t/s/1", "en", "uk"),
        )
        val stats = compute(words, progressMap)
        val enUk = stats.directions.find { it.sourceLanguage == "en" && it.targetLanguage == "uk" }
        val enPl = stats.directions.find { it.sourceLanguage == "en" && it.targetLanguage == "pl" }
        assertEquals(2, enUk?.learnedCount)
        assertEquals(0, enPl?.learnedCount)
    }
}
