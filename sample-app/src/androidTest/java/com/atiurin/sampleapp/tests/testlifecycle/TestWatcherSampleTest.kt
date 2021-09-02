package com.atiurin.sampleapp.tests.testlifecycle

import com.atiurin.sampleapp.framework.Log
import com.atiurin.ultron.testlifecycle.rulesequence.RuleSequence
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

class TestWatcherSampleTest {
    @get:Rule
    val ruleChain = RuleSequence()
            .add(MyTestWatcherRule("rule 1"))
            .add(MyTestWatcherRule("rule 2"))
    @Test
    fun someTests(){
        Log.debug("test")
    }
}