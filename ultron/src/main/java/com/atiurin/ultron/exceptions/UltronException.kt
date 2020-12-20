package com.atiurin.ultron.exceptions

import java.lang.RuntimeException

class UltronException(override val message: String) : RuntimeException(message)