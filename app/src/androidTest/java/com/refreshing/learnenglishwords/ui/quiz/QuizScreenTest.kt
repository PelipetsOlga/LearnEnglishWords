package com.refreshing.learnenglishwords.ui.quiz

import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.refreshing.learnenglishwords.MainActivity
import com.refreshing.learnenglishwords.ui.BaseUiTest
import com.refreshing.learnenglishwords.ui.pom.QuizScreenPom
import com.refreshing.learnenglishwords.ui.pom.SubtopicsScreenPom
import com.refreshing.learnenglishwords.ui.pom.TopicsScreenPom
import com.refreshing.learnenglishwords.utils.waitForAppear
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
class QuizScreenTest : BaseUiTest() {

    @Test
    fun quiz_screen_shows_question_prompt_and_options() = runTest {
        navigateToSubtopicQuiz()
        with(QuizScreenPom(this)) {
            prompt.element.waitForAppear(this@runTest)
            option.elements.onFirst().assertExists()
        }
    }

    @Test
    fun quiz_shows_four_answer_options() = runTest {
        navigateToSubtopicQuiz()
        with(QuizScreenPom(this)) {
            prompt.element.waitForAppear(this@runTest)
            repeat(4) { index ->
                option.elements[index].assertExists()
            }
        }
    }

    @Test
    fun selecting_option_reveals_continue_button() = runTest {
        navigateToSubtopicQuiz()
        with(QuizScreenPom(this)) {
            prompt.element.waitForAppear(this@runTest)
            option.elements.onFirst().performClick()
            continueButton.element.waitForAppear(this@runTest)
        }
    }

    @Test
    fun continue_button_advances_to_next_question_or_summary() = runTest {
        navigateToSubtopicQuiz()
        with(QuizScreenPom(this)) {
            prompt.element.waitForAppear(this@runTest)
            option.elements.onFirst().performClick()
            continueButton.element.waitForAppear(this@runTest)
            continueButton.element.performClick()
            waitForIdle()
            waitUntil(timeoutMillis = 5_000) {
                runCatching { prompt.element.assertExists() }.isSuccess ||
                    runCatching { summary.element.assertExists() }.isSuccess
            }
        }
    }

    @Test
    fun close_button_mid_quiz_shows_confirm_dialog() = runTest {
        navigateToSubtopicQuiz()
        with(QuizScreenPom(this)) {
            prompt.element.waitForAppear(this@runTest)
            closeButton.element.performClick()
        }
        waitForIdle()
        onNodeWithText("Quit quiz?").assertIsDisplayed()
    }

    @Test
    fun close_confirm_dialog_dismiss_keeps_quiz_open() = runTest {
        navigateToSubtopicQuiz()
        with(QuizScreenPom(this)) {
            prompt.element.waitForAppear(this@runTest)
            closeButton.element.performClick()
            waitForIdle()
            onNodeWithText("Continue quiz").performClick()
            waitForIdle()
            prompt.element.assertExists()
        }
    }

    @Test
    fun summary_screen_shows_quiz_complete_after_all_questions() = runTest {
        navigateToSubtopicQuiz()
        with(QuizScreenPom(this)) {
            // Wait for quiz to finish loading before entering the loop
            prompt.element.waitForAppear(this@runTest, timeout = 10_000)

            // Answer every question until the summary screen appears
            var guard = 0
            while (guard < 200) {
                val hasSummary = runCatching { summary.element.assertExists() }.isSuccess
                if (hasSummary) break
                val hasPrompt = runCatching { prompt.element.assertExists() }.isSuccess
                if (!hasPrompt) break
                option.elements.onFirst().performClick()
                continueButton.element.waitForAppear(this@runTest)
                continueButton.element.performClick()
                // Wait for the UI to settle into either the next question or the summary
                waitUntil(timeoutMillis = 5_000) {
                    runCatching { summary.element.assertExists() }.isSuccess ||
                        runCatching { prompt.element.assertExists() }.isSuccess
                }
                guard++
            }
            summary.element.waitForAppear(this@runTest, timeout = 15_000)
            onNodeWithText("Quiz complete!").assertExists()
        }
    }

    // Navigates to a single subtopic quiz (small word count, ~20 words)
    private fun AndroidComposeUiTest<MainActivity>.navigateToSubtopicQuiz() {
        with(TopicsScreenPom(this)) {
            topicsList.element.waitForAppear(this@navigateToSubtopicQuiz, timeout = 15_000)
            topicCard.elements.onFirst().performClick()
        }
        with(SubtopicsScreenPom(this)) {
            subtopicsList.element.waitForAppear(this@navigateToSubtopicQuiz)
            subtopicQuizButton.elements.onFirst().performClick()
        }
    }
}
