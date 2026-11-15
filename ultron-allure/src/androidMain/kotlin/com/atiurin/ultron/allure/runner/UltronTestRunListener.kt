package com.atiurin.ultron.allure.runner

import androidx.test.platform.app.InstrumentationRegistry
import com.atiurin.ultron.allure.getRunInformer
import org.junit.runner.Description
import org.junit.runner.Result
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunListener

class UltronTestRunListener : RunListener() {
    private val informer = InstrumentationRegistry.getInstrumentation().getRunInformer()

    override fun testRunStarted(description: Description) = informer.testRunStarted(description)
    override fun testStarted(description: Description) = informer.testStarted(description)
    override fun testFinished(description: Description) = informer.testFinished(description)
    override fun testFailure(failure: Failure) = informer.testFailure(failure)
    override fun testAssumptionFailure(failure: Failure) = informer.testAssumptionFailure(failure)
    override fun testIgnored(description: Description) = informer.testIgnored(description)
    override fun testRunFinished(result: Result) = informer.testRunFinished(result)
}

