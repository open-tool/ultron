package com.atiurin.ultron.allure.runner

import com.atiurin.ultron.runner.*

class UltronAllureRunInformer : UltronRunInformer() {
    init {
        addListener(UltronLogRunListener())
        addListener(LogcatAttachRunListener())
        addListener(UltronLogAttachRunListener())
        addListener(UltronLogCleanerRunListener())
    }
}