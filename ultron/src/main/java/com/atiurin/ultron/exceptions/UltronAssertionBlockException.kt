package com.atiurin.ultron.exceptions

import java.lang.RuntimeException

class UltronAssertionBlockException(override val message: String) : RuntimeException(message)