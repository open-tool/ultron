package com.atiurin.ultron.extensions

import android.view.View
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Root
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.base.ViewFinderImpl
import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.sugar.Web
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiObject2
import org.hamcrest.Matcher
import java.util.concurrent.atomic.AtomicReference

internal fun DataInteraction.getMatcher(propertyName: String): Matcher<View>? {
    return this.getProperty(propertyName)
}

internal fun DataInteraction.getRootMatcher(): Matcher<Root>? {
    return this.getProperty("rootMatcher")
}

internal fun DataInteraction.getDataMatcher(): Matcher<View>? {
    return this.getProperty("dataMatcher")
}

internal fun DataInteraction.getTargetMatcher(): Matcher<View>? {
    return this.getMethodResult("makeTargetMatcher")
}

internal fun ViewInteraction.getMatcher(propertyName: String): Matcher<View>? {
    return this.getProperty(propertyName)
}

internal fun ViewInteraction.getViewMatcher(): Matcher<View>? {
    return this.getProperty("viewMatcher")
}

internal fun ViewInteraction.getRootMatcherRef(): AtomicReference<Matcher<Root>>? {
    return this.getProperty("rootMatcherRef")
}

internal fun ViewInteraction.getRootMatcher(): Matcher<Root>? {
    return this.getRootMatcherRef()?.get()
}

internal fun ViewInteraction.getViewFinder(): ViewFinderImpl? {
    return this.getProperty("viewFinder")
}

internal fun <T> Web.WebInteraction<T>.getViewMatcher(): Matcher<View>? {
    return this.getProperty("viewMatcher")
}
internal fun <T> Web.WebInteraction<T>.getElementReference(): ElementReference? {
    return this.getProperty("element")
}

fun UiObject2.getBySelector(): BySelector? = this.getProperty<BySelector>("mSelector")

