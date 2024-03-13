package com.atiurin.ultron.allure.config

import androidx.test.platform.app.InstrumentationRegistry
import com.atiurin.ultron.allure.attachment.AllureDirectoryUtil
import com.atiurin.ultron.allure.attachment.AttachUtil
import java.io.File

data class AllureConfigParams(
    var addScreenshotPolicy: MutableSet<AllureAttachStrategy> = mutableSetOf(
        AllureAttachStrategy.TEST_FAILURE,
        AllureAttachStrategy.OPERATION_FAILURE
    ),
    var addHierarchyPolicy: MutableSet<AllureAttachStrategy> = mutableSetOf(
        AllureAttachStrategy.TEST_FAILURE,
        AllureAttachStrategy.OPERATION_FAILURE
    ),
    var attachUltronLog: Boolean = true,
    var attachLogcat: Boolean = true,
    var addConditionsToReport: Boolean = true,
    var detailedAllureReport: Boolean = true
)