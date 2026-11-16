package com.atiurin.ultron.utils

internal actual fun qualifiedClassName(instance: Any): String? = instance::class.qualifiedName
