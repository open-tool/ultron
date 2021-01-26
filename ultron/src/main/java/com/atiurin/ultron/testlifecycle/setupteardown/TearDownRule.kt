package com.atiurin.ultron.testlifecycle.setupteardown

import org.junit.runner.Description

class TearDownRule : ConditionRule() {
    override fun finished(description: Description) {
        val keys = mutableListOf<String>().apply { this.addAll(commonConditionKeys) }
        val method = description.testClass.getMethod(description.methodName)
        if (method.isAnnotationPresent(TearDown::class.java)) {
            val tearDownAnnotation = method.getAnnotation(TearDown::class.java)
            if (tearDownAnnotation != null) {
                keys.addAll(tearDownAnnotation.value.toList()) //get the list of keys in annotation TearDown
            }
            conditions
                .sortedBy { it.counter }
                .filter { it.key in keys }
                .forEach { condition ->
                    condition.actions()
                }
        } else {
            conditions.filter { it.key in commonConditionKeys }.forEach { condition ->
                condition.actions()
            }
        }
        super.finished(description)
    }
}