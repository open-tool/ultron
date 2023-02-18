package com.atiurin.ultron.custom.espresso.base

import android.view.View
import androidx.test.espresso.DaggerBaseLayerComponent
import androidx.test.espresso.base.RootViewPicker
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import com.atiurin.ultron.custom.espresso.matcher.withSuitableRoot
import org.hamcrest.Matcher
import java.lang.reflect.Constructor
import java.util.concurrent.atomic.AtomicReference

fun createRootViewPicker(viewMatcher: Matcher<View>): RootViewPicker {
    val baseLayerComponent = DaggerBaseLayerComponent.create()
    val rootViewPickerClass: Class<*> = Class.forName("androidx.test.espresso.base.RootViewPicker")
    val rootViewPickerConstructor: Constructor<*> = rootViewPickerClass.declaredConstructors.first()
    val rootResultFetcherClass: Class<*> =
        rootViewPickerClass.declaredClasses.first { clazz -> clazz.simpleName.contains("RootResultFetcher") }
    val rootResultFetcherConstructor: Constructor<*> =
        rootResultFetcherClass.declaredConstructors.first()
    rootViewPickerConstructor.isAccessible = true
    rootResultFetcherConstructor.isAccessible = true
    return rootViewPickerConstructor.newInstance(
        baseLayerComponent.uiController(),
        rootResultFetcherConstructor.newInstance(
            baseLayerComponent.activeRootLister(),
            AtomicReference(withSuitableRoot(viewMatcher))
        ),
        ActivityLifecycleMonitorRegistry.getInstance(),
        AtomicReference(true),
        baseLayerComponent.controlledLooper()
    ) as RootViewPicker
}