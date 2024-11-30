import com.atiurin.ultron.annotations.ExperimentalUltronApi
import com.atiurin.ultron.core.test.UltronTest
import com.atiurin.ultron.log.UltronLog
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UltronTestFlowTest2 : UltronTest() {
    companion object {
        var order = 0
        var beforeFirstTestCounter = 0
    }

    @OptIn(ExperimentalUltronApi::class)
    override val beforeFirstTest = {
        beforeFirstTestCounter++
        order++
        UltronLog.info("Before Class")
    }

    @Test
    fun someTest1() = test {
        var beforeOrder = -1
        var afterOrder = -1
        var goOrder = -1
        order++
        before {
            assertEquals(1, beforeFirstTestCounter, message = "beforeFirstTest block should run before all test")
            beforeOrder = order
            order++
            UltronLog.info("Before TestMethod 1")
        }.go {
            goOrder = order
            order++
            UltronLog.info("Run TestMethod 1")
        }.after {
            afterOrder = order
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
            assertEquals(1, beforeFirstTestCounter, message = "beforeFirstTest block should run before all test")
            UltronLog.info("Run TestMethod 2")
        }
    }
}