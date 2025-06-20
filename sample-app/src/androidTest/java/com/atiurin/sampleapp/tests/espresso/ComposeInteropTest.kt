package com.atiurin.sampleapp.tests.espresso

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.activity.ComposeInteropActivity
import com.atiurin.sampleapp.activity.composeInViewTag
import com.atiurin.sampleapp.activity.viewInComposeTag
import com.atiurin.sampleapp.framework.Log
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.core.compose.createUltronComposeRule
import com.atiurin.ultron.core.compose.view.semanticsNodeInteraction
import com.atiurin.ultron.core.compose.view.onView
import com.atiurin.ultron.custom.espresso.action.getView
import com.atiurin.ultron.extensions.hasText
import org.junit.Rule
import org.junit.Test

class ComposeInteropTest : BaseTest() {

    private val activityTestRule = ActivityTestRule(ComposeInteropActivity::class.java)

    @get:Rule
    val composeRule = createUltronComposeRule<ComposeInteropActivity>()

    init {
        ruleSequence.addLast(activityTestRule)
    }

    @Test
    fun search_compose_in_view() {
        Log.time("Compose in View") {
            onView(withId(R.id.compose_in_view))
                .getView()
                .semanticsNodeInteraction(matcher = hasText(composeInViewTag))
                .assertIsDisplayed()
        }
    }

    @Test
    fun search_view_in_compose() {
        Log.time("View in Compose") {
            onView(withId(R.id.view_in_compose))
                .getView()
                .semanticsNodeInteraction(matcher = hasTestTag(viewInComposeTag))
                .onView()
                .hasText(viewInComposeTag)
                .isDisplayed()
        }
    }
}
