package com.refreshing.learnenglishwords.ui.pom

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.refreshing.learnenglishwords.ui.BasePom

@OptIn(ExperimentalTestApi::class)
class LearnScreenPom(test: ComposeUiTest) : BasePom(test) {
    val wordList = PageElement("learn_word_list")
    val startQuizButton = PageElement("start_quiz_button")
}
