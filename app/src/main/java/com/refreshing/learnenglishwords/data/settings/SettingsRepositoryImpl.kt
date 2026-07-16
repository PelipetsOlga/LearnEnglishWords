package com.refreshing.learnenglishwords.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.refreshing.learnenglishwords.core.model.QuizOrder
import com.refreshing.learnenglishwords.core.model.QuizSettings
import com.refreshing.learnenglishwords.core.model.TranslationDirection
import com.refreshing.learnenglishwords.data.local.dao.CatalogDao
import com.refreshing.learnenglishwords.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val catalogDao: CatalogDao,
) : SettingsRepository {

    private object Keys {
        val QUIZ_ORDER = stringPreferencesKey("quiz_order")
        val SELECTED_LANGUAGES = stringSetPreferencesKey("selected_languages")
        val TRANSLATION_DIRECTION = stringPreferencesKey("translation_direction")
        val QUIZ_LANGUAGE = stringPreferencesKey("quiz_language")
    }

    companion object {
        private const val DEFAULT_LANGUAGE = "pl"
    }

    override fun observeSettings(): Flow<QuizSettings> {
        val additionalLanguagesFlow = catalogDao.observeAdditionalLanguages()
            .map { entities -> entities.map { it.languageCode }.toSet() }

        return combine(dataStore.data, additionalLanguagesFlow) { prefs, availableLangs ->
            val savedOrder = prefs[Keys.QUIZ_ORDER]?.let { runCatching { QuizOrder.valueOf(it) }.getOrNull() }
                ?: QuizOrder.RANDOM
            val savedDirection = prefs[Keys.TRANSLATION_DIRECTION]?.let {
                runCatching { TranslationDirection.valueOf(it) }.getOrNull()
            } ?: TranslationDirection.BOTH

            val savedLangs = prefs[Keys.SELECTED_LANGUAGES]
            val selectedLangs = if (savedLangs == null) {
                // First launch: default to "pl" only
                val default = if (DEFAULT_LANGUAGE in availableLangs) setOf(DEFAULT_LANGUAGE) else availableLangs
                default
            } else {
                // Remove unsupported codes; if empty, fall back to default
                val valid = savedLangs.intersect(availableLangs)
                valid.ifEmpty {
                    if (DEFAULT_LANGUAGE in availableLangs) setOf(DEFAULT_LANGUAGE) else availableLangs
                }
            }

            val savedQuizLang = prefs[Keys.QUIZ_LANGUAGE]
            val quizLanguage = when {
                savedQuizLang != null && savedQuizLang in availableLangs -> savedQuizLang
                DEFAULT_LANGUAGE in availableLangs -> DEFAULT_LANGUAGE
                else -> availableLangs.firstOrNull() ?: DEFAULT_LANGUAGE
            }

            QuizSettings(
                quizOrder = savedOrder,
                selectedLanguages = selectedLangs,
                translationDirection = savedDirection,
                quizLanguage = quizLanguage,
            )
        }
    }

    override fun observeAvailableLanguages(): Flow<List<String>> =
        catalogDao.observeAdditionalLanguages().map { entities -> entities.map { it.languageCode } }

    override suspend fun setQuizOrder(order: QuizOrder) {
        dataStore.edit { it[Keys.QUIZ_ORDER] = order.name }
    }

    override suspend fun setSelectedLanguages(languages: Set<String>) {
        if (languages.isEmpty()) return
        dataStore.edit { it[Keys.SELECTED_LANGUAGES] = languages }
    }

    override suspend fun setTranslationDirection(direction: TranslationDirection) {
        dataStore.edit { it[Keys.TRANSLATION_DIRECTION] = direction.name }
    }

    override suspend fun setQuizLanguage(language: String) {
        dataStore.edit { it[Keys.QUIZ_LANGUAGE] = language }
    }
}
