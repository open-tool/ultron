package com.atiurin.ultron.allure.testutil

import com.atiurin.ultron.allure.lifecycle.AllureLifecycle
import com.atiurin.ultron.allure.model.AllureJson
import com.atiurin.ultron.allure.model.AllureTestResult
import com.atiurin.ultron.allure.video.ApngFrame
import com.atiurin.ultron.allure.video.EncodedVideo
import com.atiurin.ultron.allure.video.VideoEncoder
import com.atiurin.ultron.allure.video.VideoOptions
import com.atiurin.ultron.allure.writer.AllureResultsWriter
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.atiurin.ultron.allure.writer.InMemoryResultsSink
import com.atiurin.ultron.core.common.ElementInfo
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.core.common.UltronOperationType
import com.atiurin.ultron.core.common.assertion.OperationAssertion
import com.atiurin.ultron.core.common.assertion.EmptyOperationAssertion

class ManualClock(var now: Long = 1_000) {
    fun tick(ms: Long = 10): Long { now += ms; return now }
}

class Harness(val clock: ManualClock = ManualClock()) {
    val sink = InMemoryResultsSink()
    val lifecycle = AllureLifecycle(AllureResultsWriter(sink)) { clock.now }

    fun results(): List<AllureTestResult> = sink.snapshot()
        .filter { it.key.endsWith("-result.json") }
        .map { AllureJson.format.decodeFromString(AllureTestResult.serializer(), it.value.decodeToString()) }

    fun single(): AllureTestResult = results().single()
}

/** Answers at once with fixed bytes; counts the calls. */
class FakeMp4Encoder(val bytes: ByteArray = byteArrayOf(1, 2, 3)) : VideoEncoder {
    var calls = 0
    override suspend fun encode(frames: List<ApngFrame>, options: VideoOptions): EncodedVideo {
        calls++
        return EncodedVideo(bytes, "mp4", "video/mp4")
    }
}

class FailingVideoEncoder(private val failure: Throwable = IllegalStateException("codec exploded")) : VideoEncoder {
    var calls = 0
    override suspend fun encode(frames: List<ApngFrame>, options: VideoOptions): EncodedVideo {
        calls++
        throw failure
    }
}

/** Suspends like a browser encoder does; the test resumes it by hand. */
class SuspendingVideoEncoder(val bytes: ByteArray = byteArrayOf(9, 9)) : VideoEncoder {
    var calls = 0
    private var continuation: Continuation<EncodedVideo>? = null
    val suspended: Boolean get() = continuation != null

    override suspend fun encode(frames: List<ApngFrame>, options: VideoOptions): EncodedVideo {
        calls++
        return suspendCoroutine { continuation = it }
    }

    fun resume() = checkNotNull(continuation).resume(EncodedVideo(bytes, "mp4", "video/mp4"))
    fun fail(failure: Throwable) = checkNotNull(continuation).resumeWithException(failure)
}

enum class FakeType : UltronOperationType { CLICK }

class FakeOperation(
    override val name: String,
    override val type: UltronOperationType = FakeType.CLICK,
) : Operation {
    override val description: String = "desc of $name"
    override val timeoutMs: Long = 1
    override val assertion: OperationAssertion = EmptyOperationAssertion()
    override val elementInfo: ElementInfo = object : ElementInfo { override var name = "el"; override var meta: Any? = null; override fun copy(): ElementInfo = this }
    override fun execute(): OperationIterationResult = throw UnsupportedOperationException()
}

class FakeResult(
    override val operation: Operation,
    val throwable: Throwable? = null,
) : OperationResult<Operation> {
    override val success: Boolean = throwable == null
    override val exceptions: List<Throwable> = listOfNotNull(throwable)
    override var description: String = ""
    override var operationIterationResult: OperationIterationResult? = object : OperationIterationResult {
        override val success = throwable == null
        override val exception = throwable
    }
    override val executionTimeMs: Long = 1
}
