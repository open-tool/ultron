package com.atiurin.sampleapp.test.compose.simple

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.junit4.createComposeRule
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.likesCounterButton
import com.atiurin.sampleapp.compose.ButtonWithCount
import com.kaspersky.components.alluresupport.withForcedAllureSupport
import com.kaspersky.components.composesupport.config.addComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.github.kakaocup.compose.node.element.KNode
import org.junit.Rule
import org.junit.Test

class KaspressoSimpleComposeTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withForcedAllureSupport().addComposeSupport()
) {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun simpleAssertionTest() = run {
        val testTagValue = "testTag"
        composeRule.setContent {
            Column {
                Text(text = "Hello, world!", modifier = Modifier.semantics { testTag = testTagValue })
                Text(text = "Text without testTag")
                ButtonWithCount()
            }
        }
        onComposeScreen<ComposeSimpleScreen>(composeRule) {
            textWithTag {
                assertIsDisplayed()
                assertTextContains("Hello, world!")
            }
            textWithoutTag.assertIsDisplayed()
            likesButton {
                performClick()
                assertTextContains("Like count = 1")
            }
        }
    }

    @Test
    fun simpleAssertionFailedTestWithSteps() = run {
        val testTagValue = "testTag"
        composeRule.setContent {
            Column {
                Text(text = "Hello, world!", modifier = Modifier.semantics { testTag = testTagValue })
                Text(text = "Text without testTag")
                ButtonWithCount()
            }
        }
        onComposeScreen<ComposeSimpleScreen>(composeRule) {
            step("Assert text with tag") {
                textWithTag {
                    assertIsDisplayed()
                    assertTextContains("Hello, world!")
                }
            }
            step("Assert text without tag") {
                textWithoutTag.assertIsDisplayed()
            }
            step("Assert likes button") {
                likesButton {
                    performClick()
                    assertTextContains("Like count = 2")
                }
            }
        }
    }
}

class ComposeSimpleScreen(semanticsProvider: SemanticsNodeInteractionsProvider) : ComposeScreen<ComposeSimpleScreen>(
    semanticsProvider = semanticsProvider
) {
    val textWithTag: KNode = child {
        hasTestTag("testTag")
    }
    val textWithoutTag: KNode = child {
        hasText("Text without testTag")
    }
    val likesButton: KNode = child {
        hasTestTag(likesCounterButton)
    }
}