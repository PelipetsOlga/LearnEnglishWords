package com.refreshing.learnenglishwords.ui.progress

import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.refreshing.learnenglishwords.MainActivity
import com.refreshing.learnenglishwords.ui.BaseUiTest
import com.refreshing.learnenglishwords.ui.pom.BottomNavPom
import com.refreshing.learnenglishwords.ui.pom.ProgressScreenPom
import com.refreshing.learnenglishwords.ui.pom.TopicsScreenPom
import com.refreshing.learnenglishwords.utils.waitForAppear
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
class ProgressScreenTest : BaseUiTest() {

    @Test
    fun progress_screen_displays_overall_card() = runTest {
        navigateToProgress()
        with(ProgressScreenPom(this)) {
            content.element.waitForAppear(this@runTest)
            overallCard.element.assertExists()
        }
    }

    @Test
    fun progress_screen_displays_reset_all_button() = runTest {
        navigateToProgress()
        with(ProgressScreenPom(this)) {
            content.element.waitForAppear(this@runTest)
            resetAllButton.element.assertExists()
        }
    }

    @Test
    fun reset_all_button_shows_confirmation_dialog() = runTest {
        navigateToProgress()
        with(ProgressScreenPom(this)) {
            content.element.waitForAppear(this@runTest)
            resetAllButton.element.performClick()
        }
        waitForIdle()
        onNodeWithText("Reset all progress?").assertExists()
    }

    @Test
    fun reset_all_dialog_cancel_dismisses_dialog() = runTest {
        navigateToProgress()
        with(ProgressScreenPom(this)) {
            content.element.waitForAppear(this@runTest)
            resetAllButton.element.performClick()
        }
        waitForIdle()
        onNodeWithText("Cancel").performClick()
        waitForIdle()
        onNodeWithText("Reset all progress?").assertDoesNotExist()
    }

    private fun AndroidComposeUiTest<MainActivity>.navigateToProgress() {
        with(TopicsScreenPom(this)) {
            topicsList.element.waitForAppear(this@navigateToProgress, timeout = 15_000)
        }
        with(BottomNavPom(this)) {
            progressTab.element.performClick()
        }
    }
}
