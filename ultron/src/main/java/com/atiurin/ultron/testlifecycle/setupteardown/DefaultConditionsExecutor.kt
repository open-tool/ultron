package com.atiurin.ultron.testlifecycle.setupteardown

import android.util.Log
import com.atiurin.ultron.core.config.UltronConfig
import kotlin.reflect.KClass

open class DefaultConditionsExecutor : ConditionsExecutor{
    override val conditionExecutor: ConditionExecutorWrapper by lazy { UltronConfig.Conditions.conditionExecutorWrapper }
    override fun before(name: String, ruleClass: KClass<*>) {
        Log.d(UltronConfig.LOGCAT_TAG, "Execute ${ruleClass.simpleName} '$name' conditions")
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