import androidx.compose.material.Text
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.isRoot
import com.atiurin.ultron.annotations.InternalUltronApi
import com.atiurin.ultron.core.compose.ComposeTestContainer
import com.atiurin.ultron.core.compose.UltronTestRunHook
import com.atiurin.ultron.core.compose.UltronTestRunHooks
import com.atiurin.ultron.core.compose.runDesktopUltronUiTest
import com.atiurin.ultron.core.compose.runUltronUiTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class UltronTestRunHooksTest {
    private val events = mutableListOf<String>()
    private val hook = object : UltronTestRunHook {
        override fun beforeTest(testName: String?) { events += "before:$testName" }
        override fun afterTest(throwable: Throwable?) { events += "after:${throwable?.message}" }
    }
    private val sceneProbe = object : UltronTestRunHook {
        override fun beforeTest(testName: String?) {}
        override fun onTestFailed(throwable: Throwable) {
            events += "failed:${throwable.message}:provider=${ComposeTestContainer.isInitialized}"
            // the scene must still be alive here: a semantics query must not hang or throw
            ComposeTestContainer.getProvider().onAllNodes(isRoot()).fetchSemanticsNodes()
            events += "queried"
        }
        override fun afterTest(throwable: Throwable?) { events += "after:${throwable?.message}" }
    }

    @AfterTest
    fun cleanup() = UltronTestRunHooks.clear()

    @Test
    fun namedOverloadPassesTheNameToTheHookAroundTheBlock() {
        UltronTestRunHooks.register(hook)
        runUltronUiTest("login shows error") {
            setContent { Text("x") }
            events += "body"
        }
        assertEquals(listOf("before:login shows error", "body", "after:null"), events)
    }

    @Test
    fun unnamedCallPassesNullNameAndStillWrapsTheBlock() {
        UltronTestRunHooks.register(hook)
        runUltronUiTest { setContent { Text("x") } }
        assertEquals(listOf("before:null", "after:null"), events)
    }

    @Test
    fun failingBlockReachesAfterTestWithTheThrowableAndRethrows() {
        UltronTestRunHooks.register(hook)
        assertFailsWith<AssertionError> {
            runUltronUiTest("boom") { setContent { Text("x") }; throw AssertionError("expected X") }
        }
        assertEquals("after:expected X", events.last())
    }

    @Test
    fun onTestFailedRunsInsideTheLiveComposeSceneBeforeAfterTest() {
        UltronTestRunHooks.register(sceneProbe)
        assertFailsWith<AssertionError> {
            runUltronUiTest("scene") { setContent { Text("x") }; throw AssertionError("dead") }
        }
        assertEquals(listOf("failed:dead:provider=true", "queried", "after:dead"), events)
    }

    @Test
    fun onTestFailedIsNotCalledForPassingTests() {
        UltronTestRunHooks.register(sceneProbe)
        runUltronUiTest("ok") { setContent { Text("x") } }
        assertEquals(listOf("after:null"), events)
    }

    @Test
    fun desktopNamedOverloadWorksWithSizeParameters() {
        UltronTestRunHooks.register(hook)
        runDesktopUltronUiTest("desktop named", 800, 600) { setContent { Text("x") } }
        assertEquals("before:desktop named", events.first())
    }

    @Test
    fun oldPositionalShapesStillCompileAndRun() {
        var ran = 0
        runUltronUiTest(EmptyCoroutineContext) { setContent { Text("x") }; ran++ }
        runDesktopUltronUiTest(800, 600) { setContent { Text("x") }; ran++ }
        assertEquals(2, ran)
    }

    @Test
    fun registeringTheSameHookIdTwiceKeepsOneHook() {
        UltronTestRunHooks.register(hook)
        UltronTestRunHooks.register(hook)
        runUltronUiTest("once") { setContent { Text("x") } }
        assertEquals(1, events.count { it == "before:once" })
    }

    @Test
    fun aHookThatUnregistersItselfFromItsCallbackDoesNotFailTheTest() {
        val selfRemoving = object : UltronTestRunHook {
            override val id = "selfRemoving"
            override fun beforeTest(testName: String?) { events += "self:before"; UltronTestRunHooks.unregister(id) }
            override fun afterTest(throwable: Throwable?) { events += "self:after" }
        }
        val registering = object : UltronTestRunHook {
            override val id = "registering"
            override fun beforeTest(testName: String?) { UltronTestRunHooks.register(hook) }
            override fun onTestFailed(throwable: Throwable) { UltronTestRunHooks.unregister(id) }
            override fun afterTest(throwable: Throwable?) { events += "registering:after" }
        }
        UltronTestRunHooks.register(selfRemoving)
        UltronTestRunHooks.register(registering)

        assertFailsWith<AssertionError> {
            runUltronUiTest("mutating hooks") { setContent { Text("x") }; throw AssertionError("boom") }
        }

        assertEquals(listOf("self:before", "after:boom"), events, "the removed hook is not called again, the added one is called from the next event on")
        assertEquals(listOf(hook), UltronTestRunHooks.all(), "the self-removing and the self-removing-on-failure hooks are gone, the added one stays")
    }

    @Test
    fun activeRunNamesTheRunningBlockAndIsNullOutsideIt() {
        assertNull(UltronTestRunHooks.activeRun)
        runUltronUiTest("named run") {
            setContent { Text("x") }
            events += "inside:${UltronTestRunHooks.activeRun?.testName}"
        }
        runUltronUiTest {
            events += "unnamed:${UltronTestRunHooks.activeRun != null}:${UltronTestRunHooks.activeRun?.testName}"
        }
        assertEquals(listOf("inside:named run", "unnamed:true:null"), events)
        assertNull(UltronTestRunHooks.activeRun)
    }

    @Test
    fun activeRunIsClearedWhenTheBlockThrows() {
        assertFailsWith<AssertionError> { runUltronUiTest("boom") { throw AssertionError("x") } }
        assertNull(UltronTestRunHooks.activeRun)
    }

    @Test
    fun aNestedRunUltronUiTestLeavesTheOuterActiveRunInPlaceWhenItReturns() {
        runUltronUiTest("outer") {
            runUltronUiTest("inner") { events += "inner:${UltronTestRunHooks.activeRun?.testName}" }
            events += "outer after inner:${UltronTestRunHooks.activeRun?.testName}"
        }
        assertEquals(listOf("inner:inner", "outer after inner:outer"), events)
        assertNull(UltronTestRunHooks.activeRun)
    }

    @Test
    fun aBlockCalledFromTheBodyOfAnotherKnowsItsEnclosingRun() {
        runUltronUiTest("outer") {
            val outer = UltronTestRunHooks.activeRun
            runUltronUiTest("inner") { events += "enclosed by outer:${UltronTestRunHooks.activeRun?.enclosing === outer}" }
            events += "outer is top level:${outer?.enclosing == null}"
        }
        assertEquals(listOf("enclosed by outer:true", "outer is top level:true"), events)
    }

    /** A block that only overlaps another one, started on another thread while that one hangs, is not nested in it. */
    @Test
    fun aBlockOverlappingAnotherOnAnotherThreadHasNoEnclosingRun() {
        val aStarted = CountDownLatch(1)
        val releaseA = CountDownLatch(1)
        val a = thread(name = "hung run") {
            runUltronUiTest("A") { aStarted.countDown(); releaseA.await(10, TimeUnit.SECONDS) }
        }
        assertTrue(aStarted.await(10, TimeUnit.SECONDS), "precondition: A is running")
        try {
            runUltronUiTest("B") { events += "B enclosed:${UltronTestRunHooks.activeRun?.enclosing != null}" }
        } finally {
            releaseA.countDown()
            a.join(10_000)
        }
        assertEquals(listOf("B enclosed:false"), events)
    }

    /**
     * Run A is abandoned (a JUnit timeout left it running on its own thread) when run B starts; A ends while B runs.
     * B must not hand the finished A back as the active run: nothing runs once B is done.
     */
    @Test
    fun aBlockEndingAfterTheRunItInterruptedDoesNotRestoreThatRunOnceItEnded() {
        val aStarted = CountDownLatch(1)
        val releaseA = CountDownLatch(1)
        val a = thread(name = "abandoned run") {
            runUltronUiTest("A") { aStarted.countDown(); releaseA.await(10, TimeUnit.SECONDS) }
        }
        assertTrue(aStarted.await(10, TimeUnit.SECONDS), "precondition: A is running")
        runUltronUiTest("B") {
            releaseA.countDown()
            a.join(10_000)
            events += "A ended while B runs:${!a.isAlive}"
        }
        assertEquals(listOf("A ended while B runs:true"), events)
        assertNull(UltronTestRunHooks.activeRun, "A already ended: no block is running")
    }

    /**
     * Run A hangs on its own thread (a JUnit timeout abandoned it, it never ends) when run B starts. B is not nested
     * in A: once B returns no block is running as far as `activeRun` goes, rather than the hung A, which would name
     * a ghost test opened by an `apply` outside any block.
     */
    @Test
    fun aBlockOverlappingAHungOneDoesNotGiveTheHungRunBackWhenItReturns() {
        val aStarted = CountDownLatch(1)
        val releaseA = CountDownLatch(1)
        val a = thread(name = "hung run") {
            runUltronUiTest("A") { aStarted.countDown(); releaseA.await(10, TimeUnit.SECONDS) }
        }
        try {
            assertTrue(aStarted.await(10, TimeUnit.SECONDS), "precondition: A is running")
            runUltronUiTest("B") { }
            assertNull(UltronTestRunHooks.activeRun, "the hung A is nobody's enclosing run")
        } finally {
            releaseA.countDown()
            a.join(10_000)
        }
    }

    /**
     * Run A hangs on its own thread while run B runs on another: each thread finds its own block, a thread that runs
     * no block finds none. This is how a reporter attributes an operation to the block it belongs to.
     */
    @OptIn(InternalUltronApi::class)
    @Test
    fun eachThreadFindsTheLiveRunItExecutes() {
        val aStarted = CountDownLatch(1)
        val releaseA = CountDownLatch(1)
        var runA: UltronTestRunHooks.ActiveRun? = null
        val seen = mutableListOf<String>()
        val a = thread(name = "hung run") {
            runUltronUiTest("A") {
                runA = UltronTestRunHooks.activeRun
                aStarted.countDown()
                releaseA.await(10, TimeUnit.SECONDS)
                seen += "A finds A:${UltronTestRunHooks.runOnCurrentThread() === runA}"
            }
        }
        try {
            assertTrue(aStarted.await(10, TimeUnit.SECONDS), "precondition: A is running")
            runUltronUiTest("B") {
                val runB = UltronTestRunHooks.activeRun
                seen += "B finds B:${UltronTestRunHooks.runOnCurrentThread() === runB}"
                var third: UltronTestRunHooks.ActiveRun? = runB
                thread(name = "no run") { third = UltronTestRunHooks.runOnCurrentThread() }.join(10_000)
                seen += "third finds none:${third == null}"
                releaseA.countDown()
                a.join(10_000)
            }
        } finally {
            releaseA.countDown()
            a.join(10_000)
        }
        assertEquals(listOf("B finds B:true", "third finds none:true", "A finds A:true"), seen)
        assertNull(UltronTestRunHooks.runOnCurrentThread(), "no block runs any more")
    }

    @OptIn(InternalUltronApi::class)
    @Test
    fun aNestedBlockIsTheRunOfItsThreadUntilItReturns() {
        runUltronUiTest("outer") {
            val outer = UltronTestRunHooks.runOnCurrentThread()
            runUltronUiTest("inner") { events += "inner:${UltronTestRunHooks.runOnCurrentThread()?.testName}" }
            events += "outer again:${UltronTestRunHooks.runOnCurrentThread() === outer}"
        }
        assertEquals(listOf("inner:inner", "outer again:true"), events)
    }

    @Test
    fun withoutHooksNothingIsRecorded() {
        runUltronUiTest("silent") { setContent { Text("x") } }
        assertTrue(events.isEmpty())
        assertNull(UltronTestRunHooks.current())
    }
}
