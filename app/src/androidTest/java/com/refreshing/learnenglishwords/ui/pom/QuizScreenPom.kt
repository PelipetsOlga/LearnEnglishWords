package com.refreshing.learnenglishwords.ui.pom

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.refreshing.learnenglishwords.ui.BasePom

@OptIn(ExperimentalTestApi::class)
class QuizScreenPom(test: ComposeUiTest) : BasePom(test) {
    val closeButton = PageElement("quiz_close_button")
    val prompt = PageElement("quiz_prompt")
    val option = PageElement("quiz_option")
    val continueButton = PageElement("quiz_continue_button")
    val summary = PageElement("quiz_summary")
    val empty = PageElement("quiz_empty")
}
