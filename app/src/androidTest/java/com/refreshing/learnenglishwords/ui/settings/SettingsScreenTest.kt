package com.refreshing.learnenglishwords.ui.settings

import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.refreshing.learnenglishwords.MainActivity
import com.refreshing.learnenglishwords.ui.BaseUiTest
import com.refreshing.learnenglishwords.ui.pom.BottomNavPom
import com.refreshing.learnenglishwords.ui.pom.SettingsScreenPom
import com.refreshing.learnenglishwords.ui.pom.TopicsScreenPom
import com.refreshing.learnenglishwords.utils.waitForAppear
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
class SettingsScreenTest : BaseUiTest() {

    @Test
    fun settings_screen_displays_content() = runTest {
        navigateToSettings()
        with(SettingsScreenPom(this)) {
            content.element.waitForAppear(this@runTest)
        }
    }

    @Test
    fun settings_screen_shows_vocabulary_section() = runTest {
        navigateToSettings()
        with(SettingsScreenPom(this)) {
            content.element.waitForAppear(this@runTest)
        }
        onNodeWithText("Vocabulary").assertIsDisplayed()
    }

    @Test
    fun settings_screen_shows_quiz_section() = runTest {
        navigateToSettings()
        with(SettingsScreenPom(this)) {
            content.element.waitForAppear(this@runTest)
        }
        onNodeWithText("Quiz").assertIsDisplayed()
    }

    @Test
    fun settings_screen_shows_translation_direction_options() = runTest {
        navigateToSettings()
        with(SettingsScreenPom(this)) {
            content.element.waitForAppear(this@runTest)
        }
        onNodeWithText("Translation direction").assertIsDisplayed()
        onNodeWithText("Both directions").assertIsDisplayed()
    }

    @Test
    fun settings_screen_shows_words_order_options() = runTest {
        navigateToSettings()
        with(SettingsScreenPom(this)) {
            content.element.waitForAppear(this@runTest)
        }
        onNodeWithText("Words order").assertIsDisplayed()
        onNodeWithText("Random").assertIsDisplayed()
    }

    private fun AndroidComposeUiTest<MainActivity>.navigateToSettings() {
        with(TopicsScreenPom(this)) {
            topicsList.element.waitForAppear(this@navigateToSettings, timeout = 15_000)
        }
        with(BottomNavPom(this)) {
            settingsTab.element.performClick()
        }
    }
}
