package com.atiurin.sampleapp.tests.regression

import com.atiurin.ultron.core.config.UltronCommonConfig
import com.atiurin.ultron.core.config.UltronConfig
import org.junit.Assert.assertEquals
import org.junit.Test

class UltronConfigRegressionTest {
    @Test
    fun applyRecommendedDoesNotCreateUiDeviceDuringConfigInitialization() {
        UltronConfig.applyRecommended()

        assertEquals(
            UltronCommonConfig.Defaults.OPERATION_TIMEOUT_MS,
            UltronConfig.UiAutomator.OPERATION_TIMEOUT
        )
    }
}
