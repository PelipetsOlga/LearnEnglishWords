package com.refreshing.learnenglishwords.ui.pom

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.refreshing.learnenglishwords.ui.BasePom

@OptIn(ExperimentalTestApi::class)
class ProgressScreenPom(test: ComposeUiTest) : BasePom(test) {
    val content = PageElement("progress_content")
    val overallCard = PageElement("progress_overall_card")
    val resetAllButton = PageElement("reset_all_button")
}
