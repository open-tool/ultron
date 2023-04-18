package com.atiurin.ultron.runner

import com.atiurin.ultron.extensions.fullTestName
import com.atiurin.ultron.log.LogLevel
import com.atiurin.ultron.log.UltronLogUtil.logTextBlock
import org.junit.runner.Description
import org.junit.runner.Result
import org.junit.runner.notification.Failure

class UltronLogRunListener : UltronRunListener() {
    override fun testRunStarted(description: Description) {
        logTextBlock("TEST RUN STARTED")
    }

    override fun testStarted(description: Description) {
        logTextBlock("Test ${description.fullTestName()} STARTED")
    }

    override fun testFinished(description: Description) {
        logTextBlock("Test ${description.fullTestName()} FINISHED")
    }

    override fun testFailure(failure: Failure) {
        logTextBlock(
            logLevel = LogLevel.E,
            text = """ |Test ${failure.description.fullTestName()} failed with exception:
            |Message: ${failure.exception.message},
            |Cause:  ${failure.exception.cause}
            |Stacktrace: ${failure.exception.stackTrace.joinToString("\n")}
            """.trimMargin()
        )
    }

    override fun testAssumptionFailure(failure: Failure) {
        logTextBlock(logLevel = LogLevel.E, text = "Test ${failure.description.fullTestName()} ASSUMPTION FAILURE")
    }

    override fun testIgnored(description: Description) {
        logTextBlock("Test ${description.fullTestName()} IGNORED")
    }

    override fun testRunFinished(result: Result) {
        logTextBlock(
            """
            |Test run finished ${if (result.wasSuccessful()) "SUCCESSFULLY" else "with FAILURE"}
            |Duration: ${result.runTime} ms
            |Tests count: ${result.runCount}
            |Ignored: ${result.ignoreCount}
            |Failed: ${result.failureCount}
            """.trimMargin()
        )
    }
}