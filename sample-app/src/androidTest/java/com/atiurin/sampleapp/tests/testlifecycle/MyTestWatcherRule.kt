package com.atiurin.sampleapp.tests.testlifecycle

import com.atiurin.sampleapp.framework.Log
import com.atiurin.ultron.testlifecycle.setupteardown.RuleSequenceTearDown
import org.junit.rules.Stopwatch
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MyTestWatcherRule(val name: String): TestWatcher(), RuleSequenceTearDown {
    override fun starting(description: Description?) {
        Log.debug("$name starting")
    }
    override fun finished(description: Description?) {
        Log.debug("$name finished")
    }
}