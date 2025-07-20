package com.atiurin.sampleapp.tests.compose

import com.atiurin.sampleapp.activity.ComposeElementsActivity
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.ComposeElementsPage
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.core.compose.createSimpleUltronComposeRule
import com.atiurin.ultron.extensions.withAssertion
import com.atiurin.ultron.extensions.withTimeout
import com.atiurin.ultron.core.compose.nodeinteraction.click
import org.junit.Rule
import org.junit.Test

class ComposeCustomAssertionTest : BaseTest() {
    val page = ComposeElementsPage

    @get:Rule
    val composeRule = createSimpleUltronComposeRule<ComposeElementsActivity>()

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