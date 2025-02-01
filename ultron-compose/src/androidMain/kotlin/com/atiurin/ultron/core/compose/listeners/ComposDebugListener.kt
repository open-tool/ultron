package com.atiurin.ultron.core.compose.listeners

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.core.compose.ComposeTestContainer
import com.atiurin.ultron.core.compose.ComposeTestContainer.withComposeTestEnvironment
import com.atiurin.ultron.listeners.UltronLifecycleListener

class ComposDebugListener(private val advanceFrameAmount: Int = 10) : UltronLifecycleListener() {
    override fun after(operationResult: OperationResult<Operation>) {
        super.after(operationResult)
        if (android.os.Debug.isDebuggerConnected() && ComposeTestContainer.isInitialized){
            withComposeTestEnvironment { env ->
                repeat(advanceFrameAmount) {
                    env.mainClock.advanceTimeByFrame()
                }
            }
        }
    }
}