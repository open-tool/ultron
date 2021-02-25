package com.atiurin.ultron.testlifecycle.setupteardown

import android.util.Log
import com.atiurin.ultron.core.config.UltronConfig

class DefaultConditionExecutorWrapper : ConditionExecutorWrapper{
    override fun execute(condition: Condition) {
        Log.d(UltronConfig.LOGCAT_TAG, "Execute condition '${condition.name}' with key '${condition.key}'")
        condition.actions()
    }
}