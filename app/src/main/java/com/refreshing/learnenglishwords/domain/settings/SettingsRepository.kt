package com.refreshing.learnenglishwords.domain.settings

import com.refreshing.learnenglishwords.core.model.QuizOrder
import com.refreshing.learnenglishwords.core.model.QuizSettings
import com.refreshing.learnenglishwords.core.model.TranslationDirection
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    /** Emits current settings, re-emitting whenever settings or available languages change. */
    fun observeSettings(): Flow<QuizSettings>

    /** All additional language codes currently in the catalog. */
    fun observeAvailableLanguages(): Flow<List<String>>

    suspend fun setQuizOrder(order: QuizOrder)

    /**
     * Saves [languages] as the selected set.
     * Ignored if [languages] is empty (at-least-one enforcement).
     */
    suspend fun setSelectedLanguages(languages: Set<String>)

    suspend fun setTranslationDirection(direction: TranslationDirection)
}
