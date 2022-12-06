package com.atiurin.ultron.extensions

import android.view.View
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Root
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.sugar.Web
import org.hamcrest.Matcher
import java.util.concurrent.atomic.AtomicReference

inline fun <reified T> Any.getProperty(propertyName: String): T? {
    return try {
        val property = this.javaClass.getDeclaredField(propertyName)
        property.isAccessible = true
        property.get(this) as T
    } catch (ex: Throwable) { null }
}

inline fun <reified T> Any.getMethodResult(methodName: String, vararg args: Any?): T? {
    return try {
        val method = this.javaClass.getDeclaredMethod(methodName)
        method.isAccessible = true
        method.invoke(this, *args) as T
    } catch (ex: Throwable) { null }
}

internal fun Class<*>.isAssignedFrom(klasses: List<Class<*>>): Boolean{
    klasses.forEach {
        if (it.isAssignableFrom(this)) return true
    }
    return false
}

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

internal fun <T> Web.WebInteraction<T>.getViewMatcher(): Matcher<View>? {
    return this.getProperty("viewMatcher")
}
internal fun <T> Web.WebInteraction<T>.getElementReference(): ElementReference? {
    return this.getProperty("element")
}
internal fun  SemanticsNodeInteraction.getUseMergedTree(): Boolean? {
    return this.getProperty("useUnmergedTree")
}
internal fun  SemanticsNodeInteractionCollection.getUseMergedTree(): Boolean? {
    return this.getProperty("useUnmergedTree")
}
