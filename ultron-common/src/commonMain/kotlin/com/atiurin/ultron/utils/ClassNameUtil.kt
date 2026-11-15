package com.atiurin.ultron.utils

/**
 * The qualified class name of [instance] where the platform's reflection provides one (JVM, Native);
 * `null` on js/wasmJs, whose `KClass` has no qualified names (a compile error on Kotlin/Wasm).
 */
internal expect fun qualifiedClassName(instance: Any): String?
