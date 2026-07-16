package com.refreshing.learnenglishwords.domain.quiz

import com.refreshing.learnenglishwords.core.time.TimeProvider
import com.refreshing.learnenglishwords.data.local.entity.WordProgressEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EvaluateQuizAnswerTest {

    private val fakeTime = 1_000_000L
    private val timeProvider = object : TimeProvider {
        override fun currentTimeMillis(): Long = fakeTime
    }
    private lateinit var evaluate: EvaluateQuizAnswer

    @Before
    fun setUp() {
        evaluate = EvaluateQuizAnswer(timeProvider)
    }

    private fun eval(existing: WordProgressEntity?, isCorrect: Boolean) =
        evaluate.evaluate(
            existing = existing,
            wordUid = "t/s/1",
            sourceLanguage = "en",
            targetLanguage = "pl",
            learningRevision = 1,
            isCorrect = isCorrect,
        )

    private fun progress(
        failureCount: Int = 0,
        correctStreak: Int = 0,
        isLearned: Boolean = false,
    ) = WordProgressEntity(
        wordUid = "t/s/1",
        sourceLanguage = "en",
        targetLanguage = "pl",
        learningRevision = 1,
        failureCount = failureCount,
        correctStreak = correctStreak,
        isLearned = isLearned,
    )

    // 1. Correct first answer -> learned
    @Test
    fun `correct first answer is learned`() {
        val result = eval(null, isCorrect = true)
        assertTrue(result.isLearned)
    }

    // 2. One wrong then correct -> learned
    @Test
    fun `one wrong then correct is learned`() {
        val afterWrong = eval(null, isCorrect = false)
        val result = eval(afterWrong, isCorrect = true)
        assertTrue(result.isLearned)
    }

    // 3. Two wrong then one correct -> not learned, streak 1
    @Test
    fun `two wrong then one correct - not learned, streak 1`() {
        val step1 = eval(null, isCorrect = false)
        val step2 = eval(step1, isCorrect = false)
        val result = eval(step2, isCorrect = true)
        assertFalse(result.isLearned)
        assertEquals(1, result.correctStreak)
    }

    // 4. Two wrong then two consecutive correct -> learned
    @Test
    fun `two wrong then two consecutive correct is learned`() {
        val step1 = eval(null, isCorrect = false)
        val step2 = eval(step1, isCorrect = false)
        val step3 = eval(step2, isCorrect = true)
        val result = eval(step3, isCorrect = true)
        assertTrue(result.isLearned)
    }

    // 5. Correct, wrong while still eligible -> streak resets
    @Test
    fun `correct streak resets on wrong answer`() {
        // Two failures, then correct (streak=1), then wrong -> streak=0
        val step1 = eval(null, isCorrect = false)
        val step2 = eval(step1, isCorrect = false)
        val step3 = eval(step2, isCorrect = true) // streak=1, not yet learned
        val result = eval(step3, isCorrect = false)
        assertEquals(0, result.correctStreak)
        assertFalse(result.isLearned)
        assertEquals(3, result.failureCount)
    }

    // 6. EN->PL and PL->EN progress are independent
    @Test
    fun `en-pl and pl-en progress are independent`() {
        val enToPlResult = evaluate.evaluate(
            existing = null,
            wordUid = "t/s/1",
            sourceLanguage = "en",
            targetLanguage = "pl",
            learningRevision = 1,
            isCorrect = true,
        )
        // PL->EN has its own row (null existing)
        val plToEnResult = evaluate.evaluate(
            existing = null,
            wordUid = "t/s/1",
            sourceLanguage = "pl",
            targetLanguage = "en",
            learningRevision = 1,
            isCorrect = false,
        )
        assertTrue(enToPlResult.isLearned)
        assertFalse(plToEnResult.isLearned)
        assertEquals(1, plToEnResult.failureCount)
    }

    @Test
    fun `firstAnsweredAt set on first answer and preserved thereafter`() {
        val first = eval(null, isCorrect = false)
        assertEquals(fakeTime, first.firstAnsweredAt)
        val second = eval(first, isCorrect = true)
        assertEquals(fakeTime, second.firstAnsweredAt)
    }

    @Test
    fun `learnedAt is null after wrong answer`() {
        val result = eval(null, isCorrect = false)
        assertNull(result.learnedAt)
    }
}
