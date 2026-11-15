package com.atiurin.ultron.core.test

import com.atiurin.ultron.annotations.ExperimentalUltronApi
import com.atiurin.ultron.annotations.InternalUltronApi
import com.atiurin.ultron.core.test.context.DefaultUltronTestContextProvider
import com.atiurin.ultron.core.test.context.UltronTestContextProvider
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.utils.qualifiedClassName

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
     * The simple name of the current test class.
     *
     * @throws UltronException if the test class is anonymous.
     */
    private val className = this::class.simpleName
        ?: throw UltronException("Don't use anonymous class for UltronTest")

    /**
     * The qualified name of the test class where the platform provides one (JVM, Native); the simple name on
     * js/wasmJs, whose reflection has no qualified names. This is what [UltronTestListener]s receive, so that
     * same-named classes in different packages stay apart in reports.
     */
    private val listenerClassName: String = qualifiedClassName(this) ?: className

    /**
     * Function to be executed once before all tests in the class.
     * Can be overridden in subclasses.
     */
    @ExperimentalUltronApi
    open val beforeFirstTest: () -> Unit = {}

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
     * A failure of the common `beforeTest` is the test's failure: the test blocks are skipped, `afterTest` still
     * runs (unless suppressed) and the `beforeTest` throwable is rethrown. An exception thrown by `afterTest` is
     * rethrown only when nothing failed before it; otherwise it is dropped in favour of the original one.
     *
     * @param suppressCommonBefore If `true`, the `beforeTest` function will not be executed. Defaults to `false`.
     * @param suppressCommonAfter If `true`, the `afterTest` function will not be executed. Defaults to `false`.
     * @param name Optional human-readable name of the test. Reporters receive it through
     * [UltronTestListener.beforeTest]; the Allure module uses it as the test name in reports. Defaults to `null`,
     * in which case reporters fall back to their own discovery (for example the JVM test method name).
     * Pass it by name: `test(name = "login") { }`. From Java use the four-argument form
     * `test(false, false, "login", block)`.
     * @param configureTestBlock The block of test logic to execute. Implemented as an extension of [TestMethod].
     * @return `Unit` on JVM, Android and Native; on js/wasmJs a promise settled when the test has finished. Return it
     * from the test function, `@Test fun login() = test { go { runUltronUiTest { } } }`: a `runUltronUiTest` in a
     * block runs after `test` has returned there, and the test waits for it before its next block, `afterTest` and
     * the listeners' `afterTest`. Its failure fails the test like any other failure of the block.
     */
    @OptIn(ExperimentalUltronApi::class, InternalUltronApi::class)
    fun test(
        suppressCommonBefore: Boolean = false,
        suppressCommonAfter: Boolean = false,
        name: String? = null,
        configureTestBlock: TestMethod.() -> Unit
    ): UltronTestResult {
        val method = TestMethod(testContextProvider.provide())
        // Ensure `beforeAllTests` is executed only once per class
        if (beforeAllTestsExecutionMap[listenerClassName] != true) {
            beforeFirstTest()
            beforeAllTestsExecutionMap[listenerClassName] = true
        }
        val started = UltronTestListeners.fireBeforeTest(listenerClassName, name)
        method.activeTest = started
        // Every step below may start asynchronous work (a `runUltronUiTest` on js/wasmJs, see UltronTestAsync): the
        // next step runs once it has settled. Without any, the whole flow runs synchronously inside this call.
        return UltronTestAsync.result { finish ->
            // Execute common `afterTest` logic if not suppressed, then tell the listeners the test is closed.
            val closeTest = { failure: Throwable? ->
                UltronTestAsync.step({ if (!suppressCommonAfter) afterTest() }) { afterFailure ->
                    val throwable = failure ?: afterFailure
                    UltronTestAsync.step({ UltronTestListeners.fireAfterTest(started, throwable) }) { finish(throwable) }
                }
            }
            // Execute common `beforeTest` logic if not suppressed. A failure here is the test's failure:
            // the blocks are skipped, afterTest still runs and listeners see the test closed with it.
            UltronTestAsync.step({ if (!suppressCommonBefore) beforeTest() }) { beforeFailure ->
                if (beforeFailure != null) {
                    closeTest(beforeFailure)
                } else {
                    // Configure and execute the test block
                    UltronTestAsync.step({ method.configureTestBlock() }) { configureFailure ->
                        if (configureFailure != null) closeTest(configureFailure) else method.attack(closeTest)
                    }
                }
            }
        }
    }

    /**
     * Binary compatibility: the signature `test` had before `name` was added, kept so that code compiled against an
     * older `ultron-common` links without a rebuild. Hidden from source: Kotlin calls with a trailing lambda,
     * `test { }`, `test(true) { }` and `test(true, false) { }` included, resolve to the four-parameter `test`. A lambda
     * passed as the third positional argument inside the parentheses, `test(false, false, { })`, does not compile
     * any more (that position is `name` now): write it as a trailing lambda or name it, `configureTestBlock = { }`.
     * Java callers use `test(false, false, null, block)`.
     */
    @Deprecated("Use test(suppressCommonBefore, suppressCommonAfter, name, configureTestBlock)", level = DeprecationLevel.HIDDEN)
    @OptIn(ExperimentalUltronApi::class)
    fun test(
        suppressCommonBefore: Boolean = false,
        suppressCommonAfter: Boolean = false,
        configureTestBlock: TestMethod.() -> Unit
    ) = test(suppressCommonBefore, suppressCommonAfter, null, configureTestBlock)
}
