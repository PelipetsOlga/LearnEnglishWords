package com.refreshing.learnenglishwords.feature.learn

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refreshing.learnenglishwords.core.model.TranslationDirection
import com.refreshing.learnenglishwords.core.model.WordEntry
import com.refreshing.learnenglishwords.data.local.entity.WordProgressEntity
import com.refreshing.learnenglishwords.domain.catalog.CatalogRepository
import com.refreshing.learnenglishwords.domain.progress.ProgressRepository
import com.refreshing.learnenglishwords.domain.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LearnViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val catalogRepository: CatalogRepository,
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val subtopicUid: String? =
        savedStateHandle.get<String>("subtopicUid")?.let { Uri.decode(it) }
    private val topicKey: String? = savedStateHandle.get<String>("topicKey")

    private val _uiState = MutableStateFlow(LearnUiState())
    val uiState: StateFlow<LearnUiState> = _uiState.asStateFlow()

    private val _effects = Channel<LearnEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        val titleFlow = if (subtopicUid != null) {
            catalogRepository.observeSubtopicTitle(subtopicUid)
        } else {
            flowOf("All words")
        }
        val entriesFlow = if (subtopicUid != null) {
            catalogRepository.observeWordEntries(subtopicUid)
        } else {
            catalogRepository.observeWordEntriesForTopic(topicKey!!)
        }
        val progressFlow = if (subtopicUid != null) {
            progressRepository.observeProgressForSubtopic(subtopicUid)
        } else {
            progressRepository.observeProgressForTopic(topicKey!!)
        }

        viewModelScope.launch {
            combine(
                titleFlow,
                catalogRepository.observeMainLanguage(),
                entriesFlow,
                settingsRepository.observeSettings(),
                progressFlow,
            ) { title, mainLang, entries, settings, progressList ->
                val orderedSelected = settings.selectedLanguages.toList()
                val progressByWordUid = progressList.groupBy { it.wordUid }
                val words = entries.map { entry ->
                    LearnWordItem(
                        wordUid = entry.wordUid,
                        mainTranslation = entry.translations[mainLang] ?: "",
                        additionalTranslations = orderedSelected.mapNotNull { lang ->
                            entry.translations[lang]?.let { lang to it }
                        },
                        status = computeWordStatus(
                            entry, mainLang, orderedSelected,
                            settings.translationDirection,
                            progressByWordUid,
                        ),
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

    private fun computeWordStatus(
        entry: WordEntry,
        mainLang: String,
        selectedLangs: List<String>,
        direction: TranslationDirection,
        progressByWordUid: Map<String, List<WordProgressEntity>>,
    ): WordStatus {
        val wordProgress = progressByWordUid[entry.wordUid] ?: emptyList()

        val eligibleKeys = mutableListOf<Pair<String, String>>()
        selectedLangs.forEach { lang ->
            val hasMain = entry.translations.containsKey(mainLang)
            val hasLang = entry.translations.containsKey(lang)
            when (direction) {
                TranslationDirection.MAIN_TO_ADDITIONAL ->
                    if (hasMain && hasLang) eligibleKeys += mainLang to lang
                TranslationDirection.ADDITIONAL_TO_MAIN ->
                    if (hasLang && hasMain) eligibleKeys += lang to mainLang
                TranslationDirection.BOTH -> if (hasMain && hasLang) {
                    eligibleKeys += mainLang to lang
                    eligibleKeys += lang to mainLang
                }
            }
        }

        if (eligibleKeys.isEmpty()) return WordStatus.NONE

        val progressBySrcTgt = wordProgress.associateBy { it.sourceLanguage to it.targetLanguage }
        if (eligibleKeys.all { progressBySrcTgt[it]?.isLearned == true }) return WordStatus.LEARNED

        return if (wordProgress.any { it.failureCount > 0 }) WordStatus.HAS_MISTAKES else WordStatus.NONE
    }
}
