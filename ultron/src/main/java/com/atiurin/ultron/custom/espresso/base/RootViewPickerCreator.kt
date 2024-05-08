package com.atiurin.ultron.custom.espresso.base

import android.view.View
import androidx.test.espresso.base.RootViewPicker
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import com.atiurin.ultron.core.config.UltronConfig.Espresso
import com.atiurin.ultron.custom.espresso.matcher.withSuitableRoot
import org.hamcrest.Matcher
import java.lang.reflect.Constructor
import java.util.concurrent.atomic.AtomicReference

fun createRootViewPicker(viewMatcher: Matcher<View>): RootViewPicker {
    val rootViewPickerClass: Class<*> = Class.forName("androidx.test.espresso.base.RootViewPicker")
    val rootViewPickerConstructor: Constructor<*> = rootViewPickerClass.declaredConstructors.first()
    val rootResultFetcherClass: Class<*> =
        rootViewPickerClass.declaredClasses.first { clazz -> clazz.simpleName.contains("RootResultFetcher") }
    val rootResultFetcherConstructor: Constructor<*> =
        rootResultFetcherClass.declaredConstructors.first()
    rootViewPickerConstructor.isAccessible = true
    rootResultFetcherConstructor.isAccessible = true
    val rootResultFetcher = rootResultFetcherConstructor.newInstance(
        Espresso.activeRootLister,
        AtomicReference(withSuitableRoot(viewMatcher))
    )

    val commonArgs = arrayOf(
        Espresso.uiController,
        rootResultFetcher,
        ActivityLifecycleMonitorRegistry.getInstance(),
        AtomicReference(true),
        Espresso.controlledLooper
    )

    var rootViewPicker: RootViewPicker? = null
    runCatching {
        rootViewPicker = rootViewPickerConstructor.newInstance(*commonArgs) as RootViewPicker
    }.onFailure {
        val argsWithTargetContext = commonArgs + InstrumentationRegistry.getInstrumentation().targetContext
        rootViewPicker = rootViewPickerConstructor.newInstance(*argsWithTargetContext) as RootViewPicker
    }

    return rootViewPicker ?: throw IllegalStateException("RootViewPicker is not created")
}