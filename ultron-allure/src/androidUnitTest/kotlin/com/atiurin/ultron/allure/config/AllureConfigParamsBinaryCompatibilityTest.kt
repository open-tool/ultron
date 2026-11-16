package com.atiurin.ultron.allure.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Modifier

/**
 * Pins the JVM signatures the former `data class AllureConfigParams` (2.6.6) had, so that Android test code
 * compiled against it keeps linking: the synthetic default-arguments constructor and the six-parameter
 * `copy$default`.
 */
class AllureConfigParamsBinaryCompatibilityTest {
    private val cls = AllureConfigParams::class.java
    private val set = java.util.Set::class.java
    private val bool = Boolean::class.javaPrimitiveType!!
    private val int = Int::class.javaPrimitiveType!!

    @Test
    fun `synthetic constructor with DefaultConstructorMarker keeps the six-parameter shape`() {
        val marker = Class.forName("kotlin.jvm.internal.DefaultConstructorMarker")
        val constructor = cls.getDeclaredConstructor(set, set, bool, bool, bool, bool, int, marker)
        assertTrue(constructor.isSynthetic)
    }

    @Test
    fun `the six-parameter constructor and the no-argument one are public`() {
        assertTrue(Modifier.isPublic(cls.getDeclaredConstructor(set, set, bool, bool, bool, bool).modifiers))
        assertTrue(Modifier.isPublic(cls.getDeclaredConstructor().modifiers))
    }

    @Test
    fun `copy$default of the six-parameter copy is still there`() {
        val method = cls.getDeclaredMethod("copy\$default", cls, set, set, bool, bool, bool, bool, int, Any::class.java)
        assertTrue(Modifier.isStatic(method.modifiers))
        assertEquals(cls, method.returnType)
    }

    @Test
    fun `the six-parameter copy is still there`() {
        assertEquals(cls, cls.getDeclaredMethod("copy", set, set, bool, bool, bool, bool).returnType)
    }
}
