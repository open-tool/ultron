package com.atiurin.ultron.testlifecycle.setupteardown

import org.junit.runner.Description

open class TearDownRule(override val name: String = "") : ConditionRule(name), RuleSequenceTearDown {
    override fun finished(description: Description) {
        val keys = mutableListOf<String>().apply { this.addAll(commonConditionKeys) }
        val method = description.testClass.getMethod(description.methodName)
        if (method.isAnnotationPresent(TearDown::class.java)) {
            val tearDownAnnotation = method.getAnnotation(TearDown::class.java)
            if (tearDownAnnotation != null) {
                keys.addAll(tearDownAnnotation.value.toList()) //get the list of keys in annotation TearDown
            }
            conditionsExecutor.before(name, this::class)
            conditionsExecutor.execute(conditions, keys, name)
        } else {
            conditionsExecutor.before(name, this::class)
            conditionsExecutor.execute(conditions, commonConditionKeys, name)
        }
        super.finished(description)
    }
}