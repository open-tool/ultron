package com.atiurin.ultron.exceptions

import java.lang.RuntimeException

class UltronUiAutomatorException(override val message: String) : RuntimeException(message)