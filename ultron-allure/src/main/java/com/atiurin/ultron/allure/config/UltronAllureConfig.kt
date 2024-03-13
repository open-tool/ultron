package com.atiurin.ultron.allure.config

import android.os.Environment
import androidx.test.platform.app.InstrumentationRegistry
import com.atiurin.ultron.allure.attachment.AllureDirectoryUtil
import com.atiurin.ultron.allure.attachment.AttachUtil
import com.atiurin.ultron.allure.condition.AllureConditionExecutorWrapper
import com.atiurin.ultron.allure.condition.AllureConditionsExecutor
import com.atiurin.ultron.allure.getRunInformer
import com.atiurin.ultron.allure.listeners.DetailedOperationAllureListener
import com.atiurin.ultron.allure.listeners.ScreenshotAttachListener
import com.atiurin.ultron.allure.listeners.WindowHierarchyAttachListener
import com.atiurin.ultron.allure.runner.LogcatAttachRunListener
import com.atiurin.ultron.allure.runner.ScreenshotAttachRunListener
import com.atiurin.ultron.allure.runner.UltronAllureResultsTransferListener
import com.atiurin.ultron.allure.runner.UltronLogAttachRunListener
import com.atiurin.ultron.allure.runner.UltronLogCleanerRunListener
import com.atiurin.ultron.allure.runner.WindowHierarchyAttachRunListener
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.extensions.simpleClassName
import com.atiurin.ultron.listeners.AbstractListener
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.runner.UltronRunListener
import java.io.File

object UltronAllureConfig {
    var params: AllureConfigParams = AllureConfigParams()

    fun setAllureConditionExecutor() {
        UltronConfig.Conditions.conditionsExecutor = AllureConditionsExecutor()
    }

    fun setAllureConditionsExecutorWrapper() {
        UltronConfig.Conditions.conditionExecutorWrapper = AllureConditionExecutorWrapper()
    }

    /**
     * Sets the directory where Allure results will be stored.
     * Original results artifacts are saved in [AllureDirectoryUtil.getOriginalResultsDirectory]
     *
     * The results will be copied from [AllureDirectoryUtil.getOriginalResultsDirectory]
     * to [targetDirectory] once test run is finished.
     *
     * @param targetDirectory The directory where the Allure results will be stored.
     *
     * @see AllureDirectoryUtil.getOriginalResultsDirectory
     */
    fun setAllureResultsDirectory(targetDirectory: File) {
        removeRunListener(UltronLogCleanerRunListener::class.java)
        addRunListener(
            UltronAllureResultsTransferListener(
                sourceDir = AllureDirectoryUtil.getOriginalResultsDirectory(),
                targetDir = targetDirectory
            )
        )
        addRunListener(UltronLogCleanerRunListener())
    }

    /**
     * Sets the directory where Allure results will be stored with default public directory.
     *
     * @param publicDirectory The public directory where the Allure results will be stored.
     *                        Default is [Environment.DIRECTORY_DOWNLOADS].
     *
     * So, the final path by default will be like '/sdcard/Download/allure-result'
     */
    fun setAllureResultsDirectory(publicDirectory: String = Environment.DIRECTORY_DOWNLOADS) {
        val targetDir = Environment.getExternalStoragePublicDirectory(publicDirectory).resolve(AllureDirectoryUtil.getResultsDirectoryName())
        setAllureResultsDirectory(targetDir)
    }

    private fun modify() {
        if (params.detailedAllureReport) {
            UltronConfig.addGlobalListener(DetailedOperationAllureListener())
        }
        if (!params.addScreenshotPolicy.contains(AllureAttachStrategy.NONE)) {
            UltronConfig.addGlobalListener(ScreenshotAttachListener(params.addScreenshotPolicy))
            addRunListener(ScreenshotAttachRunListener(params.addScreenshotPolicy))
        }
        if (!params.addHierarchyPolicy.contains(AllureAttachStrategy.NONE)) {
            UltronConfig.addGlobalListener(WindowHierarchyAttachListener(params.addHierarchyPolicy))
            addRunListener(WindowHierarchyAttachRunListener(params.addHierarchyPolicy))
        }
        if (params.addConditionsToReport) {
            setAllureConditionsExecutorWrapper()
            setAllureConditionExecutor()
        }
        if (!params.attachUltronLog) {
            removeRunListener(UltronLogAttachRunListener::class.java)
        }
        if (!params.attachLogcat) {
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
        UltronLog.info("Add ${listener::class.simpleName} run listener")
        InstrumentationRegistry.getInstrumentation().getRunInformer().addListener(listener)
    }

    fun <T : AbstractListener> removeRunListener(listenerClass: Class<T>) {
        UltronLog.info("Remove ${listenerClass.simpleClassName()} run listener")
        InstrumentationRegistry.getInstrumentation().getRunInformer().removeListener(listenerClass)
    }
}
