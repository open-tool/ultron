package com.atiurin.sampleapp.tests

import androidx.test.platform.app.InstrumentationRegistry
import com.atiurin.ultron.testlifecycle.rulesequence.RuleSequence
import com.atiurin.sampleapp.data.repositories.CURRENT_USER
import com.atiurin.sampleapp.framework.Log
import com.atiurin.sampleapp.managers.AccountManager
import com.atiurin.ultron.allure.config.UltronAllureConfig
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import org.junit.BeforeClass
import org.junit.Rule

abstract class BaseTest {
    val setupRule = SetUpRule().add {
            Log.info("Login valid user")
            AccountManager(InstrumentationRegistry.getInstrumentation().targetContext).login(
                CURRENT_USER.login, CURRENT_USER.password
            )
        }

    @get:Rule
    open val ruleSequence = RuleSequence(setupRule)

    companion object {
        @BeforeClass
        @JvmStatic
        fun config() {
            UltronConfig.applyRecommended()
            UltronAllureConfig.applyRecommended()
//            InstrumentationRegistry.getInstrumentation().getRunInformer().addListener(object : UltronRunListener {
//                override fun testFailure(failure: Failure) {
//                    UltronLog.info("Attach log file '${UltronLog.fileLogger.getLogFile().name}' to allure report")
//                    AttachUtil.attachFile(UltronLog.fileLogger.getLogFile(), MimeType.PLAIN_TEXT)
//                }
//            })
        }
    }
}