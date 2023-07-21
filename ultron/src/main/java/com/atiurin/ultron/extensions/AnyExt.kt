package com.atiurin.ultron.extensions

fun Any?.simpleClassName() = this?.let { it::class.simpleName }