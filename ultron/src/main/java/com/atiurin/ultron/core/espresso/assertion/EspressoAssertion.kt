package com.atiurin.ultron.core.espresso.assertion

import android.view.View
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationType
import org.hamcrest.Matcher

interface EspressoAssertion : Operation {
    val matcher: Matcher<View>
    override val name: String
    override val type: OperationType
    override val description: String
    override val timeoutMs: Long
}