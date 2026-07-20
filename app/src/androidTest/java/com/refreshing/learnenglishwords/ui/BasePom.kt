package com.refreshing.learnenglishwords.ui

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag

/**
 * Base class for all Page Object Model classes.
 * Each POM wraps a [ComposeUiTest] and exposes [PageElement] handles
 * that resolve nodes lazily by their test tag.
 *
 * Mirrors the BasePom pattern from the yoga-android project.
 */
@OptIn(ExperimentalTestApi::class)
open class BasePom(val test: ComposeUiTest) {

    inner class PageElement(val tag: String) {

        val element: SemanticsNodeInteraction
            get() = test.onNodeWithTag(tag)

        val elements: SemanticsNodeInteractionCollection
            get() = test.onAllNodesWithTag(tag)
    }
}
