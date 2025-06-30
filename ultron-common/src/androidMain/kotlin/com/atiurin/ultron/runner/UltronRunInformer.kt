package com.atiurin.ultron.runner

import com.atiurin.ultron.listeners.AbstractListenersContainer
import org.junit.runner.Description
import org.junit.runner.Result
import org.junit.runner.notification.Failure

abstract class UltronRunInformer : AbstractListenersContainer<UltronRunListener>(), RunListener {
    override fun testRunStarted(description: Description) = getListeners().forEach { it.testRunStarted(description) }
    override fun testStarted(description: Description) = getListeners().forEach { it.testStarted(description) }
    override fun testFinished(description: Description) = getListeners().forEach { it.testFinished(description) }
    override fun testFailure(failure: Failure) {
        getListeners().forEach {
            it.testFailure(failure)
        }
    }
    override fun testAssumptionFailure(failure: Failure) = getListeners().forEach { it.testAssumptionFailure(failure) }
    override fun testIgnored(description: Description) = getListeners().forEach { it.testIgnored(description) }
    override fun testRunFinished(result: Result) = getListeners().forEach { it.testRunFinished(result) }
}