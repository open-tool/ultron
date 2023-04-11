package com.atiurin.ultron.runner

import com.atiurin.ultron.log.LogLevel
import com.atiurin.ultron.log.UltronLog
import org.junit.runner.Description
import org.junit.runner.Result
import org.junit.runner.notification.Failure

class UltronLogRunListener : UltronRunListener {
    override fun testRunStarted(description: Description) {
        logBlockText("TEST RUN STARTED")
    }

    override fun testStarted(description: Description) {
        logBlockText("Test ${description.fullTestName()} STARTED")
    }

    override fun testFinished(description: Description) {
        logBlockText("Test ${description.fullTestName()} FINISHED")
    }

    override fun testFailure(failure: Failure) {
        logBlockText(
            logLevel = LogLevel.E,
            text = """ |Test ${failure.description.fullTestName()} failed with exception:
            |Message: ${failure.exception.message},
            |Cause:  ${failure.exception.cause}
            |Stacktrace: ${failure.exception.stackTrace.joinToString("\n")}
            """.trimMargin()
        )
    }

    override fun testAssumptionFailure(failure: Failure) {
        logBlockText(logLevel = LogLevel.E, text = "Test ${failure.description.fullTestName()} ASSUMPTION FAILURE")
    }

    override fun testIgnored(description: Description) {
        logBlockText("Test ${description.fullTestName()} IGNORED")
    }

    override fun testRunFinished(result: Result) {
        logBlockText(
            """
            |Test run finished ${if (result.wasSuccessful()) "SUCCESSFULLY" else "with FAILURE"}
            |Duration: ${result.runTime} ms
            |Tests count: ${result.runCount}
            |Ignored: ${result.ignoreCount}
            |Failed: ${result.failureCount}
            """.trimMargin()
        )
    }

    private fun logBlockText(text: String, logLevel: LogLevel = LogLevel.I) {
        val delimiter = "============================================================================================================================"
        UltronLog.log(logLevel, delimiter)
        UltronLog.log(logLevel, text)
        UltronLog.log(logLevel, delimiter)
    }

    private fun Description.fullTestName() = "'${this.className}.${this.methodName}'"
}