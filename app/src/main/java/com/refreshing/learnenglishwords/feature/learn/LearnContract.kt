package com.refreshing.learnenglishwords.feature.learn

enum class WordStatus { NONE, LEARNED, HAS_MISTAKES }

data class LearnWordItem(
    val wordUid: String,
    val mainTranslation: String,
    val additionalTranslations: List<Pair<String, String>>, // languageCode to text
    val status: WordStatus = WordStatus.NONE,
)

data class LearnUiState(
    val subtopicTitle: String = "",
    val words: List<LearnWordItem> = emptyList(),
    val isEmpty: Boolean = false,
    val isLoading: Boolean = true,
)

sealed interface LearnIntent {
    data object StartQuizClicked : LearnIntent
}

sealed interface LearnEffect {
    data object NavigateToQuiz : LearnEffect
}
