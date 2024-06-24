package com.atiurin.ultron.testlifecycle.setupteardown

import com.atiurin.ultron.log.UltronLog

class DefaultConditionExecutorWrapper : ConditionExecutorWrapper {
    override fun execute(condition: Condition) {
        UltronLog.info("Execute condition '${condition.name}' with key '${condition.key}'")
        condition.actions()
    }
}