package com.refreshing.learnenglishwords.feature.quiz

import com.refreshing.learnenglishwords.core.model.QuizTask

data class QuizUiState(
    val isLoading: Boolean = true,
    val isEmpty: Boolean = false,
    val currentTask: QuizTask? = null,
    val currentIndex: Int = 0,
    val totalTasks: Int = 0,
    val selectedOption: String? = null,
    val isCorrect: Boolean? = null,
    val isComplete: Boolean = false,
    val questionsAnswered: Int = 0,
    val mistakeCount: Int = 0,
    val newlyLearnedCount: Int = 0,
    val remainingCount: Int = 0,
    val showCloseConfirm: Boolean = false,
)

sealed interface QuizIntent {
    data class OptionSelected(val option: String) : QuizIntent
    data object ContinueClicked : QuizIntent
    data object CloseClicked : QuizIntent
    data object CloseConfirmed : QuizIntent
    data object CloseDismissed : QuizIntent
}

sealed interface QuizEffect {
    data object NavigateBack : QuizEffect
}
