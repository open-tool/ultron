package com.atiurin.ultron.allure.condition

import com.atiurin.ultron.allure.step.step
import com.atiurin.ultron.testlifecycle.setupteardown.Condition
import com.atiurin.ultron.testlifecycle.setupteardown.DefaultConditionsExecutor

class AllureConditionsExecutor : DefaultConditionsExecutor() {
    override fun execute(conditions: List<Condition>, keys: List<String>, description: String) {
        val stepName = description.ifEmpty { "Conditions" }
        step(stepName) { super.execute(conditions, keys, description) }
    }
}