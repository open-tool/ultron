package com.atiurin.sampleapp.tests.compose

import com.atiurin.sampleapp.activity.ComposeElementsActivity
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.ComposeElementsPage
import com.atiurin.ultron.core.compose.createUltronComposeRule
import com.atiurin.ultron.core.compose.nodeinteraction.click
import com.atiurin.ultron.extensions.withAssertion
import com.atiurin.ultron.extensions.withTimeout
import org.junit.Rule
import org.junit.Test

class ComposeCustomAssertionTest {
    val page = ComposeElementsPage

    @get:Rule
    val composeRule = createUltronComposeRule<ComposeElementsActivity>()

    @Test
    fun validAssertion(){
        page.likesCounter.withAssertion {
            page.likesCounter.withTimeout(100).assertTextEquals("Like count = 3")
        }.click()
    }

    @Test
    fun invalidAssertion(){
        AssertUtils.assertException {
            page.likesCounter.withTimeout(1000).withAssertion {
                page.likesCounter.withTimeout(500).assertTextEquals("some invalid text")
            }.click()
        }
    }
}