package com.refreshing.learnenglishwords.core.model

data class QuizSettings(
    val quizOrder: QuizOrder = QuizOrder.RANDOM,
    val selectedLanguages: Set<String> = emptySet(),
    val translationDirection: TranslationDirection = TranslationDirection.MAIN_TO_ADDITIONAL,
)
