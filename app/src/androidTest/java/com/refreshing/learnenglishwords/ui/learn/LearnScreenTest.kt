package com.refreshing.learnenglishwords.ui.learn

import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import com.refreshing.learnenglishwords.MainActivity
import com.refreshing.learnenglishwords.ui.BaseUiTest
import com.refreshing.learnenglishwords.ui.pom.LearnScreenPom
import com.refreshing.learnenglishwords.ui.pom.QuizScreenPom
import com.refreshing.learnenglishwords.ui.pom.SubtopicsScreenPom
import com.refreshing.learnenglishwords.ui.pom.TopicsScreenPom
import com.refreshing.learnenglishwords.utils.waitForAppear
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
class LearnScreenTest : BaseUiTest() {

    @Test
    fun word_list_is_displayed_on_learn_screen() = runTest {
        navigateToLearnScreen()
        with(LearnScreenPom(this)) {
            wordList.element.waitForAppear(this@runTest)
        }
    }

    @Test
    fun start_quiz_button_is_visible_on_learn_screen() = runTest {
        navigateToLearnScreen()
        with(LearnScreenPom(this)) {
            wordList.element.waitForAppear(this@runTest)
            startQuizButton.element.assertExists()
        }
    }

    @Test
    fun start_quiz_button_navigates_to_quiz_screen() = runTest {
        navigateToLearnScreen()
        with(LearnScreenPom(this)) {
            wordList.element.waitForAppear(this@runTest)
            startQuizButton.element.performClick()
        }
        with(QuizScreenPom(this)) {
            waitUntil(timeoutMillis = 5_000) {
                runCatching { prompt.element.assertExists() }.isSuccess ||
                    runCatching { empty.element.assertExists() }.isSuccess
            }
        }
    }

    private fun AndroidComposeUiTest<MainActivity>.navigateToLearnScreen() {
        with(TopicsScreenPom(this)) {
            topicsList.element.waitForAppear(this@navigateToLearnScreen, timeout = 15_000)
            topicCard.elements.onFirst().performClick()
        }
        with(SubtopicsScreenPom(this)) {
            subtopicsList.element.waitForAppear(this@navigateToLearnScreen)
            subtopicCard.elements.onFirst().performClick()
        }
    }
}
