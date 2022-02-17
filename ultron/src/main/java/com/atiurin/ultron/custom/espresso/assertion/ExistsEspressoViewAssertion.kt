package com.atiurin.ultron.custom.espresso.assertion

import android.view.View
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import com.atiurin.ultron.exceptions.UltronException

class ExistsEspressoViewAssertion : ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (view == null){
            val ex = noViewFoundException ?: UltronException("View does not exist in hierarchy")
            throw ex
        }
    }
}