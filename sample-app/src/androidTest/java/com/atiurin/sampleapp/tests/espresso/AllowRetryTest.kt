package com.atiurin.sampleapp.tests.espresso

import androidx.compose.ui.test.hasText
import com.atiurin.sampleapp.activity.XmlAndComposeActivity
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.core.compose.config.UltronComposeConfig
import com.atiurin.ultron.core.compose.createUltronComposeRule
import com.atiurin.ultron.extensions.assertIsDisplayed
import org.junit.After
import org.junit.Before
import org.junit.Test

class AllowRetryTest : BaseTest() {
    private val activityRule = createUltronComposeRule<XmlAndComposeActivity>()

    @Before
    fun setUp() {
        UltronComposeConfig.isExceptionAllowed = { exception ->
            when(exception) {
                is IllegalStateException -> {
                    exception.message?.startsWith("No compose hierarchies found in the app.") == true
                }
                else -> false
            }
        }
    }

    @After
    fun tearDown() { UltronComposeConfig.isExceptionAllowed = { false } }

    init {
        ruleSequence.addLast(activityRule)
    }

    @Test
    fun retryNoComposeHierarchy() {
        hasText("Compose Text").assertIsDisplayed()
    }
}