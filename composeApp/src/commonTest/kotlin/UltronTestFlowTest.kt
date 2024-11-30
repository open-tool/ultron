
import com.atiurin.ultron.annotations.ExperimentalUltronApi
import com.atiurin.ultron.core.test.UltronTest
import com.atiurin.ultron.log.UltronLog
import kotlin.test.Test
import kotlin.test.assertTrue

class UltronTestFlowTest : UltronTest() {
    companion object {
        var order = 0
        var beforeAllTestCounter = -1
        var commonBeforeOrder = -1
        var commonAfterOrder = -1
        var afterOrder = -1

    }

    @OptIn(ExperimentalUltronApi::class)
    override val beforeFirstTest = {
        beforeAllTestCounter = order
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
            assertTrue(beforeAllTestCounter == 0, message = "beforeAllTests block should run before all test")
            assertTrue(beforeAllTestCounter < commonBeforeOrder, message = "beforeAllTests block should run before commonBefore block")
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
            assertTrue(beforeAllTestCounter == 0, message = "beforeAllTests block should run only once")
            UltronLog.info("Run TestMethod 2")
        }
    }

    @Test
    fun simpleTest() = test {
        assertTrue(beforeAllTestCounter == 0, message = "beforeAllTests block should run only once")
        UltronLog.info("UltronTest simpleTest")
    }
}