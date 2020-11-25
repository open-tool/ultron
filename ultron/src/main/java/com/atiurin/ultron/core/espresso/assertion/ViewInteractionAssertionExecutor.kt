package com.atiurin.ultron.core.espresso.assertion

import com.atiurin.ultron.core.espresso.EspressoOperationExecutor

open class ViewInteractionAssertionExecutor(
    val assertion: EspressoAssertion
) : EspressoOperationExecutor(assertion)