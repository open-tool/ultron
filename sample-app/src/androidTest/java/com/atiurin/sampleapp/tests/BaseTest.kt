package com.atiurin.sampleapp.tests

import androidx.test.platform.app.InstrumentationRegistry
import com.atiurin.ultron.testlifecycle.rulesequence.RuleSequence
import com.atiurin.sampleapp.data.repositories.CURRENT_USER
import com.atiurin.sampleapp.framework.Log
import com.atiurin.sampleapp.idlingresources.resources.ContactsIdlingResource
import com.atiurin.sampleapp.managers.AccountManager
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import org.junit.BeforeClass
import org.junit.Rule

abstract class BaseTest {

    private val idlingRes = ContactsIdlingResource.getInstanceFromTest()

    val setupRule = SetUpRule()
        .add {
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
        fun speedUpAutomator() {
            UltronConfig.UiAutomator.speedUp()
        }
    }
}