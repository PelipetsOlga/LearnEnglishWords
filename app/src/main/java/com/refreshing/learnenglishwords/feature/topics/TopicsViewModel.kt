package com.refreshing.learnenglishwords.feature.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refreshing.learnenglishwords.domain.catalog.CatalogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicsViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TopicsUiState())
    val uiState: StateFlow<TopicsUiState> = _uiState.asStateFlow()

    private val _effects = Channel<TopicsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            catalogRepository.observeTopics().collect { topics ->
                _uiState.value = _uiState.value.copy(topics = topics, isLoading = false)
            }
        }
    }

    fun onIntent(intent: TopicsIntent) {
        when (intent) {
            is TopicsIntent.TopicClicked ->
                viewModelScope.launch { _effects.send(TopicsEffect.NavigateToSubtopics(intent.topicKey)) }
        }
    }
}
