package com.atiurin.ultron.extensions

import android.view.View
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Root
import androidx.test.espresso.ViewInteraction
import org.hamcrest.Matcher
import java.util.concurrent.atomic.AtomicReference

internal inline operator fun <reified T> Any.get(propertyName: String): T? {
    return try {
        val property = this.javaClass.getDeclaredField(propertyName)
        property.isAccessible = true
        property.get(this) as T
    } catch (ex: Throwable) { null }
}

internal fun Class<*>.isAssignedFrom(klasses: List<Class<*>>): Boolean{
    klasses.forEach {
        if (it.isAssignableFrom(this)) return true
    }
    return false
}

internal fun DataInteraction.getMatcher(propertyName: String): Matcher<View>? {
    return this[propertyName]
}

internal fun DataInteraction.getRootMatcher(): Matcher<View>? {
    return this["rootMatcher"]
}

internal fun DataInteraction.getDataMatcher(): Matcher<View>? {
    return this["dataMatcher"]
}

internal fun ViewInteraction.getMatcher(propertyName: String): Matcher<View>? {
    return this[propertyName]
}

internal fun ViewInteraction.getViewMatcher(): Matcher<View>? {
    return this["viewMatcher"]
}

internal fun ViewInteraction.getRootMatcherRef(): AtomicReference<Matcher<Root>>? {
    return this["rootMatcherRef"]
}

internal fun ViewInteraction.getRootMatcher(): Matcher<Root>? {
    return this.getRootMatcherRef()?.get()
}