package com.refreshing.learnenglishwords.ui.topics

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import com.refreshing.learnenglishwords.ui.BaseUiTest
import com.refreshing.learnenglishwords.ui.pom.BottomNavPom
import com.refreshing.learnenglishwords.ui.pom.SubtopicsScreenPom
import com.refreshing.learnenglishwords.ui.pom.TopicsScreenPom
import com.refreshing.learnenglishwords.utils.waitForAppear
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
class TopicsScreenTest : BaseUiTest() {

    @Test
    fun topics_list_is_displayed_after_startup() = runTest {
        with(TopicsScreenPom(this)) {
            topicsList.element.waitForAppear(this@runTest, timeout = 15_000)
            topicCard.elements.onFirst().assertExists()
        }
    }

    @Test
    fun bottom_navigation_bar_is_visible_on_topics_screen() = runTest {
        with(TopicsScreenPom(this)) {
            topicsList.element.waitForAppear(this@runTest, timeout = 15_000)
        }
        with(BottomNavPom(this)) {
            navBar.element.assertExists()
            topicsTab.element.assertExists()
            progressTab.element.assertExists()
            settingsTab.element.assertExists()
        }
    }

    @Test
    fun clicking_topic_card_navigates_to_subtopics() = runTest {
        with(TopicsScreenPom(this)) {
            topicsList.element.waitForAppear(this@runTest, timeout = 15_000)
            topicCard.elements.onFirst().performClick()
        }
        with(SubtopicsScreenPom(this)) {
            subtopicsList.element.waitForAppear(this@runTest)
        }
    }
}
