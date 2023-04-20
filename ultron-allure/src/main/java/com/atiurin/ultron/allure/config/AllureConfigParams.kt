package com.atiurin.ultron.allure.config

data class AllureConfigParams(
    var addScreenshotPolicy: MutableSet<AllureAttachStrategy> = mutableSetOf(
        AllureAttachStrategy.TEST_FAILURE,
        AllureAttachStrategy.OPERATION_FAILURE
    ),
    var attachUltronLog: Boolean = true,
    var attachLogcat: Boolean = true,
    var addConditionsToReport: Boolean = true,
    var detailedAllureReport: Boolean = true
)