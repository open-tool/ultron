package com.atiurin.ultron.custom.espresso.action

import android.view.View
import androidx.test.espresso.ViewAction
import com.atiurin.ultron.core.espresso.action.UltronEspressoActionParams
import org.hamcrest.Matcher

abstract class AnonymousViewAction(val params: UltronEspressoActionParams) : ViewAction {
    override fun getConstraints(): Matcher<View> = params.viewActionConstraints
    override fun getDescription(): String = params.viewActionDescription
}