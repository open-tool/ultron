import com.atiurin.ultron.annotations.ExperimentalUltronApi
import com.atiurin.ultron.core.test.UltronTest
import com.atiurin.ultron.log.UltronLog
import kotlin.test.Test
import kotlin.test.assertTrue

class UltronTestFlowTest2 : UltronTest() {
    var order = 0
    var beforeAllTestCounter = 0

    @OptIn(ExperimentalUltronApi::class)
    override val beforeFirstTest = {
        beforeAllTestCounter = order
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
            beforeOrder = order
            order++
            UltronLog.info("Before TestMethod 1")
        }.go {
            goOrder = order
            order++
            UltronLog.info("Run TestMethod 1")
        }.after {
            afterOrder = order
            assertTrue(beforeAllTestCounter == 0, message = "beforeAllTests block should run before all test")
            assertTrue(beforeOrder > beforeAllTestCounter, message = "Before block should run after 'Before All'")
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
}