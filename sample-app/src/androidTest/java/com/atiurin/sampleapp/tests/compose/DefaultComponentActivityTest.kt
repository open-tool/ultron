package com.atiurin.sampleapp.tests.compose

import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.test.hasTestTag
import com.atiurin.ultron.core.compose.createDefaultUltronComposeRule
import com.atiurin.ultron.extensions.assertIsDisplayed
import org.junit.Rule
import org.junit.Test

class DefaultComponentActivityTest {
    @get:Rule
    val composeRule = createDefaultUltronComposeRule()

    @Test
    fun setContent() {
        val testTagValue = "testTag"
        composeRule.setContent {
            Text(text = "Hello, world!", modifier = Modifier.semantics { testTag = testTagValue })
        }
        hasTestTag(testTagValue)
            .assertIsDisplayed()
            .assertTextEquals("Hello, world!")
    }
}