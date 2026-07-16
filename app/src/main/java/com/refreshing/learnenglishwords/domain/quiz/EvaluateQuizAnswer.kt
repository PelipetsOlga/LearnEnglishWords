package com.refreshing.learnenglishwords.domain.quiz

import com.refreshing.learnenglishwords.core.time.TimeProvider
import com.refreshing.learnenglishwords.data.local.entity.WordProgressEntity
import javax.inject.Inject

/**
 * Applies one quiz answer to a progress row and returns the updated row.
 *
 * Rules:
 * - Incorrect: failureCount+1, correctStreak=0, isLearned=false
 * - Correct with failureCount < 2: isLearned=true
 * - Correct with failureCount >= 2: correctStreak+1, isLearned=(correctStreak>=2)
 */
class EvaluateQuizAnswer @Inject constructor(
    private val timeProvider: TimeProvider,
) {

    fun evaluate(
        existing: WordProgressEntity?,
        wordUid: String,
        sourceLanguage: String,
        targetLanguage: String,
        learningRevision: Int,
        isCorrect: Boolean,
    ): WordProgressEntity {
        val now = timeProvider.currentTimeMillis()
        val base = existing ?: WordProgressEntity(
            wordUid = wordUid,
            sourceLanguage = sourceLanguage,
            targetLanguage = targetLanguage,
            learningRevision = learningRevision,
        )

        val firstAnsweredAt = base.firstAnsweredAt ?: now

        return if (isCorrect) {
            if (base.failureCount < 2) {
                base.copy(
                    isLearned = true,
                    correctStreak = 1,
                    firstAnsweredAt = firstAnsweredAt,
                    lastAnsweredAt = now,
                    learnedAt = now,
                )
            } else {
                val newStreak = base.correctStreak + 1
                val learned = newStreak >= 2
                base.copy(
                    correctStreak = newStreak,
                    isLearned = learned,
                    firstAnsweredAt = firstAnsweredAt,
                    lastAnsweredAt = now,
                    learnedAt = if (learned && base.learnedAt == null) now else base.learnedAt,
                )
            }
        } else {
            base.copy(
                failureCount = base.failureCount + 1,
                correctStreak = 0,
                isLearned = false,
                firstAnsweredAt = firstAnsweredAt,
                lastAnsweredAt = now,
                learnedAt = null,
            )
        }
    }
}
