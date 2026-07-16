package com.refreshing.learnenglishwords.domain.analytics

interface AnalyticsTracker {
    fun trackQuizStart(subtopicKey: String)
    fun trackQuizAnswer(subtopicKey: String, isCorrect: Boolean)
    fun trackQuizComplete(subtopicKey: String)
    fun trackProgressReset(topicKey: String, subtopicKey: String?)
}
