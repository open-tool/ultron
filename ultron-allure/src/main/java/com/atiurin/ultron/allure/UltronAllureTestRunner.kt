package com.atiurin.ultron.allure

import android.app.Instrumentation
import android.os.Bundle
import com.atiurin.ultron.allure.runner.UltronAllureRunInformer
import com.atiurin.ultron.allure.runner.UltronTestRunListener
import com.atiurin.ultron.extensions.putArguments
import com.atiurin.ultron.runner.UltronRunInformer
import io.qameta.allure.android.runners.AllureAndroidJUnitRunner

open class UltronAllureTestRunner : AllureAndroidJUnitRunner() {
    val informer: UltronRunInformer = UltronAllureRunInformer()

    override fun onCreate(arguments: Bundle) {
        arguments.putArguments("listener", UltronTestRunListener::class.qualifiedName!!)
        super.onCreate(arguments)
    }
}

fun Instrumentation.getRunInformer() : UltronRunInformer {
    return requireNotNull((this as? UltronAllureTestRunner)?.informer) {
        "Set testInstrumentationRunner = ${UltronAllureTestRunner::class.qualifiedName}"
    }
}