package com.atiurin.sampleapp.tests.compose

import androidx.compose.ui.test.hasTestTag
import com.atiurin.sampleapp.activity.ComposeElementsActivity
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.core.compose.createUltronComposeRule
import com.atiurin.ultron.extensions.assertIsDisplayed
import com.atiurin.ultron.extensions.withName
import com.atiurin.ultron.extensions.withTimeout
import com.atiurin.ultron.page.Page
import org.junit.Rule
import org.junit.Test

class SampleClassTest : BaseTest() {
    @get:Rule
    val composeRuleBase = createUltronComposeRule<ComposeElementsActivity>()

    @Test
    fun test() {
        SomePage{
            elementWithName.assertIsDisplayed()
            elementWithTimeout.assertIsDisplayed()
            elementMatcher.assertIsDisplayed()
        }
    }

    @Test
    fun test2() {
        SomePage{
            elementWithName.assertIsDisplayed()
            elementWithTimeout.assertIsDisplayed()
            elementMatcher.assertIsDisplayed()
        }
    }
}

object SomePage : Page<SomePage>() {
    val elementWithName = hasTestTag("statusText").withName("sample element name")
    val elementWithTimeout = hasTestTag("statusText").withTimeout(4000)
    val elementMatcher = hasTestTag("statusText")
}