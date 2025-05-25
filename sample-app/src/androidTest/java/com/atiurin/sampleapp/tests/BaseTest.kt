package com.atiurin.sampleapp.tests

import android.os.Environment
import androidx.test.platform.app.InstrumentationRegistry
import com.atiurin.sampleapp.data.repositories.CURRENT_USER
import com.atiurin.sampleapp.managers.AccountManager
import com.atiurin.ultron.allure.config.UltronAllureConfig
import com.atiurin.ultron.core.compose.config.UltronComposeConfig
import com.atiurin.ultron.core.compose.listeners.ComposDebugListener
import com.atiurin.ultron.core.config.UltronCommonConfig
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.recyclerview.UltronRecyclerViewImpl
import com.atiurin.ultron.core.test.UltronTest
import com.atiurin.ultron.testlifecycle.rulesequence.RuleSequence
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import org.junit.BeforeClass
import org.junit.Rule

abstract class BaseTest : UltronTest(){
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
            UltronConfig.apply {
                recyclerViewImpl = UltronRecyclerViewImpl.PERFORMANCE
            }
            UltronComposeConfig.applyRecommended()
            UltronCommonConfig.addListener(ComposDebugListener())
            UltronAllureConfig.applyRecommended()
            UltronAllureConfig.setAllureResultsDirectory(Environment.DIRECTORY_DOWNLOADS)
        }
    }
}
