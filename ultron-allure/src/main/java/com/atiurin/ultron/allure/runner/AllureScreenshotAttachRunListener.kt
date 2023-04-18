package com.atiurin.ultron.allure.runner

import com.atiurin.ultron.allure.config.AllureAttachStrategy
import com.atiurin.ultron.allure.screenshot.AllureScreenshot
import com.atiurin.ultron.extensions.fullTestName
import com.atiurin.ultron.runner.UltronRunListener
import org.junit.runner.notification.Failure

class AllureScreenshotAttachRunListener(val policies: Set<AllureAttachStrategy>) : UltronRunListener() {

    val screenshot = AllureScreenshot()

    override fun testFailure(failure: Failure) {
        if (policies.contains(AllureAttachStrategy.TEST_FAILURE)){
            screenshot.takeAndAttach(failure.description.fullTestName())
        }
    }
}