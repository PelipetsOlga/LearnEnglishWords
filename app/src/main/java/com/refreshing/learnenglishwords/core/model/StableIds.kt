package com.refreshing.learnenglishwords.core.model

fun subtopicUid(topicKey: String, subtopicKey: String): String =
    "$topicKey/$subtopicKey"

fun wordUid(topicKey: String, subtopicKey: String, wordId: Int): String =
    "$topicKey/$subtopicKey/$wordId"
