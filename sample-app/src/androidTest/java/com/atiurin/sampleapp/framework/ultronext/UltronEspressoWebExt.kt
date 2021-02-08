package com.atiurin.sampleapp.framework.ultronext

import androidx.test.espresso.web.model.Evaluation
import androidx.test.espresso.web.webdriver.DriverAtoms
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espressoweb.operation.EspressoWebOperationType
import com.atiurin.ultron.core.espressoweb.operation.WebInteractionOperation
import com.atiurin.ultron.core.espressoweb.operation.WebOperationResult
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebDocument
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement

// add action on wenView
fun UltronWebElement.appendText(text: String) = apply {
    UltronWebDocument.executeOperation(
        webInteractionBlock = {
            webInteractionBlock().perform(DriverAtoms.webKeys(text))
        },
        name = " WebElement(${locator.type} = '$value') ReplaceText to '$text'",
        type = EspressoWebOperationType.WEB_REPLACE_TEXT,
        description = " WebElement(${locator.type} = '$value') ReplaceText to '$text' during $timeoutMs ms",
        timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
        resultHandler as (WebOperationResult<WebInteractionOperation<Evaluation>>) -> Unit
    )
}