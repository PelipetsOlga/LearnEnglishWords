package com.refreshing.learnenglishwords.feature.progress

import com.refreshing.learnenglishwords.domain.progress.ProgressAggregator

data class ProgressUiState(
    val isLoading: Boolean = true,
    val overall: ProgressAggregator.Stats = ProgressAggregator.Stats.empty,
    val resetAllConfirm: Boolean = false,
)

sealed interface ProgressIntent {
    data object ResetAllRequested : ProgressIntent
    data object ResetAllConfirmed : ProgressIntent
    data object ResetAllDismissed : ProgressIntent
}
