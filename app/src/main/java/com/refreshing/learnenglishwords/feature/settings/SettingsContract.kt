package com.refreshing.learnenglishwords.feature.settings

import com.refreshing.learnenglishwords.core.model.QuizOrder
import com.refreshing.learnenglishwords.core.model.TranslationDirection

data class SettingsUiState(
    val quizOrder: QuizOrder = QuizOrder.RANDOM,
    val availableLanguages: List<String> = emptyList(),
    val selectedLanguages: Set<String> = emptySet(),
    val quizLanguage: String = "pl",
    val translationDirection: TranslationDirection = TranslationDirection.MAIN_TO_ADDITIONAL,
    val catalogVersion: String = "",
    val catalogGeneratedAt: Long = 0L,
    val isLoading: Boolean = true,
)

sealed interface SettingsIntent {
    data class VocabLanguageToggled(val languageCode: String) : SettingsIntent
    data class QuizLanguageChanged(val languageCode: String) : SettingsIntent
    data class QuizOrderChanged(val order: QuizOrder) : SettingsIntent
    data class TranslationDirectionChanged(val direction: TranslationDirection) : SettingsIntent
}
