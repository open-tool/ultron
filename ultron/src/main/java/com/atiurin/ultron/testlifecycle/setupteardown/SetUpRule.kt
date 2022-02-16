package com.atiurin.ultron.testlifecycle.setupteardown

import org.junit.runner.Description

open class SetUpRule(override val name: String = "") : ConditionRule(name) {
    override fun starting(description: Description) {
        val keys = mutableListOf<String>().apply { this.addAll(commonConditionKeys) }
        val method = description.testClass.getMethod(description.methodName)
        if (method.isAnnotationPresent(SetUp::class.java)) {
            val setUpAnnotation = method.getAnnotation(SetUp::class.java)
            if (setUpAnnotation != null) {
                keys.addAll(setUpAnnotation.value.toList()) //get the list of keys in annotation SetUp
            }
            conditionsExecutor.before(name, this::class)
            conditionsExecutor.execute(conditions, keys, name)
        } else {
            conditionsExecutor.before(name, this::class)
            conditionsExecutor.execute(conditions, commonConditionKeys, name)
        }
        super.starting(description)
    }
}