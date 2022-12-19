package com.atiurin.sampleapp.tests.espresso

import com.atiurin.sampleapp.framework.Log
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.UiElementsPage
import com.atiurin.sampleapp.tests.UiElementsTest
import com.atiurin.ultron.core.common.UltronDefaultOperationResultAnalyzer
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.extensions.isDisplayed
import com.atiurin.ultron.extensions.withResultHandler
import com.atiurin.ultron.extensions.withTimeout
import com.atiurin.ultron.testlifecycle.setupteardown.SetUp
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import com.atiurin.ultron.testlifecycle.setupteardown.TearDown
import com.atiurin.ultron.testlifecycle.setupteardown.TearDownRule
import org.junit.Assert
import org.junit.Test

class UltronEspressoConfigTest : UiElementsTest() {
    val page = UiElementsPage

    companion object {
        const val SET_CUSTOM_RESULT_ANALYZER = "SET_CUSTOM_RESULT_ANALYZER"
        const val SET_DEFAULT_CONFIG = "SET_DEFAULT_CONFIG"

        const val SET_CUSTOM_ASSERTIONS_TIMEOUT = "SET_ASSERTIONS_TIMEOUT"
        const val SET_CUSTOM_ACTIONS_TIMEOUT = "SET_ACTIONS_TIMEOUT"
        const val SET_DEFAULT_TIMEOUT = "DROP_DEFAULT_TIMEOUT"
        const val MODIFIED_OPERATIONS_TIMEOUT = 7_000L
    }

    val setUpRule = SetUpRule()
        .add(SET_CUSTOM_RESULT_ANALYZER) {
            UltronConfig.Espresso.setResultAnalyzer {
                Log.debug("SET_CUSTOM_RESULT_ANALYZER ${it.success}")
                if (it.success) throw UltronException("Special reversed analyzer exception on ${it.description}")
                it.success
            }
        }.add(SET_CUSTOM_ASSERTIONS_TIMEOUT) {
            UltronConfig.Espresso.ASSERTION_TIMEOUT =
                MODIFIED_OPERATIONS_TIMEOUT
        }
        .add(SET_CUSTOM_ACTIONS_TIMEOUT) {
            UltronConfig.Espresso.ACTION_TIMEOUT =
                MODIFIED_OPERATIONS_TIMEOUT
        }
    private val tearDownRule = TearDownRule()
        .add(SET_DEFAULT_CONFIG) {
            UltronConfig.Espresso.resultAnalyzer = UltronDefaultOperationResultAnalyzer()
        }
        .add(SET_DEFAULT_TIMEOUT) {
            UltronConfig.Espresso.ACTION_TIMEOUT = UltronConfig.Espresso.DEFAULT_ACTION_TIMEOUT
            UltronConfig.Espresso.ASSERTION_TIMEOUT =
                UltronConfig.Espresso.DEFAULT_ASSERTION_TIMEOUT
        }

    init {
        ruleSequence.add(setUpRule, tearDownRule)
    }

    @Test
    @SetUp(SET_CUSTOM_RESULT_ANALYZER)
    @TearDown(SET_DEFAULT_CONFIG)
    fun resultAnalyzer_reversed_should_throw_on_success_action() {
        AssertUtils.assertException { page.button.click() }
    }

    @Test
    @SetUp(SET_CUSTOM_RESULT_ANALYZER)
    @TearDown(SET_DEFAULT_CONFIG)
    fun resultAnalyzer_reversed_should_NOT_throw_on_failed_action() {
        page.notExistElement.withTimeout(100).click()
    }

    @Test
    @SetUp(SET_CUSTOM_RESULT_ANALYZER)
    @TearDown(SET_DEFAULT_CONFIG)
    fun resultAnalyzer_reversed_should_throw_on_success_assertion() {
        AssertUtils.assertException { page.button.isDisplayed() }
    }

    @Test
    @SetUp(SET_CUSTOM_RESULT_ANALYZER)
    @TearDown(SET_DEFAULT_CONFIG)
    fun resultAnalyzer_reversed_should_NOT_throw_on_failed_assertion() {
        page.notExistElement.withTimeout(100).isDisplayed()
    }

    //timeouts
    @Test
    fun withTimeout_action_default() {
        val default = UltronConfig.Espresso.DEFAULT_ACTION_TIMEOUT
        AssertUtils.assertExecTimeBetween(default, default + 5_000) { page.notExistElement.click() }
    }

    @Test
    fun withTimeou_actiont_customValue() {
        AssertUtils.assertExecTimeBetween(2_000, 4_500) {
            page.notExistElement.withTimeout(2000).click()
        }
    }

    @Test
    @SetUp(SET_CUSTOM_ACTIONS_TIMEOUT)
    @TearDown(
        SET_DEFAULT_TIMEOUT
    )
    fun withTimeout_action_modifiedDefaultValue() {
        AssertUtils.assertExecTimeBetween(
            MODIFIED_OPERATIONS_TIMEOUT, MODIFIED_OPERATIONS_TIMEOUT + 2_000L
        ) { page.notExistElement.click() }
    }

    @Test
    fun withTimeout_assertion_default() {
        val default = UltronConfig.Espresso.DEFAULT_ASSERTION_TIMEOUT
        AssertUtils.assertExecTimeBetween(
            default,
            default + 2_000
        ) { page.notExistElement.isDisplayed() }
    }

    @Test
    fun withTimeout_assertion_customValue() {
        AssertUtils.assertExecTimeBetween(2_000, 4_500) {
            page.notExistElement.withTimeout(2000).isDisplayed()
        }
    }

    @Test
    @SetUp(SET_CUSTOM_ASSERTIONS_TIMEOUT)
    @TearDown(
        SET_DEFAULT_TIMEOUT
    )
    fun withTimeout_assertion_modifiedDefaultValue() {
        AssertUtils.assertExecTimeBetween(
            MODIFIED_OPERATIONS_TIMEOUT,
            MODIFIED_OPERATIONS_TIMEOUT + 2_000L
        ) { page.notExistElement.isDisplayed() }
    }

    //resultHandler
    @Test
    fun withResultHandler_action_default_true() {
        var result: EspressoOperationResult<UltronEspressoOperation>? = null
        page.button.withResultHandler {
            result = it
        }.click()
        Assert.assertNotNull(result)
        Assert.assertTrue(result!!.success)
//        Assert.assertTrue(result!!.exceptions.isEmpty())
        Assert.assertFalse(result!!.operation.name.isNullOrEmpty())
        Assert.assertFalse(result!!.operation.description.isNullOrEmpty())
        Assert.assertEquals(UltronConfig.Espresso.ACTION_TIMEOUT, result!!.operation.timeoutMs)
    }

    @Test
    fun withResultHandler_action_default_false() {
        var result: EspressoOperationResult<UltronEspressoOperation>? = null
        page.notExistElement.withTimeout(100).withResultHandler {
            result = it
        }.click()
        Assert.assertNotNull(result)
        Assert.assertFalse(result!!.success)
        Assert.assertTrue(result!!.exceptions.isNotEmpty())
        Assert.assertFalse(result!!.operation.name.isNullOrEmpty())
        Assert.assertFalse(result!!.operation.description.isNullOrEmpty())
        Assert.assertEquals(100, result!!.operation.timeoutMs)
    }
}