package com.atiurin.ultron.core.test

import com.atiurin.ultron.annotations.ExperimentalUltronApi
import com.atiurin.ultron.core.test.context.DefaultUltronTestContextProvider
import com.atiurin.ultron.core.test.context.UltronTestContextProvider
import com.atiurin.ultron.exceptions.UltronException

/**
 * Base class for tests in the Ultron framework. Provides mechanisms for managing test context,
 * common pre-test and post-test actions, and handling test lifecycle events.
 *
 * @param testContextProvider Provides the context for the test. Defaults to [DefaultUltronTestContextProvider].
 */
open class UltronTest(
    private val testContextProvider: UltronTestContextProvider = DefaultUltronTestContextProvider()
) {
    companion object {
        /**
         * A map to track whether `beforeAllTests` has been executed for a given class name.
         */
        private val beforeAllTestsExecutionMap: MutableMap<String, Boolean> = mutableMapOf()
    }

    /**
     * The fully qualified name of the current test class.
     *
     * @throws UltronException if the test class is anonymous.
     */
    private val className = this::class.qualifiedName
        ?: throw UltronException("Don't use anonymous class for UltronTest")

    /**
     * Function to be executed once before all tests in the class.
     * Can be overridden in subclasses.
     */
    @ExperimentalUltronApi
    open val beforeFirstTests: () -> Unit = {}

    /**
     * Function to be executed before each test.
     * Can be overridden in subclasses.
     */
    open val beforeTest: () -> Unit = {}

    /**
     * Function to be executed after each test.
     * Can be overridden in subclasses.
     */
    open val afterTest: () -> Unit = {}

    /**
     * Executes a test with the provided configuration.
     *
     * @param suppressCommonBefore If `true`, the `beforeTest` function will not be executed. Defaults to `false`.
     * @param suppressCommonAfter If `true`, the `afterTest` function will not be executed. Defaults to `false`.
     * @param configureTestBlock The block of test logic to execute. Implemented as an extension of [TestMethod].
     */
    @OptIn(ExperimentalUltronApi::class)
    fun test(
        suppressCommonBefore: Boolean = false,
        suppressCommonAfter: Boolean = false,
        configureTestBlock: TestMethod.() -> Unit
    ) {
        TestMethod(testContextProvider.provide()).apply {
            // Ensure `beforeAllTests` is executed only once per class
            if (beforeAllTestsExecutionMap[className] != true) {
                beforeFirstTests()
                beforeAllTestsExecutionMap[className] = true
            }

            // Execute common `beforeTest` logic if not suppressed
            if (!suppressCommonBefore) {
                beforeTest()
            }

            // Configure and execute the test block
            configureTestBlock()
            attack()

            // Execute common `afterTest` logic if not suppressed
            if (!suppressCommonAfter) {
                afterTest()
            }
        }
    }
}
