package com.atiurin.ultron.core.common.assertion

import com.atiurin.ultron.core.config.UltronCommonConfig
import com.atiurin.ultron.log.UltronLog

fun softAssertion(failOnException: Boolean = true, block: () -> Unit){
    UltronLog.info("Start soft assertion context")
    with(UltronCommonConfig.testContext){
        softAssertion = true
        block()
        softAssertion = false
        if (failOnException){
            softAnalyzer.verify()
        }
    }
    UltronLog.info("Finish soft assertion context")
}

fun verifySoftAssertions(){
    UltronCommonConfig.testContext.softAnalyzer.verify()
}