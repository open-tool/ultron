import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kotlin.time.TimeSource

/**
 * A screen with two kinds of change, so the recording shows which moments it captures:
 * - driven by the test: the step counter and the list of completed steps grow on every click;
 * - driven by time: the square changes colour twice a second and the progress bar runs from 0 to 100 %
 *   over [VideoSample.TIMELINE_MS] on its own, whether or not the test does anything.
 */
@Composable
fun VideoSampleScreen() {
    var step by remember { mutableStateOf(0) }
    var elapsedMs by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        val start = TimeSource.Monotonic.markNow()
        while (true) {
            kotlinx.coroutines.delay(16)
            elapsedMs = start.elapsedNow().inWholeMilliseconds
        }
    }
    val percent = (elapsedMs * 100 / VideoSample.TIMELINE_MS).coerceAtMost(100).toInt()
    val colourIndex = (elapsedMs / VideoSample.COLOUR_PERIOD_MS).toInt() % VideoSample.colors.size
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Step $step of ${VideoSample.STEPS}", Modifier.testTag("count"), style = MaterialTheme.typography.h5)
        Text("Timeline: $percent %", Modifier.testTag("percent"))
        LinearProgressIndicator(progress = percent / 100f, Modifier.fillMaxWidth().testTag("progress"))
        Box(
            Modifier.size(120.dp)
                .background(VideoSample.colors[colourIndex])
                .testTag("box")
        )
        Button(onClick = { step++ }, Modifier.testTag("inc")) { Text("Next step") }
        repeat(step) { Text("Completed step ${it + 1}", Modifier.testTag("done_${it + 1}")) }
    }
}

/**
 * A screen that changes on its own: a wall-clock counter of elapsed seconds. The test waits for it inside
 * one Ultron operation, so the recording has to rely on frames captured between retries.
 */
@Composable
fun ElapsedSecondsScreen() {
    var seconds by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        val start = TimeSource.Monotonic.markNow()
        while (true) {
            kotlinx.coroutines.delay(16)
            seconds = start.elapsedNow().inWholeSeconds.toInt()
        }
    }
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Elapsed: $seconds s", Modifier.testTag("elapsed"), style = MaterialTheme.typography.h4)
        LinearProgressIndicator(progress = (seconds / VideoSample.WAIT_SECONDS.toFloat()).coerceAtMost(1f), Modifier.fillMaxWidth())
    }
}

object VideoSample {
    const val WAIT_SECONDS = 3
    const val STEPS = 10
    /** The progress bar of [VideoSampleScreen] runs from 0 to 100 % over this time. */
    const val TIMELINE_MS = 5_000L
    /** The square of [VideoSampleScreen] takes the next colour this often. */
    const val COLOUR_PERIOD_MS = 500L
    /** Real time between steps; with [STEPS] steps the whole test takes about five seconds. */
    const val STEP_PAUSE_MS = 500L
    /** Every step produces a different screen, so the recording has at least one frame per step. */
    const val EXPECTED_MIN_FRAMES = STEPS
    val colors = listOf(
        Color(0xFF1E88E5), Color(0xFF43A047), Color(0xFFFB8C00), Color(0xFFE53935), Color(0xFF8E24AA)
    )

    fun titleAfter(step: Int) = "Step $step of $STEPS"

    /**
     * Blocks the test for [ms] of wall-clock time. A busy wait is the only option common to every target:
     * the browser has no sleep, and the Compose test clock is virtual and does not pace the recording.
     */
    fun pause(ms: Long = STEP_PAUSE_MS) {
        val mark = TimeSource.Monotonic.markNow()
        while (mark.elapsedNow().inWholeMilliseconds < ms) { /* spin */ }
    }
}
