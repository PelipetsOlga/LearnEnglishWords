package com.refreshing.learnenglishwords.utils

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.isDisplayed

/**
 * Waits until the node exists and is displayed, retrying up to [timeout] ms.
 * Mirrors yoga-android's SemanticsNodeInteractionExtension.waitForAppear.
 */
@OptIn(ExperimentalTestApi::class)
fun SemanticsNodeInteraction.waitForAppear(
    test: ComposeUiTest,
    timeout: Long = 5_000,
): SemanticsNodeInteraction {
    test.waitUntil(timeoutMillis = timeout) {
        runCatching { assertExists() }.isSuccess && isDisplayed()
    }
    return this
}
