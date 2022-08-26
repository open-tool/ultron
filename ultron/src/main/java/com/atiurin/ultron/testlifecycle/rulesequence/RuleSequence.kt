package com.atiurin.ultron.testlifecycle.rulesequence

import androidx.compose.ui.test.junit4.ComposeTestRule
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.testlifecycle.setupteardown.RuleSequenceTearDown
import com.atiurin.ultron.testlifecycle.setupteardown.TearDownRule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.concurrent.atomic.AtomicInteger


/**
 * The replacement of standart JUnit4 RuleChain class.
 * It provides 3 lists of rules:
 * 1. [firstRules] - will be executed firstly
 * 2. [rules] - will be executed normally after [firstRules]
 * 3. [lastRules] - will be executed last
 *
 * The rules provided in constructor will be added to normal [rules]
 */
class RuleSequence : TestRule {
    private val firstRules: MutableList<RuleContainer> = mutableListOf()
    private val lastRules: MutableList<RuleContainer> = mutableListOf()
    private val rules: MutableList<RuleContainer> = mutableListOf()

    private val firstRulesCounter = AtomicInteger(0)
    private val rulesCounter = AtomicInteger(0)
    private val lastRulesCounter = AtomicInteger(0)


    constructor(rule: TestRule) {
        this.add(rule)
    }

    constructor(vararg rules: TestRule) {
        rules.forEach { this.add(it) }
    }


    fun addFirst(vararg rules: TestRule) = apply {
        rules.forEach { rule ->
            if (rule is RuleSequenceTearDown) this.lastRules.add(RuleContainer(-firstRulesCounter.getAndIncrement(), rule))
            else this.firstRules.add(RuleContainer(firstRulesCounter.getAndIncrement(), rule))
        }
    }

    fun add(vararg rules: TestRule) = apply {
        rules.forEach { rule ->
            if (rule is RuleSequenceTearDown) this.rules.add(RuleContainer(-rulesCounter.getAndIncrement(), rule))
            else this.rules.add(RuleContainer(rulesCounter.getAndIncrement(), rule))
        }
    }

    fun addLast(vararg rules: TestRule) = apply {
        rules.forEach { rule ->
            if (rule is RuleSequenceTearDown) this.firstRules.add(RuleContainer(-lastRulesCounter.getAndIncrement(), rule))
            else this.lastRules.add(RuleContainer(lastRulesCounter.getAndIncrement(), rule))
        }
    }

    override fun apply(base: Statement?, description: Description?): Statement? {
        var statement = base
        // we do it in such manner because statement is matreshka
        // it works with LIFO principle
        lastRules.sortedByDescending { it.counter }.forEach {
            statement = it.rule.apply(statement, description)
        }
        rules.sortedByDescending { it.counter }.forEach {
            statement = it.rule.apply(statement, description)
        }
        firstRules.sortedByDescending { it.counter }.forEach {
            statement = it.rule.apply(statement, description)
        }
        return statement
    }

    private class RuleContainer(val counter: Int, val rule: TestRule)
}
