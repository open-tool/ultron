package com.atiurin.ultron.testlifecycle.setupteardown

import com.atiurin.ultron.core.config.UltronAndroidCommonConfig
import com.atiurin.ultron.log.UltronLog
import kotlin.reflect.KClass

open class DefaultConditionsExecutor : ConditionsExecutor {
    override val conditionExecutor: ConditionExecutorWrapper by lazy { UltronAndroidCommonConfig.Conditions.conditionExecutorWrapper }
    override fun before(name: String, ruleClass: KClass<*>) {
        UltronLog.info("Execute ${ruleClass.simpleName} '$name' conditions")
    }
    override fun execute(conditions: List<Condition>, keys: List<String>, description: String) {
        conditions
            .sortedBy { it.counter }
            .filter { it.key in keys }
            .forEach { condition ->
                conditionExecutor.execute(condition)
            }
    }
}