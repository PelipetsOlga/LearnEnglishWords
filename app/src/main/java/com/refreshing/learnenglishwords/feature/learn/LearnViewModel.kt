package com.refreshing.learnenglishwords.feature.learn

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refreshing.learnenglishwords.domain.catalog.CatalogRepository
import com.refreshing.learnenglishwords.domain.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LearnViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val catalogRepository: CatalogRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val subtopicUid: String =
        Uri.decode(checkNotNull(savedStateHandle["subtopicUid"]))

    private val _uiState = MutableStateFlow(LearnUiState())
    val uiState: StateFlow<LearnUiState> = _uiState.asStateFlow()

    private val _effects = Channel<LearnEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                catalogRepository.observeSubtopicTitle(subtopicUid),
                catalogRepository.observeMainLanguage(),
                catalogRepository.observeWordEntries(subtopicUid),
                settingsRepository.observeSettings(),
            ) { title, mainLang, entries, settings ->
                val words = entries.map { entry ->
                    LearnWordItem(
                        wordUid = entry.wordUid,
                        mainTranslation = entry.translations[mainLang] ?: "",
                        additionalTranslations = settings.selectedLanguages.mapNotNull { lang ->
                            entry.translations[lang]?.let { lang to it }
                        },
                    )
                }
                LearnUiState(
                    subtopicTitle = title,
                    words = words,
                    isEmpty = words.isEmpty(),
                    isLoading = false,
                )
            }.collect { _uiState.value = it }
        }
    }

    fun onIntent(intent: LearnIntent) {
        when (intent) {
            LearnIntent.StartQuizClicked ->
                viewModelScope.launch { _effects.send(LearnEffect.NavigateToQuiz) }
        }
    }
}
