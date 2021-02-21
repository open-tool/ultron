package com.atiurin.sampleapp.tests.testlifecycle

import com.atiurin.sampleapp.framework.Log
import com.atiurin.ultron.testlifecycle.rulesequence.RuleSequence
import com.atiurin.ultron.testlifecycle.setupteardown.SetUp
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import com.atiurin.ultron.testlifecycle.setupteardown.TearDown
import com.atiurin.ultron.testlifecycle.setupteardown.TearDownRule
import org.junit.*
import org.junit.rules.ExpectedException
import java.lang.RuntimeException
import java.util.concurrent.atomic.AtomicInteger

class ExceptionsProcessingTest {
    val counter = AtomicInteger(0)
    val conditionsOrderMap = mutableMapOf<Int, String>()

    companion object {
        const val setUp1Key = "setUp1"
        const val setUp2Key = "setUp2"
        const val testKey = "testKey"
        const val tearDown1Key = "tearDown1"
        const val tearDown2Key = "tearDown2"

        const val NO_EXCEPTION_FLOW = "NO_EXCEPTION_FLOW"
        const val SET_UP_EXCEPTION_FLOW = "SET_UP_EXCEPTION_FLOW"
        const val TEST_EXCEPTION_FLOW = "TEST_EXCEPTION_FLOW"
        const val TEAR_DOWN_EXCEPTION_FLOW = "TEAR_DOWN_EXCEPTION_FLOW"

        const val SET_UP_WITH_EXCEPTION = "SET_UP_WITH_EXCEPTION"
        const val TEAR_DOWN_WITH_EXCEPTION = "TEAR_DOWN_WITH_EXCEPTION"
    }

    val setUp1 = SetUpRule("setUpRule1").add(name = "setUp1") {
        conditionsOrderMap[counter.incrementAndGet()] = setUp1Key
    }.add(key = SET_UP_WITH_EXCEPTION) {
        throw SetUpException(SET_UP_WITH_EXCEPTION)
    }
    val tearDown1 = TearDownRule("tearDown1").add(name = tearDown1Key) {
        conditionsOrderMap[counter.incrementAndGet()] = tearDown1Key
    }.add(key = TEAR_DOWN_WITH_EXCEPTION) {
        throw TearDownException(TEAR_DOWN_WITH_EXCEPTION)
    }

    val setUp2 = SetUpRule("setUp2").add(name = setUp2Key) {
        conditionsOrderMap[counter.incrementAndGet()] = setUp2Key
    }
    val tearDown2 = TearDownRule("tearDown2").add(name = tearDown2Key) {
        conditionsOrderMap[counter.incrementAndGet()] = tearDown2Key
    }

    val controlTearDown = TearDownRule().add(key = NO_EXCEPTION_FLOW) {
        var index = 1
        Log.info(conditionsOrderMap.toString())
        Assert.assertEquals(setUp1Key, conditionsOrderMap[index++])
        Assert.assertEquals(setUp2Key, conditionsOrderMap[index++])
        Assert.assertEquals(testKey, conditionsOrderMap[index++])
        Assert.assertEquals(tearDown1Key, conditionsOrderMap[index++])
        Assert.assertEquals(tearDown2Key, conditionsOrderMap[index])
    }.add(key = SET_UP_EXCEPTION_FLOW) {
        var index = 1
        Log.info(conditionsOrderMap.toString())
        Assert.assertEquals(setUp1Key, conditionsOrderMap[index++])
        Assert.assertEquals(tearDown1Key, conditionsOrderMap[index++])
        Assert.assertEquals(tearDown2Key, conditionsOrderMap[index])
    }.add(key = NO_EXCEPTION_FLOW) {
        var index = 1
        Log.info(conditionsOrderMap.toString())
        Assert.assertEquals(setUp1Key, conditionsOrderMap[index++])
        Assert.assertEquals(setUp2Key, conditionsOrderMap[index++])
        Assert.assertEquals(testKey, conditionsOrderMap[index++])
        Assert.assertEquals(tearDown1Key, conditionsOrderMap[index++])
        Assert.assertEquals(tearDown2Key, conditionsOrderMap[index])
    }.add(key = TEST_EXCEPTION_FLOW) {
        var index = 1
        Log.info(conditionsOrderMap.toString())
        Assert.assertEquals(setUp1Key, conditionsOrderMap[index++])
        Assert.assertEquals(setUp2Key, conditionsOrderMap[index++])
        Assert.assertEquals(tearDown1Key, conditionsOrderMap[index++])
        Assert.assertEquals(tearDown2Key, conditionsOrderMap[index])
    }.add {
        counter.set(0)
        conditionsOrderMap.clear()

    }

    val expectedException = ExpectedException.none()

    @get:Rule
    val ruleSequence = RuleSequence(setUp1, tearDown1).add(tearDown2, setUp2).addLast(controlTearDown)

    @Test
    @TearDown(NO_EXCEPTION_FLOW)
    fun noExceptionFlow() {
        conditionsOrderMap[counter.incrementAndGet()] = testKey
    }

    @Ignore("There is a bug in expected part of JUnit. If an expected exception occurs out of the @Test scope the test fails")
    @SetUp(SET_UP_WITH_EXCEPTION)
    @TearDown(SET_UP_EXCEPTION_FLOW)
    @Test(expected = SetUpException::class)
    fun setUpExceptionFlow() {
        conditionsOrderMap[counter.incrementAndGet()] = testKey
    }

    @Suppress("UNREACHABLE_CODE")
    @Test(expected = TestException::class)
    @TearDown(TEST_EXCEPTION_FLOW)
    fun testExceptionFlow() {
        throw TestException(TEST_EXCEPTION_FLOW)
        conditionsOrderMap[counter.incrementAndGet()] = testKey
    }

    @Ignore("There is a bug in expected part of JUnit. If an expected exception occurs out of the @Test scope(e.g. @Before or @After) the test fails")
    @TearDown(TEAR_DOWN_WITH_EXCEPTION)
    @SetUp(TEAR_DOWN_EXCEPTION_FLOW)
    @Test(expected = TearDownException::class)
    fun tearDownExceptionFlow() {
        conditionsOrderMap[counter.incrementAndGet()] = testKey
    }

    private class SetUpException(override val message: String) : RuntimeException()
    private class TestException(override val message: String) : RuntimeException()
    private class TearDownException(override val message: String) : RuntimeException()
}

