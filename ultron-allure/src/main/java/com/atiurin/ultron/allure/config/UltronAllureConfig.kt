package com.atiurin.ultron.allure.config

import androidx.test.platform.app.InstrumentationRegistry
import com.atiurin.ultron.allure.condition.AllureConditionExecutorWrapper
import com.atiurin.ultron.allure.condition.AllureConditionsExecutor
import com.atiurin.ultron.allure.getRunInformer
import com.atiurin.ultron.allure.listeners.DetailedOperationAllureListener
import com.atiurin.ultron.allure.listeners.ScreenshotAttachListener
import com.atiurin.ultron.allure.runner.AllureScreenshotAttachRunListener
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.runner.UltronRunListener

object UltronAllureConfig {
    private var params: AllureConfigParams = AllureConfigParams()
    fun getParams() = params

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
            addRunListener(AllureScreenshotAttachRunListener(params.addScreenshotPolicy))
        }
        if (params.addConditionsToReport) {
            setAllureConditionsExecutorWrapper()
            setAllureConditionExecutor()
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
}

data class AllureConfigParams(
    var addScreenshotPolicy: MutableSet<AllureAttachStrategy> = mutableSetOf(
        AllureAttachStrategy.TEST_FAILURE,
        AllureAttachStrategy.OPERATION_FAILURE
    ),
    var attachUltronLog: Boolean = true,
    var addConditionsToReport: Boolean = true,
    var detailedAllureReport: Boolean = true
)

enum class AllureAttachStrategy {
    TEST_FAILURE,
    OPERATION_FAILURE, // attach artifact for failed operation
    OPERATION_SUCCESS, // attach artifact for each succeeded operation
    OPERATION_FINISH, // attach artifact for each operation
    NONE
}