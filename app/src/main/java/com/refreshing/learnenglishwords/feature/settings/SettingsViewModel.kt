package com.refreshing.learnenglishwords.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refreshing.learnenglishwords.data.local.dao.CatalogDao
import com.refreshing.learnenglishwords.domain.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val catalogDao: CatalogDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                settingsRepository.observeSettings(),
                settingsRepository.observeAvailableLanguages(),
                catalogDao.observeCatalogState(),
            ) { settings, availableLangs, catalogState ->
                SettingsUiState(
                    quizOrder = settings.quizOrder,
                    availableLanguages = availableLangs,
                    selectedLanguages = settings.selectedLanguages,
                    translationDirection = settings.translationDirection,
                    catalogVersion = catalogState?.catalogVersion ?: "",
                    catalogGeneratedAt = catalogState?.generatedAt ?: 0L,
                    isLoading = false,
                )
            }.collect { _uiState.value = it }
        }
    }

    fun onIntent(intent: SettingsIntent) {
        viewModelScope.launch {
            when (intent) {
                is SettingsIntent.QuizOrderChanged ->
                    settingsRepository.setQuizOrder(intent.order)

                is SettingsIntent.LanguageToggled -> {
                    val current = _uiState.value.selectedLanguages
                    val updated = if (intent.languageCode in current) {
                        current - intent.languageCode
                    } else {
                        current + intent.languageCode
                    }
                    // At-least-one enforcement: ignore deselect if it would leave 0 selected
                    settingsRepository.setSelectedLanguages(updated)
                }

                is SettingsIntent.TranslationDirectionChanged ->
                    settingsRepository.setTranslationDirection(intent.direction)
            }
        }
    }
}
