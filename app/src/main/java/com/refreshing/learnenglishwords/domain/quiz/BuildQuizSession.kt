package com.refreshing.learnenglishwords.domain.quiz

import com.refreshing.learnenglishwords.core.model.QuizOrder
import com.refreshing.learnenglishwords.core.model.QuizTask
import com.refreshing.learnenglishwords.core.model.TranslationDirection
import com.refreshing.learnenglishwords.core.model.WordSnapshot
import com.refreshing.learnenglishwords.core.util.RandomShuffler
import com.refreshing.learnenglishwords.core.util.Shuffler
import javax.inject.Inject

private const val TARGET_OPTIONS = 4

/**
 * Builds a quiz task list from a snapshot of words, progress, and settings.
 *
 * Eligible task: (wordUid, sourceLanguage, targetLanguage) where
 *  - both translations exist
 *  - the task is not yet learned
 *  - direction is valid per [TranslationDirection] rules (no add->add)
 *
 * Ordering: EXISTING sorts by word position, then language position, then direction.
 * RANDOM applies the injected [shuffler].
 *
 * Each task gets up to [TARGET_OPTIONS] distinct options (correct + distractors),
 * drawn first from the same word list. Tasks with no viable distractor are dropped.
 */
class BuildQuizSession @Inject constructor(
    private val shuffler: Shuffler,
) {

    fun build(
        words: List<WordSnapshot>,
        mainLanguage: String,
        orderedSelectedLanguages: List<String>,
        direction: TranslationDirection,
        order: QuizOrder,
    ): List<QuizTask> {
        // 1. Generate raw (wordUid, src, tgt) pairs
        val rawPairs = mutableListOf<RawTask>()
        words.forEachIndexed { wordIndex, word ->
            orderedSelectedLanguages.forEachIndexed { langIndex, lang ->
                when (direction) {
                    TranslationDirection.MAIN_TO_ADDITIONAL ->
                        rawPairs += RawTask(word, wordIndex, mainLanguage, lang, langIndex, 0)

                    TranslationDirection.ADDITIONAL_TO_MAIN ->
                        rawPairs += RawTask(word, wordIndex, lang, mainLanguage, langIndex, 1)

                    TranslationDirection.BOTH -> {
                        rawPairs += RawTask(word, wordIndex, mainLanguage, lang, langIndex, 0)
                        rawPairs += RawTask(word, wordIndex, lang, mainLanguage, langIndex, 1)
                        // Do NOT add lang->lang pairs
                    }
                }
            }
        }

        // 2. Filter out tasks with missing translations or already learned
        val eligible = rawPairs.filter { raw ->
            val srcText = raw.word.translations[raw.src]
            val tgtText = raw.word.translations[raw.tgt]
            if (srcText.isNullOrBlank() || tgtText.isNullOrBlank()) return@filter false
            val progress = raw.word.progressByDirection[raw.src to raw.tgt]
            progress?.isLearned != true
        }

        // 3. Sort (EXISTING) or shuffle (RANDOM)
        val ordered = when (order) {
            QuizOrder.EXISTING -> eligible.sortedWith(
                compareBy({ it.wordPosition }, { it.langPosition }, { it.directionOrdinal }),
            )
            QuizOrder.RANDOM -> shuffler.shuffle(eligible)
        }

        // 4. Build QuizTask with distractors for each eligible pair
        return ordered.mapNotNull { raw ->
            buildTask(raw, words)
        }
    }

    private fun buildTask(raw: RawTask, allWords: List<WordSnapshot>): QuizTask? {
        val prompt = raw.word.translations[raw.src] ?: return null
        val correctAnswer = raw.word.translations[raw.tgt] ?: return null

        // Collect distinct distractors (same target language, different word, not identical text)
        val distractors = allWords
            .filter { it.wordUid != raw.word.wordUid }
            .mapNotNull { it.translations[raw.tgt] }
            .filter { it.trim().lowercase() != correctAnswer.trim().lowercase() }
            .distinct()

        if (distractors.isEmpty()) return null

        val chosenDistractors = shuffler.shuffle(distractors).take(TARGET_OPTIONS - 1)
        val options = shuffler.shuffle(listOf(correctAnswer) + chosenDistractors)

        return QuizTask(
            wordUid = raw.word.wordUid,
            sourceLanguage = raw.src,
            targetLanguage = raw.tgt,
            prompt = prompt,
            correctAnswer = correctAnswer,
            options = options,
        )
    }

    private data class RawTask(
        val word: WordSnapshot,
        val wordPosition: Int,
        val src: String,
        val tgt: String,
        val langPosition: Int,
        val directionOrdinal: Int,
    )
}
