package com.atiurin.ultron.exceptions

import java.lang.RuntimeException

class UltronAssertionException(override val message: String) : RuntimeException(message)