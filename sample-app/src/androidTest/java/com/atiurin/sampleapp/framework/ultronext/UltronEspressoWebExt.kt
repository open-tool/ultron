package com.atiurin.sampleapp.framework.ultronext

import androidx.test.espresso.web.webdriver.DriverAtoms
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement

// add action on wenView
fun UltronWebElement.appendText(text: String) = apply {
    executeOperation(
        getUltronWebActionOperation(
            webInteractionBlock = {
                webInteractionBlock().perform(DriverAtoms.webKeys(text))
            },
            name = "${elementInfo.name} appendText '$text'",
            description = "${elementInfo.name} appendText '$text' during $timeoutMs ms"
        )
    )
}