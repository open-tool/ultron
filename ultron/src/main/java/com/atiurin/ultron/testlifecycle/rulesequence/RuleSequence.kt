package com.atiurin.ultron.testlifecycle.rulesequence

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

    constructor(rules: List<TestRule> = mutableListOf()) {
        this.add(rules)
    }

    constructor(rule: TestRule) {
        this.add(rule)
    }

    constructor(vararg rules: TestRule) {
        this.add(rules.asList())
    }

    fun add(rule: TestRule) = apply {
        this.rules.add(RuleContainer(rulesCounter.getAndIncrement(), rule))
    }

    fun add(vararg rules: TestRule) = apply {
        this.rules.addAll(rules.asList().map { rule ->
            RuleContainer(
                rulesCounter.getAndIncrement(),
                rule
            )
        })
    }

    fun add(rules: List<TestRule>) = apply {
        this.rules.addAll(rules.map { rule -> RuleContainer(rulesCounter.getAndIncrement(), rule) })
    }

    fun addFirst(rule: TestRule) = apply {
        this.firstRules.add(RuleContainer(firstRulesCounter.getAndIncrement(), rule))
    }

    fun addFirst(vararg rules: TestRule) = apply {
        this.firstRules.addAll(rules.asList().map { rule ->
            RuleContainer(
                firstRulesCounter.getAndIncrement(),
                rule
            )
        })
    }

    fun addFirst(rules: List<TestRule>) = apply {
        this.firstRules.addAll(rules.map { rule ->
            RuleContainer(
                firstRulesCounter.getAndIncrement(),
                rule
            )
        })
    }

    fun addLast(rule: TestRule) = apply {
        this.lastRules.add(RuleContainer(lastRulesCounter.getAndIncrement(), rule))
    }

    fun addLast(vararg rules: TestRule) = apply {
        this.lastRules.addAll(rules.asList().map { rule ->
            RuleContainer(
                lastRulesCounter.getAndIncrement(),
                rule
            )
        })
    }

    fun addLast(rules: List<TestRule>) = apply {
        this.lastRules.addAll(rules.map { rule ->
            RuleContainer(
                lastRulesCounter.getAndIncrement(),
                rule
            )
        })
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
