package com.atiurin.ultron.core.compose.view

import android.os.Looper
import androidx.compose.ui.node.RootForTest
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.test.AndroidComposeUiTestEnvironment
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.InternalTestApi
import androidx.compose.ui.test.MainTestClock
import androidx.compose.ui.test.TestOwner
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.platform.app.InstrumentationRegistry
import com.atiurin.ultron.core.compose.ComposeRuleContainer
import com.atiurin.ultron.extensions.getMethodResult
import com.atiurin.ultron.extensions.getProperty
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask

@OptIn(InternalTestApi::class)
internal class ViewAndroidTestOwner(private val composeView: AbstractComposeView) : TestOwner {

    override val mainClock: MainTestClock
        get() = ComposeRuleContainer.getComposeRule().mainClock

    override fun <T> runOnUiThread(action: () -> T): T {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return action()
        }

        val task: FutureTask<T> = FutureTask(action)
        InstrumentationRegistry.getInstrumentation().runOnMainSync(task)
        try {
            return task.get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    @OptIn(ExperimentalTestApi::class)
    override fun getRoots(atLeastOneRootExpected: Boolean): Set<RootForTest> {
        val rule = ComposeRuleContainer.getComposeRule() as AndroidComposeTestRule<*, *>
        val env = rule.getProperty<AndroidComposeUiTestEnvironment<*>>("environment")
        if (env != null) {
            env.getMethodResult<Unit?>("waitForIdle", atLeastOneRootExpected)
        } else {
            ComposeRuleContainer.getComposeRule().waitForIdle()
        }
        return setOf(composeView.getChildAt(0) as RootForTest)
    }
}