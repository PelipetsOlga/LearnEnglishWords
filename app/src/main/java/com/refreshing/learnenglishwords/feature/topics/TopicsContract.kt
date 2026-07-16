package com.refreshing.learnenglishwords.feature.topics

import com.refreshing.learnenglishwords.core.model.Topic

data class TopicsUiState(
    val topics: List<Topic> = emptyList(),
    val isLoading: Boolean = true,
    val resetConfirmTopicKey: String? = null,
)

sealed interface TopicsIntent {
    data class TopicClicked(val topicKey: String) : TopicsIntent
    data class ResetTopicRequested(val topicKey: String) : TopicsIntent
    data object ResetConfirmed : TopicsIntent
    data object ResetDismissed : TopicsIntent
}

sealed interface TopicsEffect {
    data class NavigateToSubtopics(val topicKey: String) : TopicsEffect
}
