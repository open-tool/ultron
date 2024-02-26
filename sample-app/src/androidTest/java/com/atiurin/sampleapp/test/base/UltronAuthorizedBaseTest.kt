package com.atiurin.sampleapp.test.base

import androidx.test.platform.app.InstrumentationRegistry
import com.atiurin.sampleapp.managers.AccountManager
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule

open class UltronAuthorisedBaseTest : UltronBaseTest(){
    private val backgroundLoginRule = SetUpRule().add {
        AccountManager(InstrumentationRegistry.getInstrumentation().targetContext)
            .login("joey", "1234")
    }

    init {
        ruleSequence.add(backgroundLoginRule)
    }
}