package com.atiurin.ultron.runner

import org.junit.runner.Description
import org.junit.runner.Result
import org.junit.runner.notification.Failure

interface UltronRunInformer : UltronRunListener {
    val listeners: MutableList<UltronRunListener>
    fun addListener(listener: UltronRunListener)
}

abstract class AbstractRunInformer : UltronRunInformer {
    override fun addListener(listener: UltronRunListener) {
        listeners.add(listener)
    }

    override fun testRunStarted(description: Description) = listeners.forEach { it.testRunStarted(description) }
    override fun testStarted(description: Description) = listeners.forEach { it.testStarted(description) }
    override fun testFinished(description: Description) = listeners.forEach { it.testFinished(description) }
    override fun testFailure(failure: Failure) = listeners.forEach { it.testFailure(failure) }
    override fun testAssumptionFailure(failure: Failure) = listeners.forEach { it.testAssumptionFailure(failure) }
    override fun testIgnored(description: Description) = listeners.forEach { it.testIgnored(description) }
    override fun testRunFinished(result: Result) = listeners.forEach { it.testRunFinished(result) }
}