package com.refreshing.learnenglishwords.feature.startup

sealed interface StartupUiState {
    data object Loading : StartupUiState
    data object Ready : StartupUiState
    data class Error(val message: String, val canRetry: Boolean = true) : StartupUiState
}

sealed interface StartupIntent {
    data object RetryClicked : StartupIntent
}

sealed interface StartupEffect {
    data object NavigateToTopics : StartupEffect
}
