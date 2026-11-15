package com.atiurin.ultron.allure.attach

import com.atiurin.ultron.allure.config.AllureAttachStrategy
import com.atiurin.ultron.allure.config.allows
import com.atiurin.ultron.allure.lifecycle.AllureLifecycle

/**
 * Attaches artifacts at test level when the test failed and the kind's policy has TEST_FAILURE. Test level
 * even when a manual `step { }` around the test block is still open, hence [AllureLifecycle.attachToTest].
 */
class TestFailureAttacher(
    private val lifecycle: AllureLifecycle,
    private val providers: List<ArtifactProvider>,
    private val screenshotPolicy: Set<AllureAttachStrategy>,
    private val hierarchyPolicy: Set<AllureAttachStrategy>,
) {
    fun onTestFinished(throwable: Throwable?) {
        if (throwable == null) return
        providers.forEach { provider ->
            val policy = if (provider.kind == "screenshot") screenshotPolicy else hierarchyPolicy
            if (!policy.allows(AllureAttachStrategy.TEST_FAILURE)) return@forEach
            val bytes = provider.produce() ?: return@forEach
            lifecycle.attachToTest("test_failure_${provider.kind}", bytes, provider.extension, provider.mimeType)
        }
    }
}
