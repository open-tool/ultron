package com.atiurin.ultron.testlifecycle.setupteardown

import kotlin.reflect.KClass

interface ConditionsExecutor {
    val conditionExecutor: ConditionExecutorWrapper
    fun before(name: String, ruleClass: KClass<*>)
    fun execute(conditions: List<Condition>, keys: List<String>, description: String = "")
}