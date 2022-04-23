package com.atiurin.sampleapp.tests.compose

import com.atiurin.sampleapp.activity.ComposeElementsActivity
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.ComposeElementsPage
import com.atiurin.ultron.core.compose.createUltronComposeRule
import com.atiurin.ultron.core.compose.operation.ComposeOperationResult
import com.atiurin.ultron.core.compose.operation.ComposeOperationType
import com.atiurin.ultron.core.compose.operation.UltronComposeOperation
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.extensions.*
import com.atiurin.ultron.testlifecycle.rulesequence.RuleSequence
import com.atiurin.ultron.testlifecycle.setupteardown.SetUp
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import com.atiurin.ultron.testlifecycle.setupteardown.TearDown
import com.atiurin.ultron.testlifecycle.setupteardown.TearDownRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class ComposeConfigTest {
    companion object {
        const val setCustomTimeout = "Set custom timeout"
        const val dropCustomTimeout = "Drop custom timeout"
        const val customTimeout = 1100L
    }

    val page = ComposeElementsPage
    val composeRule = createUltronComposeRule<ComposeElementsActivity>()
    val setUpRule = SetUpRule().add(setCustomTimeout) { UltronConfig.Compose.OPERATION_TIMEOUT = customTimeout }
    val tearDownRule = TearDownRule().add(dropCustomTimeout){ UltronConfig.Compose.OPERATION_TIMEOUT = UltronConfig.Compose.DEFAULT_OPERATION_TIMEOUT}

    @get:Rule
    val ruleSequence = RuleSequence().add(composeRule,setUpRule, tearDownRule)


    @Test
    fun resultHandler_successOperation() {
        lateinit var result: ComposeOperationResult<UltronComposeOperation>
        page.editableText.withResultHandler { result = it }.assertTextContains("")
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.operation.type == ComposeOperationType.CONTAINS_TEXT)
    }

    @Test
    fun resultHandler_failedOperation() {
        lateinit var result: ComposeOperationResult<UltronComposeOperation>
        page.editableText.withResultHandler { result = it }.withTimeout(100).assertTextContains("invalid")
        Assert.assertFalse(result.success)
        Assert.assertTrue(result.operation.type == ComposeOperationType.CONTAINS_TEXT)
        Assert.assertTrue(result.exceptions.isNotEmpty())
        Assert.assertTrue(result.description.isNotEmpty())
    }

    @Test
    @SetUp(setCustomTimeout)
    @TearDown(dropCustomTimeout)
    fun operationTimeout() {
        AssertUtils.assertExecTimeBetween(customTimeout, 4900) {
            page.editableText.withResultHandler { }.assertDoesNotExist()
        }
    }

    @Test
    fun isSuccess_false() {
        Assert.assertFalse(page.editableText.isSuccess { withTimeout(100).assertDoesNotExist() })
    }

    @Test
    fun isSuccess_true() {
        Assert.assertTrue(page.editableText.isSuccess { assertExists() })
    }
}