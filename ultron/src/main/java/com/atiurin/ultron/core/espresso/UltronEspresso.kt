@file:Suppress("DeprecatedCallableAddReplaceWith")

package com.atiurin.ultron.core.espresso

import android.content.Context
import androidx.test.espresso.Espresso
import androidx.test.espresso.PerformException
import androidx.test.platform.app.InstrumentationRegistry
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.action.EspressoActionExecutor
import com.atiurin.ultron.core.espresso.action.EspressoActionType
import com.atiurin.ultron.core.espresso.assertion.EspressoAssertionExecutor

object UltronEspresso {
    /** Closes soft keyboard if open. */
    fun closeSoftKeyboard() {
        executeUltronAction(
            UltronEspressoOperation(
                operationBlock = { Espresso.closeSoftKeyboard() },
                name = "Espresso.closeSoftKeyboard()",
                type = EspressoActionType.CLOSE_SOFT_KEYBOARD,
                description = "Espresso.closeSoftKeyboard() during ${UltronConfig.Espresso.ACTION_TIMEOUT} ms",
                timeoutMs = UltronConfig.Espresso.ACTION_TIMEOUT
            ),
            resultHandler = UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    /**
     * Press on the back button.
     *
     * @throws PerformException if currently displayed activity is root activity, since pressing back
     *     button would result in application closing.
     */
    fun pressBack() {
        executeUltronAction(
            UltronEspressoOperation(
                operationBlock = { Espresso.pressBack() },
                name = "Espresso.pressBack()",
                type = EspressoActionType.PRESS_BACK,
                description = "Espresso.pressBack() during ${UltronConfig.Espresso.ACTION_TIMEOUT} ms",
                timeoutMs = UltronConfig.Espresso.ACTION_TIMEOUT
            ),
            resultHandler = UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    /**
     * Opens the overflow menu displayed within an ActionBar.
     *
     * <p>This works with both native and SherlockActionBar ActionBars.
     *
     * <p>Note the significant differences of UX between ActionMode and ActionBars with respect to
     * overflows. If a hardware menu key is present, the overflow icon is never displayed in
     * ActionBars and can only be interacted with via menu key presses.
     */
    fun openActionBarOverflowOrOptionsMenu(context: Context = InstrumentationRegistry.getInstrumentation().targetContext) {
        executeUltronAction(
            UltronEspressoOperation(
                operationBlock = { Espresso.openActionBarOverflowOrOptionsMenu(context) },
                name = "Espresso.openActionBarOverflowOrOptionsMenu(context)",
                type = EspressoActionType.OPEN_ACTION_BAR_OVERFLOW_OR_OPTION_MENU,
                description = "Espresso.openActionBarOverflowOrOptionsMenu() during ${UltronConfig.Espresso.ACTION_TIMEOUT} ms",
                timeoutMs = UltronConfig.Espresso.ACTION_TIMEOUT
            ),
            resultHandler = UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    /**
     * Opens the overflow menu displayed in the contextual options of an ActionMode.
     *
     * <p>This works with both native and SherlockActionBar action modes.
     *
     * <p>Note the significant difference in UX between ActionMode and ActionBar overflows -
     * ActionMode will always present an overflow icon and that icon only responds to clicks. The menu
     * button (if present) has no impact on it.
     */
    fun openContextualActionModeOverflowMenu() {
        executeUltronAction(
            UltronEspressoOperation(
                operationBlock = { Espresso.openContextualActionModeOverflowMenu() },
                name = "Espresso.openContextualActionModeOverflowMenu()",
                type = EspressoActionType.OPEN_CONTEXTUAL_ACTION_MODE_OVERFLOW_MENU,
                description = "Espresso.openContextualActionModeOverflowMenu() during ${UltronConfig.Espresso.ACTION_TIMEOUT} ms",
                timeoutMs = UltronConfig.Espresso.ACTION_TIMEOUT
            ),
            resultHandler = UltronConfig.Espresso.ViewActionConfig.resultHandler
        )
    }

    /**
     * Executes any espresso action inside Ultron lifecycle
     */
    @Deprecated("It doesn't support withAssertion(). Consider usage of UltronEspressoInteraction().executeAction() instead")
    fun executeAction(
        operation: UltronEspressoOperation,
        resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit = UltronConfig.Espresso.ViewActionConfig.resultHandler
    ) {
        UltronEspressoOperationLifecycle.execute(EspressoActionExecutor(operation), resultHandler)
    }

    /**
     * Executes any espresso assertion inside Ultron lifecycle
     */
    @Deprecated("It doesn't support withAssertion(). Consider usage of UltronEspressoInteraction().executeAction() instead")
    fun executeAssertion(
        operation: UltronEspressoOperation,
        resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit = UltronConfig.Espresso.ViewAssertionConfig.resultHandler
    ) {
        UltronEspressoOperationLifecycle.execute(EspressoAssertionExecutor(operation), resultHandler)
    }

    private fun executeUltronAction(
        operation: UltronEspressoOperation,
        resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit = UltronConfig.Espresso.ViewActionConfig.resultHandler
    ) {
        UltronEspressoOperationLifecycle.execute(EspressoActionExecutor(operation), resultHandler)
    }
}