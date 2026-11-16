package com.atiurin.ultron.core.common

import com.atiurin.ultron.core.common.assertion.EmptyOperationAssertion
import com.atiurin.ultron.core.common.assertion.OperationAssertion
import com.atiurin.ultron.core.config.UltronCommonConfig
import com.atiurin.ultron.listeners.UltronLifecycleListener
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AbstractOperationLifecycleTest {
    private enum class Type : UltronOperationType { OP }

    private val operation = object : Operation {
        override val name = "op"
        override val description = "op"
        override val type: UltronOperationType = Type.OP
        override val timeoutMs = 1L
        override val assertion: OperationAssertion = EmptyOperationAssertion()
        override val elementInfo: ElementInfo = object : ElementInfo {
            override var name = "el"
            override var meta: Any? = null
            override fun copy(): ElementInfo = this
        }
        override fun execute(): OperationIterationResult = throw UnsupportedOperationException()
    }

    private val lifecycle = object : AbstractOperationLifecycle() {}

    @AfterTest
    fun cleanup() = UltronCommonConfig.clearListeners()

    @Test
    fun `a listener that throws between retries does not abort the operation nor the other listeners`() {
        val calls = mutableListOf<String>()
        UltronCommonConfig.addListener(object : UltronLifecycleListener() {
            init { id = "throwing" }
            override fun betweenRetries(operation: Operation) { calls += "throwing"; throw IllegalStateException("boom") }
        })
        UltronCommonConfig.addListener(object : UltronLifecycleListener() {
            init { id = "quiet" }
            override fun betweenRetries(operation: Operation) { calls += "quiet" }
        })

        lifecycle.notifyBetweenRetries(operation)

        assertEquals(listOf("throwing", "quiet"), calls)
    }
}
