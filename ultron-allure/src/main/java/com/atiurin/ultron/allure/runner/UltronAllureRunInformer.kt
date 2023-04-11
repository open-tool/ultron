package com.atiurin.ultron.allure.runner

import com.atiurin.ultron.runner.AbstractRunInformer
import com.atiurin.ultron.runner.UltronLogRunListener
import com.atiurin.ultron.runner.UltronRunListener

class UltronAllureRunInformer : AbstractRunInformer() {
    override val listeners: MutableList<UltronRunListener> = mutableListOf(
        UltronLogRunListener(),
        AllureLogAttachRunListener()
    )
}