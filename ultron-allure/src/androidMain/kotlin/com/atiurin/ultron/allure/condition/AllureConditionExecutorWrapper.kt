package com.atiurin.ultron.allure.condition

import com.atiurin.ultron.allure.step.step
import com.atiurin.ultron.testlifecycle.setupteardown.Condition
import com.atiurin.ultron.testlifecycle.setupteardown.ConditionExecutorWrapper

class AllureConditionExecutorWrapper : ConditionExecutorWrapper {
    override fun execute(condition: Condition) {
        val stepName = condition.name.ifEmpty {
            "${condition.counter} - ${condition.key}"
        }
        step(stepName) { condition.actions() }
    }
}