package com.atiurin.ultron.testlifecycle.setupteardown

data class Condition(val counter: Int, val key: String, val name: String = "", val actions: () -> Unit)