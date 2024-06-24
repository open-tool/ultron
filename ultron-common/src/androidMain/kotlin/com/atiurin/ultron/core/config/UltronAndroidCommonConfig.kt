package com.atiurin.ultron.core.config

import com.atiurin.ultron.testlifecycle.setupteardown.ConditionExecutorWrapper
import com.atiurin.ultron.testlifecycle.setupteardown.ConditionsExecutor
import com.atiurin.ultron.testlifecycle.setupteardown.DefaultConditionExecutorWrapper
import com.atiurin.ultron.testlifecycle.setupteardown.DefaultConditionsExecutor

object UltronAndroidCommonConfig {
    class Conditions {
        companion object {
            var conditionExecutorWrapper: ConditionExecutorWrapper = DefaultConditionExecutorWrapper()
            var conditionsExecutor: ConditionsExecutor = DefaultConditionsExecutor()
        }
    }
}