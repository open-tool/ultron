package com.atiurin.ultron.allure.config

import com.atiurin.ultron.allure.condition.AllureConditionExecutorWrapper
import com.atiurin.ultron.allure.condition.AllureConditionsExecutor
import com.atiurin.ultron.allure.listeners.ScreenshotAttachListener
import com.atiurin.ultron.allure.listeners.OperationAllureStepListener
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.log.UltronLog

object UltronAllureConfig {
    private var params: AllureConfigParams = AllureConfigParams()

    fun setAllureConditionExecutor() {
        UltronConfig.Conditions.conditionsExecutor = AllureConditionsExecutor()
    }

    fun setAllureConditionsExecutorWrapper() {
        UltronConfig.Conditions.conditionExecutorWrapper = AllureConditionExecutorWrapper()
    }

    fun applyRecommended() {
        params = AllureConfigParams()
        apply()
    }

    fun apply() {
        if (params.detailedAllureReport) {
            UltronConfig.addGlobalListener(OperationAllureStepListener())
        }
        if (params.addScreenshotPolicy != AllureAttachStrategy.NONE) {
            UltronConfig.addGlobalListener(ScreenshotAttachListener(params.addScreenshotPolicy))
        }

        if (params.attachUltronLog) {
//            UltronConfig.addGlobalListener()
        }
        if (params.addConditionsToReport) {
            setAllureConditionsExecutorWrapper()
            setAllureConditionExecutor()
        }
        UltronLog.info("UltronComposeConfig applied with params $params")
    }

    fun edit(block: AllureConfigParams.() -> Unit) {
        params.block()
        apply()
    }
}

data class AllureConfigParams(
    var addScreenshotPolicy: AllureAttachStrategy = AllureAttachStrategy.FAILURE,
    var attachUltronLog: Boolean = true,
    var addConditionsToReport: Boolean = true,
    var detailedAllureReport: Boolean = true
)

enum class AllureAttachStrategy {
    FAILURE, // attach artifact for failed operation
    SUCCESS, // attach artifact for each succeeded operation
    FINISH, // attach artifact for each operation
    NONE
}