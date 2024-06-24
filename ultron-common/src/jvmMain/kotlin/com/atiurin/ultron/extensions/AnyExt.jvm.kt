package com.atiurin.ultron.extensions

inline fun <reified T> Any.getProperty(propertyName: String): T? {
    return try {
        val property = this.javaClass.getDeclaredField(propertyName)
        property.isAccessible = true
        property.get(this) as T
    } catch (ex: Throwable) { null }
}