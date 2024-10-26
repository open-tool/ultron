package com.atiurin.ultron.core.common.assertion

import com.atiurin.ultron.core.config.UltronCommonConfig
import com.atiurin.ultron.log.UltronLog

fun softAssertion(failOnExceptions: Boolean = true, block: () -> Unit){
    UltronLog.info("Start soft assertion context")
    UltronCommonConfig.isSoftAssertion = true
    block()
    UltronCommonConfig.isSoftAssertion = false
    if (failOnExceptions) {
        UltronCommonConfig.softAnalyzer.throwIfCaughtExceptions()
    }
    UltronLog.info("Finish soft assertion context")
}

fun verifySoftAssertions(){
    UltronCommonConfig.softAnalyzer.throwIfCaughtExceptions()
}