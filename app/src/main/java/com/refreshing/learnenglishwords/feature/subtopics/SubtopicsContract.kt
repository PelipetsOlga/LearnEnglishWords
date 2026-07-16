package com.refreshing.learnenglishwords.feature.subtopics

import com.refreshing.learnenglishwords.core.model.Subtopic

data class SubtopicsUiState(
    val topicKey: String = "",
    val topicTitle: String = "",
    val subtopics: List<Subtopic> = emptyList(),
    val isLoading: Boolean = true,
    val resetConfirmSubtopicUid: String? = null,
)

sealed interface SubtopicsIntent {
    data class LearnClicked(val subtopicUid: String) : SubtopicsIntent
    data class QuizClicked(val subtopicUid: String) : SubtopicsIntent
    data class ResetSubtopicRequested(val subtopicUid: String) : SubtopicsIntent
    data object ResetConfirmed : SubtopicsIntent
    data object ResetDismissed : SubtopicsIntent
}

sealed interface SubtopicsEffect {
    data class NavigateToLearn(val subtopicUid: String) : SubtopicsEffect
    data class NavigateToQuiz(val subtopicUid: String) : SubtopicsEffect
}
