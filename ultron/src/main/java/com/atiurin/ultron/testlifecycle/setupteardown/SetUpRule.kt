package com.atiurin.ultron.testlifecycle.setupteardown

import org.junit.runner.Description

class SetUpRule : ConditionRule() {
    override fun starting(description: Description) {
        val keys = mutableListOf<String>().apply { this.addAll(commonConditionKeys) }
        val method = description.testClass.getMethod(description.methodName)
        if (method.isAnnotationPresent(SetUp::class.java)) {
            val setUpAnnotation = method.getAnnotation(SetUp::class.java)
            if (setUpAnnotation != null) {
                keys.addAll(setUpAnnotation.value.toList()) //get the list of keys in annotation SetUp
            }
            conditions
                .sortedBy { it.counter }
                .filter {
                    it.key in keys
                }
                .forEach { condition ->
                    condition.actions()
                }
        } else {
            conditions.filter { it.key in commonConditionKeys }.forEach { condition ->
                condition.actions()
            }
        }
        super.starting(description)
    }
}