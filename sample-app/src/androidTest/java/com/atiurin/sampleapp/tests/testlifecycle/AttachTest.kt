package com.atiurin.sampleapp.tests.testlifecycle

import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.testlifecycle.rulesequence.RuleSequence
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import org.junit.Rule
import org.junit.Test

class AttachTest: BaseTest() {
    val setUp = SetUpRule().add { throw Exception("test extp") }
    init {
        ruleSequence.add(setUp)
    }
    @Test
    fun demo(){}
}