package com.atiurin.ultron.allure.attachment

import androidx.test.platform.app.InstrumentationRegistry
import io.qameta.allure.kotlin.util.PropertiesUtils
import java.io.File

object AllureDirectoryUtil {

    fun getResultsDirectoryName(): String = PropertiesUtils.resultsDirectoryPath

    /**
     * From Allure source code
     * see [https://github.com/allure-framework/allure-kotlin/blob/master/allure-kotlin-android/src/main/kotlin/io/qameta/allure/android/AllureAndroidLifecycle.kt]
     */
    fun getOriginalResultsDirectory(): File {
        return File(InstrumentationRegistry.getInstrumentation().targetContext.filesDir, getResultsDirectoryName())
    }
}