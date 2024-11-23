import com.atiurin.ultron.core.test.UltronTest
import com.atiurin.ultron.log.UltronLog
import kotlin.test.Test

class UltronTestFlowTest: UltronTest() {
    override val beforeAllTests = {}

    override val afterAllTests = {}

    override val afterTest = {}

    @Test
    fun someTest() = test {
        before {
            UltronLog.info("Before TestMethod")
        }.after {
            UltronLog.info("Before TestMethod")
        }.go {
            UltronLog.info("Run TestMethod")
        }
        UltronLog.info("UltronTest test")
    }
}