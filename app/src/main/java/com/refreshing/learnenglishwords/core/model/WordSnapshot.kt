package com.refreshing.learnenglishwords.core.model

import com.refreshing.learnenglishwords.data.local.entity.WordProgressEntity

/**
 * A resolved snapshot of a word at quiz-session start.
 * Contains all translations and current progress per direction.
 */
data class WordSnapshot(
    val wordUid: String,
    val position: Int,
    val translations: Map<String, String>,
    /** Key: (sourceLanguage, targetLanguage) */
    val progressByDirection: Map<Pair<String, String>, WordProgressEntity> = emptyMap(),
)
