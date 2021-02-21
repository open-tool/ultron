package com.atiurin.ultron.testlifecycle.setupteardown

import com.atiurin.ultron.core.config.UltronConfig
import org.junit.internal.AssumptionViolatedException
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.MultipleFailureException
import org.junit.runners.model.Statement
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Class to execute setup and teardown methods before and after @Test
 *
 * How it works:
 * The execution order of all lambdas depends on it addition order
 * If lambda added without key it is executed for all @Tests in class
 * Supported the unlimited number of lambdas without key
 */
abstract class ConditionRule(open val name: String) : TestRule {
    companion object {
        private const val COMMON_CONDITION_KEY = "COMMON_CONDITION_KEY"
    }
    private val commonConditionCounter = AtomicInteger(0)
    internal val commonConditionKeys = mutableListOf<String>()
    private val conditionCounter = AtomicInteger(0)
    internal val conditions = mutableListOf<Condition>()

    open var conditionsExecutor: ConditionsExecutor = UltronConfig.Conditions.conditionsExecutor

    open fun add(key: String = getCommonKey(), name: String = "", actions: () -> Unit) = apply {
        if (key.contains(COMMON_CONDITION_KEY)) {
            commonConditionKeys.add(key)
        }
        conditions.add(Condition(conditionCounter.getAndIncrement(), key, name, actions))
    }

    private fun getCommonKey(): String {
        return "${COMMON_CONDITION_KEY}_${commonConditionCounter.getAndIncrement()}"
    }

    override fun apply(base: Statement, description: Description): Statement? {
        return object : Statement() {
            override fun evaluate() {
                val errors: MutableList<Throwable> = ArrayList()
                startingQuietly(description, errors)
                try {
                    if (errors.isEmpty()) {
                        base.evaluate()
                        succeededQuietly(description, errors)
                    }
                } catch (e: AssumptionViolatedException) {
                    errors.add(e)
                    skippedQuietly(e, description, errors)
                } catch (e: Throwable) {
                    errors.add(e)
                    failedQuietly(e, description, errors)
                } finally {
                    finishedQuietly(description, errors)
                }
                MultipleFailureException.assertEmpty(errors)
            }
        }
    }

    open fun succeededQuietly(
        description: Description, errors: MutableList<Throwable>
    ) {
        try {
            succeeded(description)
        } catch (e: Throwable) {
            errors.add(e)
        }
    }

    open fun failedQuietly(
        e: Throwable, description: Description, errors: MutableList<Throwable>
    ) {
        try {
            failed(e, description)
        } catch (e1: Throwable) {
            errors.add(e1)
        }
    }

    open fun skippedQuietly(
        e: AssumptionViolatedException, description: Description, errors: MutableList<Throwable>
    ) {
        try {
            if (e is org.junit.AssumptionViolatedException) {
                skipped(e, description)
            } else {
                skipped(e, description)
            }
        } catch (e1: Throwable) {
            errors.add(e1)
        }
    }

    open fun startingQuietly(
        description: Description, errors: MutableList<Throwable>
    ) {
        try {
            starting(description)
        } catch (e: Throwable) {
            errors.add(e)
        }
    }

    open fun finishedQuietly(
        description: Description, errors: MutableList<Throwable>
    ) {
        try {
            finished(description)
        } catch (e: Throwable) {
            errors.add(e)
        }
    }


    /**
     * Invoked when a test succeeds
     */
    protected open fun succeeded(description: Description) {}

    /**
     * Invoked when a test fails
     */
    protected open fun failed(e: Throwable?, description: Description) {}

    /**
     * Invoked when a test is skipped due to a failed assumption.
     */
    protected open fun skipped(e: org.junit.AssumptionViolatedException, description: Description) {
        // For backwards compatibility with JUnit 4.11 and earlier, call the legacy version
        val asInternalException: AssumptionViolatedException = e
        skipped(asInternalException, description)
    }

    /**
     * Invoked when a test is skipped due to a failed assumption.
     *
     */
    @Deprecated("use {@link #skipped(AssumptionViolatedException, Description)}")
    protected open fun skipped(e: AssumptionViolatedException?, description: Description) {
    }

    /**
     * Invoked when a test is about to start
     */
    open fun starting(description: Description) {}

    /**
     * Invoked when a test method finishes (whether passing or failing)
     */
    open fun finished(description: Description) {}
}