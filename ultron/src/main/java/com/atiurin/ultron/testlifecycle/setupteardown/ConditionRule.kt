package com.atiurin.ultron.testlifecycle.setupteardown

import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.util.concurrent.atomic.AtomicInteger

/**
 * Class to execute setup and teardown methods before and after @Test
 *
 * How it works:
 * The execution order of all lambdas depends on it addition order
 * If lambda added without key it is executed for all @Tests in class
 * Supported the unlimited number of lambdas without key
 */
abstract class ConditionRule : TestWatcher() {
    companion object {
        private const val COMMON_CONDITION_KEY = "COMMON_CONDITION_KEY"
    }

    private val commonConditionCounter = AtomicInteger(0)
    internal val commonConditionKeys = mutableListOf<String>()
    private val conditionCounter = AtomicInteger(0)
    internal val conditions = mutableListOf<Condition>()

    open fun add(
        key: String = getCommonKey(),
        actions: () -> Unit
    ) = apply {
        if (key.contains(COMMON_CONDITION_KEY)) {
            commonConditionKeys.add(key)
        }
        conditions.add(Condition(conditionCounter.getAndIncrement(), key, actions))
    }

    private fun getCommonKey(): String {
        return "${COMMON_CONDITION_KEY}_${commonConditionCounter.getAndIncrement()}"
    }
}