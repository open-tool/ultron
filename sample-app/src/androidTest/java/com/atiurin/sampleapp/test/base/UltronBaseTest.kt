package com.atiurin.sampleapp.test.base

import androidx.test.rule.GrantPermissionRule
import com.atiurin.sampleapp.framework.UltronCustomListener
import com.atiurin.ultron.allure.config.UltronAllureConfig
import com.atiurin.ultron.allure.listeners.DetailedOperationAllureListener
import com.atiurin.ultron.allure.listeners.ScreenshotAttachListener
import com.atiurin.ultron.allure.listeners.WindowHierarchyAttachListener
import com.atiurin.ultron.core.compose.config.UltronComposeConfig
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.testlifecycle.rulesequence.RuleSequence
import org.junit.BeforeClass
import org.junit.Rule

open class UltronBaseTest {
    private val permissionRule: GrantPermissionRule = GrantPermissionRule.grant()

    @get:Rule
    val ruleSequence = RuleSequence().addFirst(permissionRule)

    companion object {
        @BeforeClass
        @JvmStatic
        fun config(){
            UltronConfig.applyRecommended()
            UltronConfig.addGlobalListener(UltronCustomListener())
            UltronComposeConfig.addListener(UltronCustomListener())
            UltronAllureConfig.applyRecommended()
            UltronComposeConfig.applyRecommended()
            UltronComposeConfig.addListener(ScreenshotAttachListener())
            UltronComposeConfig.addListener(WindowHierarchyAttachListener())
            UltronComposeConfig.addListener(DetailedOperationAllureListener())
        }
    }
}
