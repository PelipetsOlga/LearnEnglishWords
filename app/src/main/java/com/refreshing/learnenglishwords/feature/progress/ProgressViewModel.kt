package com.refreshing.learnenglishwords.feature.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refreshing.learnenglishwords.domain.analytics.AnalyticsTracker
import com.refreshing.learnenglishwords.domain.catalog.CatalogRepository
import com.refreshing.learnenglishwords.domain.progress.ProgressAggregator
import com.refreshing.learnenglishwords.domain.progress.ProgressRepository
import com.refreshing.learnenglishwords.domain.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository,
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                catalogRepository.observeAllWordEntries(),
                progressRepository.observeAllActiveProgress(),
                settingsRepository.observeSettings(),
                settingsRepository.observeAvailableLanguages(),
                catalogRepository.observeMainLanguage(),
            ) { words, progressList, settings, availableLangs, mainLang ->
                val orderedSelected = availableLangs.filter { it in settings.selectedLanguages }
                val progressMap = progressList.associateBy {
                    Triple(it.wordUid, it.sourceLanguage, it.targetLanguage)
                }
                val overall = ProgressAggregator.compute(
                    words, progressMap, mainLang, orderedSelected, settings.translationDirection,
                )
                ProgressUiState(
                    isLoading = false,
                    overall = overall,
                    resetAllConfirm = _uiState.value.resetAllConfirm,
                )
            }.collect { _uiState.value = it }
        }
    }

    fun onIntent(intent: ProgressIntent) {
        when (intent) {
            ProgressIntent.ResetAllRequested ->
                _uiState.value = _uiState.value.copy(resetAllConfirm = true)

            ProgressIntent.ResetAllDismissed ->
                _uiState.value = _uiState.value.copy(resetAllConfirm = false)

            ProgressIntent.ResetAllConfirmed -> {
                _uiState.value = _uiState.value.copy(resetAllConfirm = false)
                viewModelScope.launch {
                    progressRepository.resetAll()
                    analyticsTracker.trackProgressReset("all", null)
                }
            }
        }
    }
}
