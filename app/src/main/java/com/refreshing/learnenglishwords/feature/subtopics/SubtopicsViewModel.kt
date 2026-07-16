package com.refreshing.learnenglishwords.feature.subtopics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refreshing.learnenglishwords.domain.catalog.CatalogRepository
import com.refreshing.learnenglishwords.domain.progress.ProgressRepository
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
class SubtopicsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val catalogRepository: CatalogRepository,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val topicKey: String = checkNotNull(savedStateHandle["topicKey"])

    private val _uiState = MutableStateFlow(SubtopicsUiState(topicKey = topicKey))
    val uiState: StateFlow<SubtopicsUiState> = _uiState.asStateFlow()

    private val _effects = Channel<SubtopicsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                catalogRepository.observeTopics(),
                catalogRepository.observeSubtopics(topicKey),
            ) { topics, subtopics ->
                val title = topics.find { it.topicKey == topicKey }?.title ?: topicKey
                _uiState.value.copy(topicTitle = title, subtopics = subtopics, isLoading = false)
            }.collect { _uiState.value = it }
        }
    }

    fun onIntent(intent: SubtopicsIntent) {
        when (intent) {
            is SubtopicsIntent.LearnClicked ->
                viewModelScope.launch { _effects.send(SubtopicsEffect.NavigateToLearn(intent.subtopicUid)) }

            is SubtopicsIntent.QuizClicked ->
                viewModelScope.launch { _effects.send(SubtopicsEffect.NavigateToQuiz(intent.subtopicUid)) }

            is SubtopicsIntent.ResetSubtopicRequested ->
                _uiState.value = _uiState.value.copy(resetConfirmSubtopicUid = intent.subtopicUid)

            SubtopicsIntent.ResetSubtopicDismissed ->
                _uiState.value = _uiState.value.copy(resetConfirmSubtopicUid = null)

            SubtopicsIntent.ResetSubtopicConfirmed -> {
                val uid = _uiState.value.resetConfirmSubtopicUid ?: return
                _uiState.value = _uiState.value.copy(resetConfirmSubtopicUid = null)
                viewModelScope.launch { progressRepository.resetSubtopic(uid) }
            }

            SubtopicsIntent.ResetTopicRequested ->
                _uiState.value = _uiState.value.copy(resetConfirmTopic = true)

            SubtopicsIntent.ResetTopicDismissed ->
                _uiState.value = _uiState.value.copy(resetConfirmTopic = false)

            SubtopicsIntent.ResetTopicConfirmed -> {
                _uiState.value = _uiState.value.copy(resetConfirmTopic = false)
                viewModelScope.launch { progressRepository.resetTopic(topicKey) }
            }
        }
    }
}
