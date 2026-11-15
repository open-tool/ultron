package com.atiurin.ultron.core.test

import com.atiurin.ultron.core.test.context.UltronTestContext
import java.lang.reflect.Modifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Pins the JVM signatures that code compiled against Ultron 2.6.6 links to, so that a later change of
 * `UltronTest.test` or `TestMethod` cannot silently drop them.
 */
class UltronTestBinaryCompatibilityTest {
    private val function1 = Class.forName("kotlin.jvm.functions.Function1")
    private val bool = Boolean::class.javaPrimitiveType!!
    private val int = Int::class.javaPrimitiveType!!

    @Test
    fun `test(ZZLFunction1)V is still there`() {
        val method = UltronTest::class.java.getDeclaredMethod("test", bool, bool, function1)
        assertEquals(Void.TYPE, method.returnType)
        assertTrue(Modifier.isPublic(method.modifiers))
    }

    @Test
    fun `test$default of the three-parameter test is still there`() {
        val method = UltronTest::class.java.getDeclaredMethod(
            "test\$default", UltronTest::class.java, bool, bool, function1, int, Any::class.java
        )
        assertTrue(Modifier.isStatic(method.modifiers))
    }

    @Test
    fun `the four-parameter test and its test$default exist`() {
        UltronTest::class.java.getDeclaredMethod("test", bool, bool, String::class.java, function1)
        UltronTest::class.java.getDeclaredMethod(
            "test\$default", UltronTest::class.java, bool, bool, String::class.java, function1, int, Any::class.java
        )
    }

    @Test
    fun `TestMethod(UltronTestContext) constructor is still there`() {
        val constructor = TestMethod::class.java.getDeclaredConstructor(UltronTestContext::class.java)
        assertTrue(Modifier.isPublic(constructor.modifiers))
    }
}
