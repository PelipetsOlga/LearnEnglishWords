package com.refreshing.learnenglishwords.ui.navigation

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import com.refreshing.learnenglishwords.ui.BaseUiTest
import com.refreshing.learnenglishwords.ui.pom.BottomNavPom
import com.refreshing.learnenglishwords.ui.pom.LearnScreenPom
import com.refreshing.learnenglishwords.ui.pom.ProgressScreenPom
import com.refreshing.learnenglishwords.ui.pom.SettingsScreenPom
import com.refreshing.learnenglishwords.ui.pom.SubtopicsScreenPom
import com.refreshing.learnenglishwords.ui.pom.TopicsScreenPom
import com.refreshing.learnenglishwords.utils.waitForAppear
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
class NavigationFlowTest : BaseUiTest() {

    @Test
    fun bottom_nav_switches_to_progress_screen() = runTest {
        with(TopicsScreenPom(this)) {
            topicsList.element.waitForAppear(this@runTest, timeout = 15_000)
        }
        with(BottomNavPom(this)) {
            progressTab.element.performClick()
        }
        with(ProgressScreenPom(this)) {
            content.element.waitForAppear(this@runTest)
        }
    }

    @Test
    fun bottom_nav_switches_to_settings_screen() = runTest {
        with(TopicsScreenPom(this)) {
            topicsList.element.waitForAppear(this@runTest, timeout = 15_000)
        }
        with(BottomNavPom(this)) {
            settingsTab.element.performClick()
        }
        with(SettingsScreenPom(this)) {
            content.element.waitForAppear(this@runTest)
        }
    }

    @Test
    fun bottom_nav_returns_to_topics_from_settings() = runTest {
        with(TopicsScreenPom(this)) {
            topicsList.element.waitForAppear(this@runTest, timeout = 15_000)
        }
        with(BottomNavPom(this)) {
            settingsTab.element.performClick()
        }
        with(SettingsScreenPom(this)) {
            content.element.waitForAppear(this@runTest)
        }
        with(BottomNavPom(this)) {
            topicsTab.element.performClick()
        }
        with(TopicsScreenPom(this)) {
            topicsList.element.waitForAppear(this@runTest)
        }
    }

    @Test
    fun full_flow_topics_subtopics_learn() = runTest {
        with(TopicsScreenPom(this)) {
            topicsList.element.waitForAppear(this@runTest, timeout = 15_000)
            topicCard.elements.onFirst().performClick()
        }
        with(SubtopicsScreenPom(this)) {
            subtopicsList.element.waitForAppear(this@runTest)
            subtopicCard.elements.onFirst().performClick()
        }
        with(LearnScreenPom(this)) {
            wordList.element.waitForAppear(this@runTest)
            startQuizButton.element.assertExists()
        }
    }
}
