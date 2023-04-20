package com.atiurin.ultron.allure.config

import androidx.test.platform.app.InstrumentationRegistry
import com.atiurin.ultron.allure.condition.AllureConditionExecutorWrapper
import com.atiurin.ultron.allure.condition.AllureConditionsExecutor
import com.atiurin.ultron.allure.getRunInformer
import com.atiurin.ultron.allure.listeners.DetailedOperationAllureListener
import com.atiurin.ultron.allure.listeners.ScreenshotAttachListener
import com.atiurin.ultron.allure.runner.LogcatAttachRunListener
import com.atiurin.ultron.allure.runner.ScreenshotAttachRunListener
import com.atiurin.ultron.allure.runner.UltronLogAttachRunListener
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.listeners.AbstractListener
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.runner.UltronRunListener

object UltronAllureConfig {
    var params: AllureConfigParams = AllureConfigParams()

    fun setAllureConditionExecutor() {
        UltronConfig.Conditions.conditionsExecutor = AllureConditionsExecutor()
    }

    fun setAllureConditionsExecutorWrapper() {
        UltronConfig.Conditions.conditionExecutorWrapper = AllureConditionExecutorWrapper()
    }

    private fun modify() {
        if (params.detailedAllureReport) {
            UltronConfig.addGlobalListener(DetailedOperationAllureListener())
        }
        if (!params.addScreenshotPolicy.contains(AllureAttachStrategy.NONE)) {
            UltronConfig.addGlobalListener(ScreenshotAttachListener(params.addScreenshotPolicy))
            addRunListener(ScreenshotAttachRunListener(params.addScreenshotPolicy))
        }
        if (params.addConditionsToReport) {
            setAllureConditionsExecutorWrapper()
            setAllureConditionExecutor()
        }
        if (!params.attachUltronLog){
            removeRunListener(UltronLogAttachRunListener::class.java)
        }
        if (!params.attachLogcat){
            removeRunListener(LogcatAttachRunListener::class.java)
        }
        UltronLog.info("UltronAllureConfig applied with params $params")
    }

    fun applyRecommended() {
        params = AllureConfigParams()
        modify()
    }

    fun apply(block: AllureConfigParams.() -> Unit) {
        params.block()
        modify()
    }

    fun addRunListener(listener: UltronRunListener) {
        InstrumentationRegistry.getInstrumentation().getRunInformer().addListener(listener)
    }

    fun <T: AbstractListener> removeRunListener(listenerClass: Class<T>) {
        InstrumentationRegistry.getInstrumentation().getRunInformer().removeListener(listenerClass)
    }
}
