package com.atiurin.ultron.allure

import android.app.Instrumentation
import android.os.Bundle
import com.atiurin.ultron.allure.runner.UltronAllureRunInformer
import com.atiurin.ultron.allure.runner.UltronTestRunListener
import com.atiurin.ultron.allure.step.step
import com.atiurin.ultron.extensions.putArguments
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.runner.UltronRunInformer
import io.qameta.allure.android.runners.AllureAndroidJUnitRunner

open class UltronAllureTestRunner : AllureAndroidJUnitRunner() {
    val informer: UltronRunInformer = UltronAllureRunInformer()

    override fun onCreate(arguments: Bundle) {
        arguments.putArguments("listener", UltronTestRunListener::class.qualifiedName!!)
        super.onCreate(arguments)
    }

    override fun onException(obj: Any?, e: Throwable?): Boolean {
        step("Mock step"){
            UltronLog.error("Exception handled: ${e?.message}")
        }
        return super.onException(obj, e)
    }
}

fun Instrumentation.getRunInformer(): UltronRunInformer {
    return requireNotNull((this as? UltronAllureTestRunner)?.informer) {
        "Set testInstrumentationRunner = ${UltronAllureTestRunner::class.qualifiedName}"
    }
}