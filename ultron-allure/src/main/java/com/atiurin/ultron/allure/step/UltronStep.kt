package com.atiurin.ultron.allure.step

import io.qameta.allure.kotlin.Allure

inline fun <T> step (description: String, crossinline block: () -> T): T {
    return Allure.step(description) {
        block()
    }
}