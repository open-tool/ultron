import com.atiurin.ultron.annotations.ExperimentalUltronApi
import com.atiurin.ultron.core.test.UltronTest
import com.atiurin.ultron.log.UltronLog
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UltronTestFlowTest : UltronTest() {
    companion object {
        var order = 0
        var beforeFirstTestCounter = 0
        var commonBeforeOrder = -1
        var commonAfterOrder = -1
        var afterOrder = -1
    }

    @OptIn(ExperimentalUltronApi::class)
    override val beforeFirstTest = {
        beforeFirstTestCounter++
        UltronLog.info("Before Class")
    }

    override val beforeTest = {
        commonBeforeOrder = order
        order++
        UltronLog.info("Before test common")
    }
    override val afterTest = {
        commonAfterOrder = order
        order++
        assertTrue(afterOrder < commonAfterOrder, message = "CommonAfter block should run after 'after' test block")
        UltronLog.info("After test common")
    }

    @Test
    fun someTest1() = test {
        var beforeOrder = -1
        var goOrder = -1
        order++
        before {
            assertTrue(beforeFirstTestCounter == 1, message = "beforeFirstTest block should run before all test")
            beforeOrder = order
            order++
            UltronLog.info("Before TestMethod 1")
        }.go {
            goOrder = order
            order++
            UltronLog.info("Run TestMethod 1")
        }.after {
            afterOrder = order
            order++
            assertTrue(commonBeforeOrder < beforeOrder, message = "beforeOrder block should run after commonBefore block")
            assertTrue(beforeOrder < goOrder, message = "Before block should run before 'go'")
            assertTrue(goOrder < afterOrder, message = "After block should run after 'go'")
        }
    }

    @Test
    fun someTest2() = test(suppressCommonBefore = true) {
        before {
            UltronLog.info("Before TestMethod 2")
        }.after {
            UltronLog.info("After TestMethod 2")
        }.go {
            assertTrue(beforeFirstTestCounter == 1, message = "beforeFirstTest block should run only once")
            UltronLog.info("Run TestMethod 2")
        }
    }

    @Test
    fun simpleTest() = test {
        assertTrue(beforeFirstTestCounter == 1, message = "beforeFirstTest block should run only once")
        UltronLog.info("UltronTest simpleTest")
    }


    @Test
    fun afterBlockExecutedOnFailedTest() {
        var isAfterExecuted = false
        runCatching {
            test {
                go {
                    throw RuntimeException("test exception")
                }
                after {
                    isAfterExecuted = true
                }
            }
        }
        assertTrue(isAfterExecuted)
    }

    @Test
    fun testExceptionMessageThrownOnFailedTest() {
        val testExceptionMessage = "test exception"
        runCatching {
            test {
                go {
                    throw RuntimeException(testExceptionMessage)
                }
                after {
                    throw RuntimeException("Another after exception")
                }
            }
        }.onFailure { ex ->
            assertEquals(ex.message, testExceptionMessage)
        }
    }
}