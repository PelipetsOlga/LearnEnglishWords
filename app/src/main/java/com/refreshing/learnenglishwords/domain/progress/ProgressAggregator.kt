package com.refreshing.learnenglishwords.domain.progress

import com.refreshing.learnenglishwords.core.model.TranslationDirection
import com.refreshing.learnenglishwords.core.model.WordEntry
import com.refreshing.learnenglishwords.data.local.entity.WordProgressEntity

/**
 * Pure domain logic for computing progress statistics.
 *
 * The denominator is always the number of *eligible tasks*, not the number of
 * progress rows. Words with no progress row count as unlearned but still contribute
 * to the denominator. Only active words are accepted (caller filters inactive ones).
 */
object ProgressAggregator {

    data class Stats(
        val learnedCount: Int,
        val totalCount: Int,
        val directions: List<DirectionStats>,
        val failureCount: Int,
    ) {
        val percent: Int = if (totalCount == 0) 0 else (learnedCount * 100) / totalCount

        companion object {
            val empty = Stats(0, 0, emptyList(), 0)
        }
    }

    data class DirectionStats(
        val sourceLanguage: String,
        val targetLanguage: String,
        val learnedCount: Int,
        val totalCount: Int,
    ) {
        val percent: Int = if (totalCount == 0) 0 else (learnedCount * 100) / totalCount
    }

    fun compute(
        words: List<WordEntry>,
        progressMap: Map<Triple<String, String, String>, WordProgressEntity>,
        mainLanguage: String,
        orderedSelectedLanguages: List<String>,
        direction: TranslationDirection,
    ): Stats {
        val eligible = buildEligiblePairs(words, mainLanguage, orderedSelectedLanguages, direction)
        return aggregate(eligible, progressMap)
    }

    /** Groups results by the first segment of wordUid (the topicKey). */
    fun computePerTopic(
        words: List<WordEntry>,
        progressMap: Map<Triple<String, String, String>, WordProgressEntity>,
        mainLanguage: String,
        orderedSelectedLanguages: List<String>,
        direction: TranslationDirection,
    ): Map<String, Stats> =
        words.groupBy { it.wordUid.substringBefore("/") }
            .mapValues { (_, topicWords) ->
                compute(topicWords, progressMap, mainLanguage, orderedSelectedLanguages, direction)
            }

    private fun aggregate(
        eligible: List<Triple<String, String, String>>,
        progressMap: Map<Triple<String, String, String>, WordProgressEntity>,
    ): Stats {
        var learnedCount = 0
        var failureCount = 0
        val dirLearned = mutableMapOf<Pair<String, String>, Int>()
        val dirTotal = mutableMapOf<Pair<String, String>, Int>()

        eligible.forEach { key ->
            val (_, src, tgt) = key
            val dirKey = src to tgt
            dirTotal[dirKey] = (dirTotal[dirKey] ?: 0) + 1

            val progress = progressMap[key]
            if (progress?.isLearned == true) {
                learnedCount++
                dirLearned[dirKey] = (dirLearned[dirKey] ?: 0) + 1
            }
            failureCount += progress?.failureCount ?: 0
        }

        val directions = dirTotal.entries
            .sortedWith(compareBy({ it.key.first }, { it.key.second }))
            .map { (k, total) ->
                DirectionStats(k.first, k.second, dirLearned[k] ?: 0, total)
            }

        return Stats(learnedCount, eligible.size, directions, failureCount)
    }

    private fun buildEligiblePairs(
        words: List<WordEntry>,
        mainLanguage: String,
        orderedSelectedLanguages: List<String>,
        direction: TranslationDirection,
    ): List<Triple<String, String, String>> {
        val pairs = mutableListOf<Triple<String, String, String>>()
        words.forEach { entry ->
            orderedSelectedLanguages.forEach { lang ->
                val hasMain = entry.translations.containsKey(mainLanguage)
                val hasLang = entry.translations.containsKey(lang)
                when (direction) {
                    TranslationDirection.MAIN_TO_ADDITIONAL ->
                        if (hasMain && hasLang) pairs += Triple(entry.wordUid, mainLanguage, lang)

                    TranslationDirection.ADDITIONAL_TO_MAIN ->
                        if (hasLang && hasMain) pairs += Triple(entry.wordUid, lang, mainLanguage)

                    TranslationDirection.BOTH -> if (hasMain && hasLang) {
                        pairs += Triple(entry.wordUid, mainLanguage, lang)
                        pairs += Triple(entry.wordUid, lang, mainLanguage)
                    }
                }
            }
        }
        return pairs
    }
}
