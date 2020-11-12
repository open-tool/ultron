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
open class SetUpTearDownRule : TestWatcher() {
    companion object {
        private const val COMMON_CONDITION_KEY = "COMMON_CONDITION_KEY"
    }

    private val commonSetUpCounter = AtomicInteger(0)
    private val commonTearDownCounter = AtomicInteger(0)
    private val commonSetUpKeys = mutableListOf<String>()
    private val commonTearDownKeys = mutableListOf<String>()
    private val setUpsCounter = AtomicInteger(0)
    private val tearDownsCounter = AtomicInteger(0)
    private val setUps = mutableListOf<Condition>()
    private val tearDowns = mutableListOf<Condition>()

    open fun addSetUp(
        key: String = getCommonKey(COMMON_CONDITION_KEY, commonSetUpCounter),
        actions: () -> Unit
    ) = apply {
        if (key.contains(COMMON_CONDITION_KEY)) {
            commonSetUpKeys.add(key)
        }
        setUps.add(Condition(setUpsCounter.getAndIncrement(), key, actions))
    }

    open fun addTearDown(
        key: String = getCommonKey(COMMON_CONDITION_KEY, commonTearDownCounter),
        actions: () -> Unit
    ) = apply {
        if (key.contains(COMMON_CONDITION_KEY)) {
            commonTearDownKeys.add(key)
        }
        tearDowns.add(Condition(tearDownsCounter.getAndIncrement(), key, actions))
    }

    override fun starting(description: Description) {
        val keys = mutableListOf<String>().apply { this.addAll(commonSetUpKeys) }
        val method = description.testClass.getMethod(description.methodName)
        if (method.isAnnotationPresent(SetUp::class.java)) {
            val setUpAnnotation = method.getAnnotation(SetUp::class.java)
            if (setUpAnnotation != null) {
                keys.addAll(setUpAnnotation.value.toList()) //get the list of keys in annotation SetUp
            }
            setUps
                .sortedBy { it.counter }
                .filter {
                    it.key in keys
                }
                .forEach { condition ->
                    condition.actions()
                }
        } else {
            setUps.filter { it.key in commonSetUpKeys }.forEach { condition ->
                condition.actions()
            }
        }
        super.starting(description)
    }

    override fun finished(description: Description) {
        val keys = mutableListOf<String>().apply { this.addAll(commonTearDownKeys) }
        val method = description.testClass.getMethod(description.methodName)
        if (method.isAnnotationPresent(TearDown::class.java)) {
            val tearDownAnnotation = method.getAnnotation(TearDown::class.java)
            if (tearDownAnnotation != null) {
                keys.addAll(tearDownAnnotation.value.toList()) //get the list of keys in annotation TearDown
            }
            tearDowns
                .sortedBy { it.counter }
                .filter { it.key in keys }
                .forEach { condition ->
                    condition.actions()
                }
        } else {
            tearDowns.filter { it.key in commonTearDownKeys }.forEach { condition ->
                condition.actions()
            }
        }
        super.finished(description)
    }

    private fun getCommonKey(commonKey: String, counter: AtomicInteger): String {
        return "${commonKey}_${counter.getAndIncrement()}"
    }

    private inner class Condition(val counter: Int, val key: String, val actions: () -> Unit)
}