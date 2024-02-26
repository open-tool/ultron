package com.atiurin.sampleapp.test.compose.simple

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.likesCounterButton
import com.atiurin.sampleapp.compose.ButtonWithCount
import com.atiurin.sampleapp.test.base.UltronBaseTest
import com.atiurin.ultron.allure.step.step
import com.atiurin.ultron.core.compose.createDefaultUltronComposeRule
import com.atiurin.ultron.core.compose.nodeinteraction.click
import com.atiurin.ultron.extensions.assertIsDisplayed
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.extensions.withName
import com.atiurin.ultron.extensions.withResultHandler
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.page.Screen
import org.junit.Rule
import org.junit.Test

class UltronSimpleComposeTest : UltronBaseTest() {
    @get:Rule
    val composeRule = createDefaultUltronComposeRule()

    @Test
    fun simpleAssertionTest() {
        val testTagValue = "testTag"
        composeRule.setContent {
            Column {
                Text(text = "Hello, world!", modifier = Modifier.semantics { testTag = testTagValue })
                Text(text = "Text without testTag")
                ButtonWithCount()
            }
        }
        hasTestTag(testTagValue).assertIsDisplayed().assertTextContains("Hello, world!")
        hasText("Text without testTag").assertIsDisplayed()
        hasTestTag(likesCounterButton).click().assertTextContains("Like count = 1")
    }

    @Test
    fun simpleScreenAssertionFailedTest() {
        val testTagValue = "testTag"
        composeRule.setContent {
            Column {
                Text(text = "Hello, world!", modifier = Modifier.semantics { testTag = testTagValue })
                Text(text = "Text without testTag")
                ButtonWithCount()
            }
        }
        UltronSimpleScreen {
            textWithTag.assertIsDisplayed().assertTextContains("Hello, world!")
            textWithoutTag.assertIsDisplayed()
            step("CUSTOM STEP") {
                likesButton.click()
            }
        }
    }
}

object UltronSimpleScreen : Screen<UltronSimpleScreen>() {
    val textWithTag = hasTestTag("testTag")
    val textWithoutTag = hasText("Text without testTag")
    val likesButton = hasTestTag(likesCounterButton)
}

