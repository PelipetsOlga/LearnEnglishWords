package com.refreshing.learnenglishwords.domain.quiz

import com.refreshing.learnenglishwords.core.model.QuizOrder
import com.refreshing.learnenglishwords.core.model.TranslationDirection
import com.refreshing.learnenglishwords.core.model.WordSnapshot
import com.refreshing.learnenglishwords.core.util.Shuffler
import com.refreshing.learnenglishwords.data.local.entity.WordProgressEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BuildQuizSessionTest {

    // A shuffler that returns the list unchanged — for deterministic order tests
    private val identityShuffler = object : Shuffler {
        override fun <T> shuffle(list: List<T>): List<T> = list
    }

    // A seeded shuffler that reverses the list — for deterministic random tests
    private val reverseShuffler = object : Shuffler {
        override fun <T> shuffle(list: List<T>): List<T> = list.reversed()
    }

    private val mainLang = "en"
    private val selectedLangs = listOf("uk", "pl")

    /** Build 5 words: word0..word4, each with en/uk/pl translations */
    private fun makeWords(count: Int = 5): List<WordSnapshot> =
        (0 until count).map { i ->
            WordSnapshot(
                wordUid = "t/s/$i",
                position = i,
                translations = mapOf(
                    "en" to "en_word_$i",
                    "uk" to "uk_word_$i",
                    "pl" to "pl_word_$i",
                ),
            )
        }

    private fun learned(wordUid: String, src: String, tgt: String) =
        WordProgressEntity(
            wordUid = wordUid,
            sourceLanguage = src,
            targetLanguage = tgt,
            learningRevision = 1,
            isLearned = true,
        )

    private fun build(
        words: List<WordSnapshot>,
        direction: TranslationDirection = TranslationDirection.MAIN_TO_ADDITIONAL,
        order: QuizOrder = QuizOrder.EXISTING,
        shuffler: Shuffler = identityShuffler,
    ) = BuildQuizSession(shuffler).build(
        words = words,
        mainLanguage = mainLang,
        orderedSelectedLanguages = selectedLangs,
        direction = direction,
        order = order,
    )

    // 7. BOTH does not generate PL->UK
    @Test
    fun `BOTH does not generate additional to additional pairs`() {
        val words = makeWords(3)
        val tasks = build(words, direction = TranslationDirection.BOTH)
        // Valid: en->uk, en->pl, uk->en, pl->en  — NOT uk->pl or pl->uk
        tasks.forEach { task ->
            val isMainToAdd = task.sourceLanguage == mainLang && task.targetLanguage != mainLang
            val isAddToMain = task.sourceLanguage != mainLang && task.targetLanguage == mainLang
            assertTrue(
                "Unexpected pair: ${task.sourceLanguage}->${task.targetLanguage}",
                isMainToAdd || isAddToMain,
            )
        }
    }

    // 8. Learned tasks are excluded
    @Test
    fun `learned tasks are excluded from session`() {
        // Need 2+ words so distractors exist; word0 has en->uk learned
        val words = listOf(
            WordSnapshot(
                wordUid = "t/s/0",
                position = 0,
                translations = mapOf("en" to "hello", "uk" to "привіт", "pl" to "cześć"),
                progressByDirection = mapOf(
                    ("en" to "uk") to learned("t/s/0", "en", "uk"),
                ),
            ),
            WordSnapshot(
                wordUid = "t/s/1",
                position = 1,
                translations = mapOf("en" to "world", "uk" to "світ", "pl" to "świat"),
            ),
        )
        val tasks = build(words, direction = TranslationDirection.MAIN_TO_ADDITIONAL)
        // word0: en->uk learned (excluded), en->pl not learned (included)
        // word1: en->uk, en->pl both not learned (both included)
        val word0Tasks = tasks.filter { it.wordUid == "t/s/0" }
        assertEquals(1, word0Tasks.size)
        assertEquals("pl", word0Tasks[0].targetLanguage)
    }

    // 9. Options are distinct and contain exactly one correct answer
    @Test
    fun `options are distinct and contain exactly one correct answer`() {
        val words = makeWords(5)
        val tasks = build(words)
        tasks.forEach { task ->
            assertEquals(
                "Options should be distinct for task ${task.wordUid}",
                task.options.size,
                task.options.distinct().size,
            )
            assertEquals(
                "Exactly one correct answer among options",
                1,
                task.options.count { it == task.correctAnswer },
            )
        }
    }

    // 10. Existing order is stable (word position, then lang position, then direction)
    @Test
    fun `existing order is stable by word position then language position`() {
        val words = makeWords(3)
        val tasks = build(words, direction = TranslationDirection.MAIN_TO_ADDITIONAL, order = QuizOrder.EXISTING)
        // Expected: w0->uk, w0->pl, w1->uk, w1->pl, w2->uk, w2->pl
        assertEquals("t/s/0", tasks[0].wordUid)
        assertEquals("uk", tasks[0].targetLanguage)
        assertEquals("t/s/0", tasks[1].wordUid)
        assertEquals("pl", tasks[1].targetLanguage)
        assertEquals("t/s/1", tasks[2].wordUid)
    }

    // 11. Random order is deterministic with a seeded test shuffler
    @Test
    fun `random order is deterministic with seeded shuffler`() {
        val words = makeWords(3)
        val tasks1 = BuildQuizSession(reverseShuffler).build(
            words, mainLang, selectedLangs, TranslationDirection.MAIN_TO_ADDITIONAL, QuizOrder.RANDOM,
        )
        val tasks2 = BuildQuizSession(reverseShuffler).build(
            words, mainLang, selectedLangs, TranslationDirection.MAIN_TO_ADDITIONAL, QuizOrder.RANDOM,
        )
        assertEquals(
            tasks1.map { "${it.wordUid}:${it.targetLanguage}" },
            tasks2.map { "${it.wordUid}:${it.targetLanguage}" },
        )
    }

    @Test
    fun `tasks with no distractor are dropped`() {
        // Only one word -> no distractors -> no tasks
        val words = listOf(
            WordSnapshot(
                wordUid = "t/s/0",
                position = 0,
                translations = mapOf("en" to "hello", "uk" to "привіт"),
            ),
        )
        val tasks = build(words, direction = TranslationDirection.MAIN_TO_ADDITIONAL)
        assertTrue(tasks.isEmpty())
    }

    @Test
    fun `MAIN_TO_ADDITIONAL generates only en to selected language tasks`() {
        val words = makeWords(2)
        val tasks = build(words, direction = TranslationDirection.MAIN_TO_ADDITIONAL)
        tasks.forEach { task ->
            assertEquals("en", task.sourceLanguage)
            assertFalse(task.targetLanguage == mainLang)
        }
    }

    @Test
    fun `ADDITIONAL_TO_MAIN generates only selected language to en tasks`() {
        val words = makeWords(2)
        val tasks = build(words, direction = TranslationDirection.ADDITIONAL_TO_MAIN)
        tasks.forEach { task ->
            assertFalse(task.sourceLanguage == mainLang)
            assertEquals("en", task.targetLanguage)
        }
    }
}
