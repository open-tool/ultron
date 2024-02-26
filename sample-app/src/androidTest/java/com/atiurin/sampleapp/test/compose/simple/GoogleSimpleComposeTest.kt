package com.atiurin.sampleapp.test.compose.simple

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.atiurin.sampleapp.activity.ComposeElementsActivity
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.likesCounterButton
import com.atiurin.sampleapp.compose.ButtonWithCount
import org.junit.Rule
import org.junit.Test

class GoogleSimpleComposeTest {
    @get:Rule
    val composeRule = createComposeRule()

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

        composeRule.onNode(hasTestTag(testTagValue))
            .assertIsDisplayed()
            .assertTextContains("Hello, world!")
        composeRule.onNode(hasText("Text without testTag"))
            .assertIsDisplayed()
        composeRule.onNodeWithTag(likesCounterButton)
            .performClick()
            .assertTextContains("Like count = 1")
    }
}