/**
 * Nothing to configure: the Android tests of composeApp run through `AndroidJUnitRunner` without the
 * Allure pipeline (`sample-app` is the Android showcase of `ultron-allure`). The common tests still
 * compile against `UltronAllureConfig`, whose Android `AllureConfigParams` has `recordVideoPolicy`
 * instead of the Skiko `videoPolicy`.
 */
internal actual fun configureAllure() = Unit
