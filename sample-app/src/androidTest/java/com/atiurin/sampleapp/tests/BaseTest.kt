package com.atiurin.sampleapp.tests

import android.os.Environment
import androidx.test.platform.app.InstrumentationRegistry
import com.atiurin.sampleapp.data.repositories.CURRENT_USER
import com.atiurin.sampleapp.managers.AccountManager
import com.atiurin.ultron.allure.config.UltronAllureConfig
import com.atiurin.ultron.core.compose.config.UltronComposeConfig
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.testlifecycle.rulesequence.RuleSequence
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import org.junit.BeforeClass
import org.junit.Rule

abstract class BaseTest {
    val setupRule = SetUpRule("Login user rule")
        .add(name = "Login valid user $CURRENT_USER") {
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
            UltronComposeConfig.applyRecommended()
            UltronAllureConfig.applyRecommended()
            UltronAllureConfig.setAllureResultsDirectory(Environment.DIRECTORY_DOWNLOADS)
        }
    }
}
