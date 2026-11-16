import com.atiurin.ultron.allure.config.AllureAttachStrategy
import com.atiurin.ultron.allure.config.UltronAllureConfig

internal actual fun configureAllure() {
    UltronAllureConfig.apply {
        // Sample project: record every test so the demo report shows the animation on each platform.
        videoPolicy = mutableSetOf(AllureAttachStrategy.TEST_FINISH)
    }
}
