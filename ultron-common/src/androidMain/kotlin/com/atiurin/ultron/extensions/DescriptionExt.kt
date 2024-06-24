package com.atiurin.ultron.extensions

import org.junit.runner.Description

fun Description.fullTestName() = "'${this.className}.${this.methodName}'"