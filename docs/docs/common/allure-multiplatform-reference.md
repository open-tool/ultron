---
sidebar_position: 3
---

# Allure Multiplatform reference

For your first report, follow the [setup guide](./allure-multiplatform.md). This page covers configuration, lifecycle and attachments.

## Configuration

`UltronAllureConfig` holds an `AllureConfigParams` instance in `params`. The object is one for every target; the members below are what it has on the Compose Multiplatform targets, the Android page lists the [Android ones](./allure.md#custom-config). `applyRecommended()` resets the parameters to their defaults and registers everything; `apply { }` changes the current parameters and re-registers. Calling either one again replaces the previous registration, so the configuration can be changed between tests. Note that `applyRecommended()` **discards** earlier customisations — call it first, `apply { }` after.

```kotlin
UltronAllureConfig.apply {
    resultsDirectory = null                       // null → ULTRON_ALLURE_RESULTS_DIR → build/allure-results
    addScreenshotPolicy = mutableSetOf(
        AllureAttachStrategy.TEST_FAILURE,
        AllureAttachStrategy.OPERATION_FAILURE,
    )
    addHierarchyPolicy = mutableSetOf(
        AllureAttachStrategy.TEST_FAILURE,
        AllureAttachStrategy.OPERATION_FAILURE,
    )
    attachUltronLog = true
    detailedAllureReport = true
    videoPolicy = mutableSetOf(AllureAttachStrategy.NONE)
    videoFormat = VideoFormat.AUTO // VideoFormat.APNG returns the animated PNG
    videoQuality = VideoQuality.MEDIUM
    videoLimits = FrameRecorder.Limits(
        maxFrames = 200,
        maxBytes = 20L * 1024 * 1024,
        maxFrameDurationMs = 3000,
        lastFrameDurationMs = 1000,
        minFrameDurationMs = 500,
        retryFrameIntervalMs = 500,
    )
}
```

| Parameter | Default | Meaning |
|---|---|---|
| `resultsDirectory: String?` | `null` | Where result files and attachments are written. `null` means: use the `ULTRON_ALLURE_RESULTS_DIR` environment variable when set, otherwise `build/allure-results` relative to the working directory of the test process. A new directory applied while a test is open takes effect from the next test: the open test writes its remaining attachments and its result where it started, next to the attachments it already has. Ignored on js/wasmJs, where results stay in memory. |
| `addScreenshotPolicy: MutableSet<AllureAttachStrategy>` | `TEST_FAILURE, OPERATION_FAILURE` | When to attach a PNG screenshot of the whole Compose scene. `OPERATION_*` values attach to the step of that operation, `TEST_FAILURE` attaches at test level while the scene is still alive. |
| `addHierarchyPolicy: MutableSet<AllureAttachStrategy>` | `TEST_FAILURE, OPERATION_FAILURE` | Same for the semantics-tree dump (unmerged tree of every root node, full depth, `text/plain`). |
| `attachUltronLog: Boolean` | `true` | Attach `ultron_log.txt` when the test logged at least one line. Lines logged before the test opens are excluded. |
| `detailedAllureReport: Boolean` | `true` | Turn every Ultron operation into an Allure step. With `false` operations produce no steps; the result keeps the manual [`step`](#manual-steps) steps and the `before`/`after` steps of `UltronTest`. |
| `videoPolicy: MutableSet<AllureAttachStrategy>` | `NONE` | Frame-by-frame recording of the test, see [Video of the test](#video-of-the-test). `TEST_FAILURE` records every test but attaches only failed ones, `TEST_FINISH` attaches all, `NONE` records nothing. Other values are ignored. |
| `videoLimits: FrameRecorder.Limits` | see below | Memory and timing bounds of the recording. |
| `videoFormat: VideoFormat` | `AUTO` | Format of the `video` attachment: `AUTO` and `MP4` use the mp4 encoder of the target and fall back to APNG, `APNG` always attaches the animated PNG. |
| `videoQuality: VideoQuality` | `MEDIUM` | `LOW`, `MEDIUM`, `HIGH`: bit rate 0.5 / 1 / 2 Mbit/s on iOS, macOS and in the browser. |

`FrameRecorder.Limits`:

| Field | Default | Meaning |
|---|---|---|
| `maxFrames` | `200` | Maximum number of frames kept; the oldest frames are dropped first. |
| `maxBytes` | `20 MiB` | Maximum total size of the encoded frames kept in memory. A single frame larger than this is dropped. |
| `maxFrameDurationMs` | `3000` | Longest time one frame is shown in the animation, whatever the real gap between operations was: a 10 s wait is not a 10 s frame. |
| `minFrameDurationMs` | `500` | Shortest time one frame is shown: operations take milliseconds, a frame needs to stay long enough to be seen. |
| `retryFrameIntervalMs` | `500` | While an operation is retried (waits for the UI), capture a frame at most this often, so a long wait shows its intermediate states. `0` captures only after operations. |
| `lastFrameDurationMs` | `1000` | How long the final frame stays before the animation loops. |

### `AllureAttachStrategy`

The enum has the same values as on Android, so a policy reads the same in both modules:

| Value | Screenshot / hierarchy | Video |
|---|---|---|
| `OPERATION_FAILURE` | attached to the step of a failed operation | ignored |
| `OPERATION_SUCCESS` | attached to the step of a successful operation | ignored |
| `OPERATION_FINISH` | attached to every operation step | ignored |
| `TEST_FAILURE` | attached at test level when the test fails | recorded for every test, attached when the test fails |
| `TEST_FINISH` | no effect (screenshots and hierarchies are not taken at the end of a passed test) | recorded and attached for every test |
| `NONE` | disables the kind entirely, whatever else is in the set | disables recording |

A capture that is unavailable on a target or throws is logged with `UltronLog.error` and skipped; it never fails the test.

## Test names and boundaries

### UltronTest lifecycle

When a test is written with [`UltronTest`](./ultrontest.md) (the recommended form, see [Write tests with `UltronTest`](./allure-multiplatform.md#write-tests-with-ultrontest)), the Allure test follows its lifecycle:

- `test { }` opens the Allure test, labelled `suite = <test class>` (the simple class name) and `ultron.testBoundary = ultronTest`; the class name comes from `UltronTest` itself, so the grouping works on Native and in the browser too. `fullName` is `<qualified test class>.<test name>` on the JVM and on Native, where the package is known, and on js/wasmJs, whose reflection has no qualified names, `<test class>.<test name>` with the package taken from the Karma adapter's `__ultronCurrentSuite` (see [Test names in the browser](#test-names-in-the-browser)) when it names the same class; `historyId` is derived from it, so same-named classes in different packages do not merge. Without the adapter the browser falls back to the simple class name, and `auth.LoginTest` and `admin.LoginTest` with the same `test(name = ...)` share a `fullName` and a `historyId`.
- The test name is chosen in this order:
  1. the explicit `test(name = "...")`; when it replaces a JVM `@Test` method found on the stack, that method is kept as the label `ultron.testMethod`;
  2. otherwise the `@Test` method discovered from the stack on the JVM;
  3. otherwise `unknown test`.
- The `before`, `go` and `after` blocks run in this order. If `before` throws, `go` and `after` are skipped. If `go` throws, `after` still runs. The `before` and `after` blocks become steps named `before` and `after`, with the status of the block, empty when the block is not set. `go { }` adds no step of its own, its Ultron operations are the top-level steps as usual. In the short form `test { ... }`, without `go { }`, the operations run while the test is being configured, outside the three blocks, so their steps come before the empty `before` step.
- On js/wasmJs `runUltronUiTest` returns a promise whose body runs after the block that called it has returned. `test { }` waits for it: the next block, the common `afterTest` and the end of the Allure test come after the UI code, and its failure is the failure of the block. `test { }` itself returns a promise there (`UltronTestResult`; `Unit` on JVM, Android and Native), settled when the result is written, the browser video included (at most 10 seconds, see [Limits and caveats](#limits-and-caveats)); return it from the test function, otherwise the runner does not wait for the test. Without a `runUltronUiTest` inside, `test { }` runs synchronously in the browser too and throws its failure from the call.
- `runUltronUiTest` inside `go { }` **joins** the open test rather than starting a second one. `runUltronUiTest("name")` names the open test only when no explicit `test(name = ...)` was given; with an explicit name its argument is ignored.
- The reverse nesting, `runUltronUiTest { test { } }`, produces one result as well: the test opened by `runUltronUiTest` is adopted by `test { }`, which adds its labels and steps and renames it when `test(name = ...)` is given; `runUltronUiTest` still closes it, with the failure screenshot, hierarchy and video. A `test { }` that failed fails its result even when the block catches the failure and goes on; its screenshot and hierarchy are taken when `test { }` fails, while the scene is alive. This holds for the first test of the run too, whose `beforeFirstTest` applies `UltronAllureConfig` after `runUltronUiTest` has already started with no reporter registered: `apply` opens the test itself at that point, so the first test gets its artifacts like every later one. A first `apply` from the common `beforeTest` or from `go { }` works too: the listener it registers catches up on the `test { }` in progress (`UltronTestListeners.activeTest`) and adopts the test, so the suite and `test(name = ...)` are kept; re-applying the configuration from there keeps the open test as well. `test { }` is the top-level unit of a test, and `runUltronUiTest { test { } }` is meant to hold exactly one `test { }`: a second `test { }` in the same block is written as a separate result (the first is closed when the second starts), but unnamed ones share `fullName` and therefore `historyId`, so Allure shows them as one test with retries. Write one `test { }` per `runUltronUiTest` block.
- A `test { }` in the `go { }` of another one joins the outer test: one result, named after the outer `test { }`, with the inner one's `before`/`after` steps and operations among its steps; only the outer `test { }` closes it. The inner one's failure shows up in the result as far as it fails the outer one: caught in the outer `go { }`, it is not reported.
- The result is written when `test { }` returns, after the common `afterTest`; a failure anywhere, including in `after { }`, fails the test.

The mechanism is generic: `UltronTestListener` in `ultron-common` receives `beforeTest`/`afterTest` and `beforeBlock`/`afterBlock` events from every `UltronTest`, and `UltronTestListeners.register(...)` adds your own listener next to the Allure one.

The *Desktop* and *iOS* tabs in [The report on each target](./allure-multiplatform.md#the-report-on-each-target) show the outcome: `ContactsDesktopDemo` and `ContactsIosDemo` as suites, `before` and `after` as steps around the operations.

### `runUltronUiTest` as the boundary

Outside `UltronTest`, every `runUltronUiTest` / `runDesktopUltronUiTest` block is one Allure test. Both functions have an overload whose first parameter is `testName: String`; the original signatures are unchanged and keep working. The explicit name is the one to prefer in common code, because not every platform can discover it:

```kotlin
runUltronUiTest("cart keeps items after relaunch") { ... }
runDesktopUltronUiTest("wide layout", 1400, 900) { ... }
```

The explicit name is the result's `name`. Where the test class can still be discovered (desktop, and the browser with the Karma adapter described below), `fullName` is `<class>.<name>` and the result gets the `suite` label of that class, so same-named tests of different classes keep apart `historyId`s; where no class is known (Native, the browser without the adapter), `fullName` is the bare name.

A parameterized test keeps one name and tells its invocations apart by their parameters (on desktop, iOS, macOS and in the browser; `UltronAllure` does not exist on Android):

```kotlin
@ParameterizedTest
@ValueSource(strings = ["alice", "bob"])
fun login(user: String) = runUltronUiTest("login") {
    UltronAllure.parameter("user", user)
    ...
}
```

Without a name:

| Platform | Name discovery |
|---|---|
| Desktop (JVM) | The enclosing JUnit / `kotlin.test` method is found on the stack (a method annotated with `org.junit.Test`, `org.junit.jupiter.api.Test`, `org.junit.jupiter.api.RepeatedTest`, `org.junit.jupiter.params.ParameterizedTest` or `kotlin.test.Test`). `name` is the method, `fullName` is `<qualified class>.<method>`, and a `suite` label with the simple class name groups the test in the Suites tree. The parameters of a JUnit 4 `Parameterized` or JUnit 5 `@ParameterizedTest` method, and the index of a `@RepeatedTest`, are not on the stack: every invocation gets the same `fullName`, so the report shows them as retries of one test. Add the value as a parameter, `UltronAllure.parameter("user", user)` inside the block (see [Labels](#labels)), to report them apart under one test case. |
| iOS / macOS (Kotlin/Native) | Nothing can be discovered; the result is named `unknown test` and labelled `ultron.testBoundary = implicit`. |
| js / wasmJs | The browser globals `window.__ultronCurrentTest` (method) and `window.__ultronCurrentSuite` (package and class) are read if your Karma setup sets them (see [Test names in the browser](#test-names-in-the-browser)); otherwise `unknown test` as on Native. |
| Android | Not applicable: Android tests report through `allure-kotlin` and `UltronAllureTestRunner`, which name the test from the JUnit method (see [Allure (Android)](./allure.md)). |

An Ultron operation executed while no test is open — for example from a plain `runComposeUiTest` — opens an implicit `unknown test` itself; the implicit test is closed when the next test starts, with the worst status of its steps (`failed` over `broken` over `passed`), since nothing else reports on it. The last implicit test of a run has no next test: on desktop, iOS and macOS it is written when the process exits (a JVM shutdown hook, `atexit` on Kotlin/Native), with its Ultron log and video, on js/wasmJs it is lost, as the browser offers no end-of-run hook; name the test, or run it through `UltronTest`, when its result matters. If a test is still open when another one starts (a hook was bypassed, or on js/wasmJs a test hit mocha's timeout and its promise never completed), a warning is logged and the previous test is closed as `broken` with the message `test did not finish before '<next test>' started`, so a hung test never shows up as passed. This applies to the `unknown test` of an unnamed `runUltronUiTest` as well: only the implicit test opened by an operation closes with the status of its steps; one opened by `runUltronUiTest` that is still open when the next test starts, or when the process exits, is `broken`.

Do not nest `runUltronUiTest` blocks. Each block starts a Compose scene of its own and points Ultron at it, so after an inner block returns, the operations of the outer block address the inner scene, which is gone. Should a `runUltronUiTest` run inside the body of another one anyway (directly, or in `go { }` of a `test { }` nested in the outer block), the report stays one result: the inner block joins the test of the outer block, names it only when nothing named it before (neither `test(name = ...)` nor the outer `runUltronUiTest(testName)`), and only the outermost block closes it. A `test { }` in the inner block adopts that test as it would in the outer one. A block started on another thread while another one hangs, or on js/wasmJs while another block's promise is still running, is not nested: it overlaps, and the hung test is closed as `broken` as described above.

Explicitly named `runUltronUiTest` tests carry a `suite` label only where their class is discovered (see above); elsewhere, on Native and in the browser without the Karma adapter, they are listed flat at the root of the *Suites* tree, and there is no public API to add labels yet. On every target a test that runs through `UltronTest` is grouped under its class.

### Status

A test is `passed` when the block returned, `skipped` when the exception or any of its causes is an assumption failure (JUnit 4's `org.junit.AssumptionViolatedException` from `Assume.assumeTrue(...)`, `org.junit.internal.AssumptionViolatedException`, JUnit 5's `org.opentest4j.TestAbortedException` from `Assumptions`, or a subclass; recognised on the JVM only), `failed` when the exception or any of its causes is an `AssertionError` (Ultron wraps a failed assertion into `UltronOperationException`, whose cause chain contains the assertion error), and `broken` for any other exception. `statusDetails.message` and `statusDetails.trace` are filled from the exception. Steps follow the same rule.

### Labels

Every result carries `framework = ultron`, `language = kotlin` and `host = <platform>` (see [Supported targets](./allure-multiplatform.md#supported-targets)); a result whose class is discovered (from the stack on desktop, named or not) also has `suite = <class>`, an implicit test has `ultron.testBoundary = implicit`, and an `UltronTest` named with `test(name = ...)` on the JVM keeps its `@Test` method in `ultron.testMethod`. Every result also has the parameter `target`, the Kotlin target it ran on: `desktop`, `ios`, `macos`, `js` or `wasmJs` (finer than `host`, which is `web` for both browser targets). `historyId` is derived from `fullName` and `target`: repeated runs of a test with a stable name on one target share it, so the Allure history and retries features work across runs, while the same test on two targets gets two `historyId`s. Results of several targets in one `allure generate` therefore show as separate tests, and a failure on one target is never hidden as a retry behind a pass on another; Allure versions that identify a test by its name and parameters separate them through `target`.

`UltronAllure.parameter(name, value)` adds a parameter of your own, listed after `target`, to the test of the calling `runUltronUiTest` block or `test { }` (a second value for the same name replaces the first; `target` itself cannot be replaced). `historyId` is then derived from `fullName`, `target` and these parameters sorted by name: the invocations of a parameterized or repeated test with different values get different `historyId`s and show as separate results, each invocation keeps its `historyId` from run to run, and `fullName`, the name of the test case, stays the same for all of them.


## What the report contains

For every test:

- the **result**: `passed` / `failed` / `broken`, duration, message and stack trace, labels (`host`, `framework`, `language`, optionally `suite`) and the `target` parameter;
- **steps**: every Ultron operation, nested the way operations nest, each with its own status, duration and — on failure — message and trace. The step name is the operation name, i.e. what you see in the Ultron log (`Click on '...' CENTER`, `AssertTextEquals '[Step 3 of 10]' in '...'`). Use `withName("...")` on the element to replace the long semantics description with your own name in step titles. Operation types listed in `UltronCommonConfig.operationsExcludedFromListeners` produce no step;
- **step attachments**: `screenshot_<operation name>` (`image/png`) and `hierarchy_<operation name>` (`text/plain`) on a failed operation step (default policies);
- **test attachments**: `test_failure_screenshot`, `test_failure_hierarchy` on a failed test, `ultron_log.txt` on every test that logged at least one line, and `video` when the video policy says so.

The *Desktop* tab in [The report on each target](./allure-multiplatform.md#the-report-on-each-target) shows a passed test with its steps and the `video` and `ultron_log.txt` attachments; the *iOS* tab shows a failed one — the failed step carries its own screenshot and hierarchy, the test carries the failure-time pair, the log and (with `TEST_FINISH`) the video.

The screenshot is the whole Compose scene (what `runComposeUiTest` renders into its surface, with the size you gave `runDesktopUltronUiTest` or the platform default), not a device screen. The hierarchy is `printToString(maxDepth = Int.MAX_VALUE)` of every root node of the unmerged semantics tree — the same text Compose prints in its own assertion failures, but complete.

### Manual steps

Manual steps use the same `step` function shape as the Android module; operations executed inside become child steps and the step's status reflects whether the block threw:

```kotlin
import com.atiurin.ultron.allure.step.step

step("open settings") {
    hasTestTag("menu").click()
    hasTestTag("settings").click()
}
```

As on Android, wrapping Page Object methods in `step` is the recommended way to get a readable report.

### Custom attachments

There is no `AttachUtil` yet. The lifecycle that the module uses is reachable as `UltronAllure.lifecycle` (Compose Multiplatform side only, see [What common test code can see](./allure-multiplatform.md#what-common-test-code-can-see)), and its `attach(name, bytes, extension, mimeType)` links an attachment to the current step (or to the test when no step is open), while `attachToTest(...)` always links it to the test:

```kotlin
import com.atiurin.ultron.allure.UltronAllure

UltronAllure.lifecycle.attach("state.json", stateJson.encodeToByteArray(), "json", "application/json")
```

Attachments written this way follow the same sink as everything else, including the in-memory one on js/wasmJs.

## Video of the test

Compose UI tests run on a virtual clock and render on demand, so there is no real-time screen to record. Instead `videoPolicy` records the test **frame by frame**: the screen before the first Ultron operation, one screenshot of the Compose scene after every operation, plus the screen at the moment of failure, merged when nothing changed. The frames are encoded into an **mp4 (H.264)** where the target has an encoder, and into an **animated PNG** (APNG, `image/png`) everywhere else and whenever the encoder fails. Either way the attachment is named `video`; the sample tests in `composeApp` do this on desktop, the iOS simulator and wasmJs.

| Target | Encoder | `video` attachment | Falls back to APNG when |
|---|---|---|---|
| iOS, macOS | `AVAssetWriter` (VideoToolbox), H.264 Baseline | `video/mp4` | the system encoder is unavailable (for example CI without a GPU session) or fails |
| js, wasmJs (browser) | WebCodecs `VideoEncoder`: H.264 (`avc1.42001E`), then VP9 / VP8 in webm | `video/mp4`, or `video/webm` in a Chromium build without H.264 | the browser has no `VideoEncoder` (Node, old browsers), supports none of the codecs, or the report is collected before the encoder finished |
| desktop (JVM) | JCodec (H.264 Baseline, pure Java, in memory) | `video/mp4` | `image/png` when encoding fails |

Set `videoFormat = VideoFormat.APNG` to get the animated PNG everywhere, as before mp4 support. `videoFormat = VideoFormat.MP4` asks for mp4 but still falls back to APNG: a report never changes the outcome of a test.

The *Desktop* tab in [The report on each target](./allure-multiplatform.md#the-report-on-each-target) shows such an attachment opened inline in Allure.

### How it works

- Recording starts with the test and stops with it. `TEST_FAILURE` records every test but attaches only failed ones, `TEST_FINISH` attaches all, `NONE` (default) records nothing; the `OPERATION_*` values have no meaning for the video. The test is the Allure test: several `runUltronUiTest` blocks inside one `UltronTest.test { }` make one recording and one `video` attachment.
- Before the first Ultron operation of the test the scene is captured once, so the video starts with the screen as the test found it. After every Ultron operation the scene is captured with `captureToImage()` and PNG-encoded right away. A frame identical to the previous one is dropped, so a test with a static screen yields a single frame, and an assertion, which does not change the screen, adds none: the checks are in the steps, the video shows how the screen changed.
- Frames are drawn over white before they are encoded: mp4 and webm have no alpha channel, so a scene without a background of its own (no `Surface`) would turn black and dark text on it invisible. Screenshot attachments keep the scene's own transparency.
- While an operation is being retried (an assertion waiting for text to appear, say), one more frame is captured every `retryFrameIntervalMs` (default 500 ms) between two polls, so a three-second wait shows the screen as it was at each half second instead of one frame.
- `UltronAllure.captureVideoFrame("label")` adds a frame on demand from the test body, for example after an animation the test waited for without an Ultron operation. `UltronAllure` exists on the Compose Multiplatform side only; in a module that also has an Android target, call it from the Skiko test source set (see [What common test code can see](./allure-multiplatform.md#what-common-test-code-can-see)).
- On failure one more frame is captured while the scene is still alive, labelled `test failure`, so the last frame shows what the test saw.
- Frame delays come from the real clock: the time between two captures, clamped to `[minFrameDurationMs, maxFrameDurationMs]`; the last frame stays `lastFrameDurationMs`. The animation loops forever.
- Frames live in a ring buffer bounded by `videoLimits.maxFrames` and `maxBytes`; when a limit is exceeded the oldest frames go first and a warning `Allure: video kept the last N frames, M dropped by limits` is logged.
- Cost: one `captureToImage()` plus one PNG encode per frame, about 25 ms on desktop for the default scene size. With `retryFrameIntervalMs = 500` a waiting operation spends about 5 % of its time on frames; the sample `desktop waiting video sample` in `composeApp` waits three seconds and finishes in about 3.6 s including scene start-up. The capture runs on the test thread and never touches the application code, so the app itself is not slowed down; only the test takes longer by the capture time.

### Reading it in Allure

The attachment is named `video` and listed with the test-level attachments. Expanding the row shows the video (or the animation, for an APNG) inline; the full-screen button opens it in the viewer (Chrome, Firefox and Safari all play APNG natively). "Open attachment in new tab" shows the raw file. The sample above shows the `desktop video sample` test from `composeApp`: a counter, a progress bar and a coloured box that change on every step, so each frame is different.

### Limits and caveats

- **It is a sequence of states, not a recording.** Frames are taken before the first operation, after every operation, between retry polls of a waiting operation and on `captureVideoFrame`. Animations between those moments and anything that happens while the test thread is busy with its own code are not in it. There is no background capture thread: the Compose test scene is single-threaded, and on wasmJs there are no threads at all.
- **Lossless PNG frames.** File size grows with the scene size and the number of distinct frames; the defaults (200 frames / 20 MiB) are generous for a desktop scene and too generous for a browser run with `TEST_FINISH`. Tune `videoLimits` or use a smaller scene size on `runDesktopUltronUiTest`.
- **Why APNG stays as the fallback.** Skiko itself encodes only PNG, JPEG and still WebP; the H.264 encoders come from the platform (`AVAssetWriter`, WebCodecs) and are not available everywhere. Allure 2 renders `image/png` in every version, whereas `image/webp` is only rendered from 2.35. See the [feasibility notes](https://github.com/open-tool/ultron/blob/master/docs/superpowers/specs/2026-10-29-test-video-recording-feasibility.md) and the [per-platform mp4 assessment](https://github.com/open-tool/ultron/blob/master/docs/superpowers/specs/2026-11-03-mp4-test-video-per-platform.md).
- **Odd scene sizes grow by one pixel.** H.264 needs even dimensions, so a 375×667 scene becomes a 376×668 video; the last column and row are repeated.
- **The last frame on iOS and macOS.** The session ends at the sum of the delays (`endSessionAtSourceTime`), but `AVAssetWriter` may give the last sample any duration, so the last frame is appended once more one millisecond before the end: the iOS and macOS mp4 holds one sample more than there are frames, the extra one repeating the last picture. If a player still shows the last frame shorter than its delay, this is why.
- **VideoToolbox in CI.** A macOS runner without a GPU session may have no system H.264 encoder: the encoder then fails, a warning with the reason is logged and the attachment is an APNG.
- **webm in Chromium without H.264.** A Chromium build without proprietary codecs gets VP9 (or VP8) in webm; the attachment is `video/webm`.
- **Browser results are written later.** The browser encoder is asynchronous, so the result of a test is written once the video is ready; `runUltronUiTest`, or the `test { }` that owns the result, waits for that (at most 10 seconds, then the video is an APNG) before its promise resolves. `UltronAllureResults.snapshot()` and `dumpToConsole()` settle a video that is still being encoded as an APNG first, so a test is never missing from what you collect.
- **Licence.** The JVM encoder uses [JCodec](http://jcodec.org/) 0.2.5 (BSD-2-Clause, no transitive dependencies). It is added to the JVM source set only and does not reach the Android variant; keep JCodec's copyright and licence notice when you redistribute an application that bundles `ultron-allure`.
- **One test at a time per process.** The recorder is process-wide; tests entered through `runUltronUiTest` in parallel would interleave their frames.
- **Only tests entered through `runUltronUiTest` / `runDesktopUltronUiTest` are recorded.** A plain `runComposeUiTest` has no boundary and therefore no video.
- **Android:** the Android variant of `ultron-allure` does not use the frame recorder; its `recordVideoPolicy` produces a real H.264 mp4 with the device's `screenrecord` (see [Screen recording](./allure.md#screen-recording)).

### Checking the attachment programmatically

`ApngInfo` reads the frame count of an attached file, which is handy in your own smoke tests of the reporting setup:

```kotlin
import com.atiurin.ultron.allure.video.ApngInfo

val frames = ApngInfo.frameCount(bytes) // null for a still PNG or a non-PNG
```

`Mp4Info.parse(bytes)` does the same for an mp4 (`sampleCount`, `sampleDurations`, `width`, `height`, `codec`); it returns `null` for anything that is not an mp4.

### Checking the video on each target

These steps need a simulator, a browser or `ffmpeg`, which the CI of this repository does not have; run them by hand after a change to the video code.

1. **desktop:** `./gradlew :composeApp:desktopTest`, then find the `video` attachment in `composeApp/build/allure-results/desktop` and check its type; with an mp4, `ffprobe -show_entries stream=codec_name,width,height,nb_frames,duration` shows `h264` and the frame count, and `ffmpeg -v error -i <file> -f null -` prints nothing.
2. **iOS simulator:** `SIMCTL_CHILD_ULTRON_ALLURE_RESULTS_DIR=<dir> ./gradlew :composeApp:iosSimulatorArm64Test`; the same checks, and the duration `ffprobe` reports equals the sum of the frame delays.
3. **wasmJs and js:** `./gradlew :composeApp:wasmJsBrowserTest` / `:composeApp:jsBrowserTest` with the console collector from this page; the attachment is `video/mp4` in Google Chrome and `ffmpeg` decodes it.
4. **Chromium without H.264** (a Docker image with the packaged `chromium`): the attachment is `video/webm` and plays in Allure.
5. **macOS native** (`macosArm64Test`): the same as iOS.

## Browser results in detail

The browser setup itself is in [Browsers (js / wasmJs)](./allure-multiplatform.md#browsers-js--wasmjs): every result file and attachment is kept in `UltronAllureResults` in the page and printed to the console as it is written. Under Node.js (`jsNodeTest`) the result writer works the same way and prints the same lines to the test task's standard output, but Compose UI tests do not run there.

### The in-memory store

Two accessors expose the store if you want a different transport:

```kotlin
import com.atiurin.ultron.allure.writer.UltronAllureResults

UltronAllureResults.snapshot()      // Map<fileName, ByteArray> — result JSON files and attachments
UltronAllureResults.dumpToConsole() // prints every file again, for a transport that only reads the end of the log
```

`dumpToConsole()` is optional with the console setup: the browser encodes the video asynchronously and the result of a test waits for it, but the promise of `test { }` (or of a plain `runUltronUiTest`) settles only once the result of its test is written, so the result of the last test is in the console before Karma closes the page. The wait is bounded: a video not encoded within 10 seconds is attached as an APNG of its frames and the result written at once; it counts toward mocha's per-test timeout. Called from a test that runs last (`kotlin.test` runs the tests of a class in declaration order on the browser targets, so prefix the name, `z_dumpResultsToConsole`), it re-prints the whole store, which the collect task handles as a no-op.

Keep the attachments small on the browser targets: the store keeps every file of the run, attachments and video included, until the page is unloaded, the console log grows by a third because of base64, and a video for every test (`TEST_FINISH`) adds up quickly. `TEST_FAILURE` policies and the default `videoLimits` are the safe choice there. A run that relies on the console transport alone can stop keeping the files in the page: `UltronAllureResults.keepInMemory = false` (set it once, before the first test; files written earlier stay) streams every file to the console as before but retains nothing, so `snapshot()` and `dumpToConsole()` see only what was written while it was `true`.

On js/wasmJs `test { }` and `runUltronUiTest` return a promise, so a result exists only after the test function has completed. A test cannot inspect its own result; inspect the results of earlier tests instead.

### Test names in the browser

Test-name discovery in the browser is opt-in. Kotlin's Karma runner (`kotlin-test-karma-runner.js`) honours a `window.kotlinTest.adapterTransformer` that is already set when it loads; a transformer that publishes the current test name into `window.__ultronCurrentTest` and the enclosing suites (package and class, joined with `.`) into `window.__ultronCurrentSuite` makes Ultron name the results of tests that pass no explicit name after the `kotlin.test` method, with `fullName = <suite path>.<method>` and a `suite` label with the class, as on desktop.

Two files. The adapter runs in the browser and must **not** live in `karma.config.d/`: every `*.js` there is concatenated into the generated `karma.conf.js` and executed as Node configuration, where `window` does not exist. Keep it next to the config directory, and let a config fragment add it to the front of `config.files` by an absolute path built from `config.basePath` (`__dirname` would point at the generated `karma.conf.js`, not at your sources):

```js
// app/karma-adapters/ultron-test-name-adapter.js
window.kotlinTest = window.kotlinTest || {};
window.kotlinTest.adapterTransformer = function (adapter) {
  var suite = adapter.suite;
  var test = adapter.test;
  var suites = []; // the suite stack while the runner registers the tests
  adapter.suite = function (name, ignored, fn) {
    return suite.call(adapter, name, ignored, function () {
      suites.push(name);
      try {
        return fn();
      } finally {
        suites.pop();
      }
    });
  };
  adapter.test = function (name, ignored, fn) {
    var suitePath = suites.filter(Boolean).join("."); // captured at registration, the stack is gone when the test runs
    return test.call(adapter, name, ignored, function () {
      window.__ultronCurrentTest = name;
      window.__ultronCurrentSuite = suitePath;
      return fn();
    });
  };
  return adapter;
};
```

```js
// app/karma.config.d/ultron-test-name.js
(function () {
    const path = require("path");
    // basePath is <root>/build/<js|wasm>/packages/<root>-app-test; four levels up is the root project
    const adapter = path.resolve(config.basePath, "..", "..", "..", "..", "app", "karma-adapters", "ultron-test-name-adapter.js");
    config.files.unshift({pattern: adapter, included: true, served: true, watched: false});
})();
```

The `composeApp` sample runs with exactly this setup on js and wasmJs ([`karma-adapters/ultron-test-name-adapter.js`](https://github.com/open-tool/ultron/blob/master/composeApp/karma-adapters/ultron-test-name-adapter.js), [`karma.config.d/wasm/ultron-test-name.js`](https://github.com/open-tool/ultron/blob/master/composeApp/karma.config.d/wasm/ultron-test-name.js)); `AllureResultsWasmTest.a_unnamedTestIsNamedByTheKarmaAdapter` checks that a test without an explicit name gets the method name and its class as the suite in its result. The adapter also qualifies the tests of an `UltronTest`: its `test { }` knows only the simple class name in the browser, and takes the package from `__ultronCurrentSuite` when that names the same class, so `fullName` is `<package>.<class>.<test name>` as on desktop. Without `__ultronCurrentSuite` (no adapter, or one that publishes the name only) the result of an unnamed test has the method name alone and no `suite` label, an `UltronTest` gets `fullName = <simple class>.<test name>`, and same-named tests of same-named classes in different packages share a `historyId`. Passing the name to `runUltronUiTest("...")` needs no runner support at all.

## Android and Compose Multiplatform side by side

One artifact, one `UltronAllureConfig`, two implementations: on Android the `actual` drives `allure-kotlin`, on the Skiko targets it drives Ultron's own writer. The table lists what each side offers and which members of `UltronAllureConfig` / `AllureConfigParams` exist only there.

| | Android (`androidMain`) | Compose Multiplatform (desktop/JVM, iOS, macOS, js, wasmJs) |
|---|---|---|
| Targets | Android instrumented tests (Espresso, UI Automator, Compose on Android) | Compose Multiplatform tests through `runUltronUiTest` |
| Allure dependency | `allure-kotlin` 2.4.0 (`allure-kotlin-android`, `allure-kotlin-commons`, `allure-kotlin-model`, `allure-kotlin-junit4`) | None: Ultron's own Allure 2 JSON writer |
| Test boundary | JUnit run listener in `UltronAllureTestRunner` (instrumentation runner); every test method is reported | `runUltronUiTest` / `runDesktopUltronUiTest` block; a plain `runComposeUiTest` is not a test |
| Test names | From JUnit | Explicit `testName` parameter, or discovered on the JVM; `unknown test` on Native and in the browser without the Karma snippet |
| Platform-only members | `setAllureResultsDirectory`, `addRunListener`, `removeRunListener`, `setAllureConditionExecutor`, `setAllureConditionsExecutorWrapper`; params `attachLogcat`, `addConditionsToReport`, `recordVideoPolicy`, `screenRecordOptions`; the data-class members of `AllureConfigParams` (six-parameter constructor, `copy`, `componentN`) | `videoAttacher`; params `resultsDirectory`, `videoPolicy`, `videoLimits`, `videoFormat`, `videoQuality` |
| Results location | `<app files dir>/allure-results` on the device, `setAllureResultsDirectory(...)`; pulled with `adb` or the Allure Gradle/Marathon tooling | Host directory (`resultsDirectory` → `ULTRON_ALLURE_RESULTS_DIR` → `build/allure-results`); in memory plus console streaming in the browser, see [From results to a report](./allure-multiplatform.md#from-results-to-a-report) |
| Steps from operations | Yes (`detailedAllureReport`) | Yes (`detailedAllureReport`) |
| Manual `step { }` | `com.atiurin.ultron.allure.step.step`, reported to `allure-kotlin` | The same function, reported to `UltronAllure.lifecycle` |
| Screenshot | Device screenshot (`UiAutomation`) | PNG of the Compose scene via Skiko |
| Hierarchy | Window / view hierarchy dump (`WindowHierarchyAttachRunListener`) | Unmerged semantics tree of every root node |
| Logcat | Yes (`attachLogcat`) | No |
| Ultron log | On failure (`attachUltronLog`) | On every test (`attachUltronLog`) |
| Test conditions in the report | `SetUpRule` / `TearDownRule` as steps (`addConditionsToReport`) | `UltronTest` `before { }` / `after { }` blocks as steps (the rules are an Android API) |
| Video | Real H.264 mp4 from the on-device `screenrecord` (`recordVideoPolicy`, `ScreenRecordOptions(timeLimitSec = 180, bitRate = 2_000_000, size = null)`), attached as `video/mp4` and played inline; capped at 180 s on API < 34 | One frame per operation (`videoPolicy`, `videoLimits`, `videoFormat`, `videoQuality`), attached as `video/mp4` (H.264) where the target has an encoder and as an animated `image/png` otherwise; frame delays follow the real clock; frames between retry polls and on demand (`captureVideoFrame`), but no continuous recording |
| Attach strategies | `AllureAttachStrategy`: `TEST_FAILURE`, `OPERATION_FAILURE`, `OPERATION_SUCCESS`, `OPERATION_FINISH`, `NONE`, `TEST_FINISH` | The same enum |
| Custom attachments | `AttachUtil.attachFile(...)`, `createCacheFile()` | `UltronAllure.lifecycle.attach(...)` / `attachToTest(...)` |
| Run listeners (`UltronRunListener`) | Yes, `addRunListener` / `removeRunListener` | No; the extension points are `UltronLifecycleListener`, `UltronTestListener` and `UltronTestRunHook` |
| Labels | From `allure-kotlin` (suite, package, …) | `framework`, `language`, `host`, `suite` (JVM discovery or `UltronTest` class), `ultron.testBoundary` |

The Android recording for comparison is in the *Android* tab of [The report on each target](./allure-multiplatform.md#the-report-on-each-target): the mp4 attached on Android plays inline in the report.

Both implementations can feed one report: generate it from a directory that contains the results of every platform, as the screenshots on this page do (desktop, iOS simulator, wasmJs and an Android device in one `allure generate`).

