package com.refreshing.learnenglishwords.core.model

data class QuizTask(
    val wordUid: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    /** Text shown as the question prompt (source language translation). */
    val prompt: String,
    /** The correct target language translation. */
    val correctAnswer: String,
    /** All answer choices (shuffled), exactly one equals correctAnswer. */
    val options: List<String>,
)
