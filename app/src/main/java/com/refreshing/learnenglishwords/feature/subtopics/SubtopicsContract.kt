package com.refreshing.learnenglishwords.feature.subtopics

import com.refreshing.learnenglishwords.core.model.Subtopic

data class SubtopicsUiState(
    val topicKey: String = "",
    val topicTitle: String = "",
    val subtopics: List<Subtopic> = emptyList(),
    val isLoading: Boolean = true,
    val resetConfirmSubtopicUid: String? = null,
    val resetConfirmTopic: Boolean = false,
)

sealed interface SubtopicsIntent {
    data class LearnClicked(val subtopicUid: String) : SubtopicsIntent
    data class QuizClicked(val subtopicUid: String) : SubtopicsIntent
    data class ResetSubtopicRequested(val subtopicUid: String) : SubtopicsIntent
    data object ResetSubtopicConfirmed : SubtopicsIntent
    data object ResetSubtopicDismissed : SubtopicsIntent
    data object ResetTopicRequested : SubtopicsIntent
    data object ResetTopicConfirmed : SubtopicsIntent
    data object ResetTopicDismissed : SubtopicsIntent
}

sealed interface SubtopicsEffect {
    data class NavigateToLearn(val subtopicUid: String) : SubtopicsEffect
    data class NavigateToQuiz(val subtopicUid: String) : SubtopicsEffect
}
