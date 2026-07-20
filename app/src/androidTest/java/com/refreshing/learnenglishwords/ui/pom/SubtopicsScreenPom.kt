package com.refreshing.learnenglishwords.ui.pom

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.refreshing.learnenglishwords.ui.BasePom

@OptIn(ExperimentalTestApi::class)
class SubtopicsScreenPom(test: ComposeUiTest) : BasePom(test) {
    val subtopicsList = PageElement("subtopics_list")
    val subtopicCard = PageElement("subtopic_card")
    val subtopicQuizButton = PageElement("subtopic_quiz_button")
    val quizAllButton = PageElement("quiz_all_button")
    val allWordsCard = PageElement("all_words_card")
}
