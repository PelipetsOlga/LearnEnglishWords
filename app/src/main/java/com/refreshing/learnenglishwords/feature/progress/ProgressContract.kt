package com.refreshing.learnenglishwords.feature.progress

import com.refreshing.learnenglishwords.domain.progress.ProgressAggregator

data class TopicProgressRow(
    val topicKey: String,
    val title: String,
    val stats: ProgressAggregator.Stats,
)

data class ProgressUiState(
    val isLoading: Boolean = true,
    val overall: ProgressAggregator.Stats = ProgressAggregator.Stats.empty,
    val topics: List<TopicProgressRow> = emptyList(),
    val resetAllConfirm: Boolean = false,
    val resetTopicConfirmKey: String? = null,
)

sealed interface ProgressIntent {
    data object ResetAllRequested : ProgressIntent
    data object ResetAllConfirmed : ProgressIntent
    data object ResetAllDismissed : ProgressIntent
    data class ResetTopicRequested(val topicKey: String) : ProgressIntent
    data object ResetTopicConfirmed : ProgressIntent
    data object ResetTopicDismissed : ProgressIntent
}
