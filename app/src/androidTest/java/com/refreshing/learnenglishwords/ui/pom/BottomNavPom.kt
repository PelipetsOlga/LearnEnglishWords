package com.refreshing.learnenglishwords.ui.pom

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.refreshing.learnenglishwords.ui.BasePom

@OptIn(ExperimentalTestApi::class)
class BottomNavPom(test: ComposeUiTest) : BasePom(test) {
    val navBar = PageElement("bottom_nav")
    val topicsTab = PageElement("nav_topics")
    val progressTab = PageElement("nav_progress")
    val settingsTab = PageElement("nav_settings")
}
