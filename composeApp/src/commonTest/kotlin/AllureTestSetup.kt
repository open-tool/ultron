/**
 * Applied once per process from each test class's @BeforeTest: kotlin.test has no @BeforeClass on
 * every target, and an `object` initialiser is the portable "run once" primitive.
 *
 * The configuration itself is platform specific (`AllureConfigParams` has different members on Android and
 * on the Skiko targets), hence `expect`/`actual`: `skikoTest` for desktop, iOS, js and wasmJs,
 * `androidSharedTest` for the Android unit and instrumented test source sets.
 */
object AllureTestSetup {
    init {
        configureAllure()
    }

    fun ensure() = Unit
}

internal expect fun configureAllure()
