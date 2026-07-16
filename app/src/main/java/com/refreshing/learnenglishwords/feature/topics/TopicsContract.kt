package com.refreshing.learnenglishwords.feature.topics

import com.refreshing.learnenglishwords.core.model.Topic

data class TopicsUiState(
    val topics: List<Topic> = emptyList(),
    val isLoading: Boolean = true,
)

sealed interface TopicsIntent {
    data class TopicClicked(val topicKey: String) : TopicsIntent
}

sealed interface TopicsEffect {
    data class NavigateToSubtopics(val topicKey: String) : TopicsEffect
}
