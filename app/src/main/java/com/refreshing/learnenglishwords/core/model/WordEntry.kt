package com.refreshing.learnenglishwords.core.model

/** A word with all its translations, as loaded from the catalog. */
data class WordEntry(
    val wordUid: String,
    val translations: Map<String, String>, // language code -> text
)
