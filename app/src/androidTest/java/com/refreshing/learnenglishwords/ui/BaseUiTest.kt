package com.refreshing.learnenglishwords.ui

import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.runAndroidComposeUiTest
import com.refreshing.learnenglishwords.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
abstract class BaseUiTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @Before
    open fun setup() {
        hiltRule.inject()
    }

    protected fun runTest(
        block: AndroidComposeUiTest<MainActivity>.() -> Unit,
    ) {
        runAndroidComposeUiTest<MainActivity> {
            try {
                block()
            } catch (t: Throwable) {
                t.printStackTrace()
                onRoot().printToLog("semantics_tree")
                throw t
            } finally {
                waitForIdle()
            }
        }
    }
}
