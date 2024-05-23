package com.atiurin.ultron.runner

import org.junit.runner.Description
import org.junit.runner.Result
import org.junit.runner.notification.Failure

interface RunListener {
    /**
     * Called before any tests have been run. This may be called on an
     * arbitrary thread.
     *
     * @param description describes the tests to be run
     */
    fun testRunStarted(description: Description) = Unit

    /**
     * Called when all tests have finished. This may be called on an
     * arbitrary thread.
     *
     * @param result the summary of the test run, including all the tests that failed
     */
    fun testRunFinished(result: Result) = Unit

    /**
     * Called when a test suite is about to be started. If this method is
     * called for a given [Description], then [.testSuiteFinished]
     * will also be called for the same `Description`.
     *
     *
     * Note that not all runners will call this method, so runners should
     * be prepared to handle [.testStarted] calls for tests
     * where there was no corresponding `testSuiteStarted()` call for
     * the parent `Description`.
     *
     * @param description the description of the test suite that is about to be run
     * (generally a class name)
     */
    fun testSuiteStarted(description: Description) = Unit

    /**
     * Called when a test suite has finished, whether the test suite succeeds or fails.
     * This method will not be called for a given [Description] unless
     * [.testSuiteStarted] was called for the same @code Description}.
     *
     * @param description the description of the test suite that just ran
     */
    fun testSuiteFinished(description: Description) = Unit

    /**
     * Called when an atomic test is about to be started.
     *
     * @param description the description of the test that is about to be run
     * (generally a class and method name)
     */
    fun testStarted(description: Description) = Unit

    /**
     * Called when an atomic test has finished, whether the test succeeds or fails.
     *
     * @param description the description of the test that just ran
     */
    fun testFinished(description: Description) = Unit

    /**
     * Called when an atomic test fails, or when a listener throws an exception.
     *
     *
     * In the case of a failure of an atomic test, this method will be called
     * with the same `Description` passed to
     * [.testStarted], from the same thread that called
     * [.testStarted].
     *
     *
     * In the case of a listener throwing an exception, this will be called with
     * a `Description` of [Description.TEST_MECHANISM], and may be called
     * on an arbitrary thread.
     *
     * @param failure describes the test that failed and the exception that was thrown
     */
    fun testFailure(failure: Failure) = Unit

    /**
     * Called when an atomic test flags that it assumes a condition that is
     * false
     *
     * @param failure describes the test that failed and the
     * [org.junit.AssumptionViolatedException] that was thrown
     */
    fun testAssumptionFailure(failure: Failure) = Unit

    /**
     * Called when a test will not be run, generally because a test method is annotated
     * with [org.junit.Ignore].
     *
     * @param description describes the test that will not be run
     */
    fun testIgnored(description: Description) = Unit
}