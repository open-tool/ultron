package com.atiurin.ultron.custom.espresso.base

import org.hamcrest.Matcher

internal fun <T> filter(iterable: Iterable<T>, matcher: Matcher<T>): Iterable<T> {
    return iterable.filter { matcher.matches(it) }
}

internal fun <T> filterToList(iterable: Iterable<T>, matcher: Matcher<T>): List<T> {
    return filter(iterable, matcher).toList()
}

internal fun joinToString(iterable: Iterable<Any>, delimiter: String): String {
    return iterable.joinToString(separator = delimiter)
}

internal fun <T> toArray(iterator: Iterator<T>, clazz: Class<T>): Array<T> {
    val arrayList = ArrayList<T>()
    while (iterator.hasNext()) {
        arrayList.add(iterator.next())
    }
    return arrayList.toArray(
        java.lang.reflect.Array.newInstance(
            clazz,
            arrayList.size
        ) as Array<T>
    )
}