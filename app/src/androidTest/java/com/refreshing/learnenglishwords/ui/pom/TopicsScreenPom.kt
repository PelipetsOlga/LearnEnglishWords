package com.refreshing.learnenglishwords.ui.pom

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.refreshing.learnenglishwords.ui.BasePom

@OptIn(ExperimentalTestApi::class)
class TopicsScreenPom(test: ComposeUiTest) : BasePom(test) {
    val topicsList = PageElement("topics_list")
    val topicCard = PageElement("topic_card")
}
