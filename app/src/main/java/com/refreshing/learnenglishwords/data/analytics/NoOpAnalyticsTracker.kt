package com.refreshing.learnenglishwords.data.analytics

import com.refreshing.learnenglishwords.domain.analytics.AnalyticsTracker
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoOpAnalyticsTracker @Inject constructor() : AnalyticsTracker {
    override fun trackQuizStart(subtopicKey: String) = Unit
    override fun trackQuizAnswer(subtopicKey: String, isCorrect: Boolean) = Unit
    override fun trackQuizComplete(subtopicKey: String) = Unit
    override fun trackProgressReset(topicKey: String, subtopicKey: String?) = Unit
}
