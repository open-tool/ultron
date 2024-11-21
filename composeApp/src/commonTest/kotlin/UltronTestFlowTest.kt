import com.atiurin.ultron.core.test.UltronTest
import com.atiurin.ultron.log.UltronLog
import kotlin.test.Test

class UltronTestFlowTest: UltronTest() {
    override val beforeClass = {}

    override val afterClass = {}

    override val afterTest = {}

    @Test
    fun someTest() = test {
        before {
            UltronLog.info("Before TestMethod")
        }.after {
            UltronLog.info("Before TestMethod")
        }.run {
            UltronLog.info("Run TestMethod")
        }
        UltronLog.info("UltronTest test")
    }
}