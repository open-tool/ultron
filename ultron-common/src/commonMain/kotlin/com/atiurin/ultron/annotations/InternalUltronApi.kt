package com.atiurin.ultron.annotations

/**
 * Public only so that the other Ultron modules can share it: not part of the API, may change or disappear in any
 * release. Code outside Ultron must not opt in.
 */
@RequiresOptIn(message = "Internal Ultron API, shared between Ultron modules only", level = RequiresOptIn.Level.ERROR)
@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
annotation class InternalUltronApi
