package com.atiurin.sampleapp.framework.ultronext

import androidx.test.espresso.web.webdriver.DriverAtoms
import com.atiurin.ultron.core.espressoweb.operation.EspressoWebOperationType
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement

// add action on wenView
fun UltronWebElement.appendText(text: String) = apply {
    executeOperation(
        getUltronWebActionOperation(
            webInteractionBlock = {
                webInteractionBlock().perform(DriverAtoms.webKeys(text))
            },
            name = "WebElement(${locator.type} = '$value') appendText '$text'",
            description = "WebElement(${locator.type} = '$value') appendText '$text' during $timeoutMs ms"
        )
    )
}