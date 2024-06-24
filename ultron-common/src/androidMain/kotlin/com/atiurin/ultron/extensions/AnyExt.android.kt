package com.atiurin.ultron.extensions

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

fun Class<*>.isAssignedFrom(klasses: List<Class<*>>): Boolean{
    klasses.forEach {
        if (it.isAssignableFrom(this)) return true
    }
    return false
}