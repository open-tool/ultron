package com.atiurin.ultron.core.espresso.recyclerviewv2

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.ViewAction
import com.atiurin.ultron.core.common.assertion.DefaultOperationAssertion
import com.atiurin.ultron.core.common.assertion.OperationAssertion
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import com.atiurin.ultron.core.espresso.recyclerview.RecyclerViewItemExecutor
import com.atiurin.ultron.custom.espresso.matcher.withSuitableRoot
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.extensions.assertMatches
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.extensions.clickBottomCenter
import com.atiurin.ultron.extensions.clickBottomLeft
import com.atiurin.ultron.extensions.clickBottomRight
import com.atiurin.ultron.extensions.clickCenterLeft
import com.atiurin.ultron.extensions.clickCenterRight
import com.atiurin.ultron.extensions.clickTopCenter
import com.atiurin.ultron.extensions.clickTopLeft
import com.atiurin.ultron.extensions.clickTopRight
import com.atiurin.ultron.extensions.contentDescriptionContains
import com.atiurin.ultron.extensions.doubleClick
import com.atiurin.ultron.extensions.hasContentDescription
import com.atiurin.ultron.extensions.isClickable
import com.atiurin.ultron.extensions.isCompletelyDisplayed
import com.atiurin.ultron.extensions.isDisplayed
import com.atiurin.ultron.extensions.isDisplayingAtLeast
import com.atiurin.ultron.extensions.isEnabled
import com.atiurin.ultron.extensions.isNotClickable
import com.atiurin.ultron.extensions.isNotDisplayed
import com.atiurin.ultron.extensions.isNotEnabled
import com.atiurin.ultron.extensions.longClick
import com.atiurin.ultron.extensions.perform
import com.atiurin.ultron.extensions.swipeDown
import com.atiurin.ultron.extensions.swipeLeft
import com.atiurin.ultron.extensions.swipeRight
import com.atiurin.ultron.extensions.swipeUp
import com.atiurin.ultron.extensions.withAssertion
import com.atiurin.ultron.extensions.withResultHandler
import com.atiurin.ultron.extensions.withTimeout
import com.atiurin.ultron.listeners.setListenersState
import org.hamcrest.Matcher

open class UltronRecyclerViewItemV2 {
    private var executor: RecyclerViewItemExecutor? = null

    /**
     * Use this constructor to inherit from [UltronRecyclerViewItemV2]
     * Don't create an instance of subclass. Use [UltronRecyclerViewV2.getItem] instead
     */
    protected constructor()

    fun child(block: () -> Matcher<View>): Lazy<Matcher<View>> = lazy {
        getChild(block())
    }

    constructor(
        ultronRecyclerView: UltronRecyclerViewV2,
        itemViewMatcher: Matcher<View>,
        autoScroll: Boolean = true,
        scrollOffset: Int = 0
    ) {
        setExecutor(ultronRecyclerView, itemViewMatcher)
        if (autoScroll) scrollToItem(scrollOffset)
    }

    constructor(
        ultronRecyclerView: UltronRecyclerViewV2,
        position: Int,
        autoScroll: Boolean = true,
        scrollOffset: Int = 0
    ) {
        setExecutor(ultronRecyclerView, position)
        if (autoScroll) scrollToItem(scrollOffset)
    }

    fun scrollToItem(offset: Int = 0): UltronRecyclerViewItemV2 = apply {
        executor?.scrollToItem(offset)
    }

    fun getViewHolder(): RecyclerView.ViewHolder? {
        return executor?.getItemViewHolder()
    }

    /**
     * @return matcher to a child element
     */
    fun getChild(childMatcher: Matcher<View>): Matcher<View> {
        if (executor == null) throw UltronException(
            """
            |UltronRecyclerViewItemV2 child element should have lazy initialisation in subclasses. 
            |For example, do not use: 
            |val name = getChild(matcher)
            |Refactor it to:
            |val name by lazy { getChild(matcher) }
        """.trimMargin()
        )
        return executor!!.getItemChildMatcher(childMatcher)
    }

    fun withTimeout(timeoutMs: Long) = getMatcher().withTimeout(timeoutMs)
    fun withResultHandler(resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit) =
        getMatcher().withResultHandler(resultHandler)

    fun withAssertion(assertion: OperationAssertion) = getMatcher().withAssertion(assertion)
    fun withAssertion(name: String = "", isListened: Boolean = false, block: () -> Unit) =
        getMatcher().withAssertion(DefaultOperationAssertion(name, block.setListenersState(isListened)))

    //root view searching
    fun withSuitableRoot() = apply { this.getMatcher().withSuitableRoot() }

    //actions
    fun click() = apply { this.getMatcher().click() }
    fun longClick() = apply { this.getMatcher().longClick() }
    fun doubleClick() = apply { this.getMatcher().doubleClick() }

    fun clickTopLeft(offsetX: Int = 0, offsetY: Int = 0) = apply { this.getMatcher().clickTopLeft(offsetX, offsetY) }
    fun clickTopCenter(offsetY: Int) = apply { this.getMatcher().clickTopCenter(offsetY) }
    fun clickTopRight(offsetX: Int = 0, offsetY: Int = 0) = apply { this.getMatcher().clickTopRight(offsetX, offsetY) }
    fun clickCenterRight(offsetX: Int = 0) = apply { this.getMatcher().clickCenterRight(offsetX) }
    fun clickBottomRight(offsetX: Int = 0, offsetY: Int = 0) = apply { this.getMatcher().clickBottomRight(offsetX, offsetY) }
    fun clickBottomCenter(offsetY: Int = 0) = apply { this.getMatcher().clickBottomCenter(offsetY) }
    fun clickBottomLeft(offsetX: Int = 0, offsetY: Int = 0) = apply { this.getMatcher().clickBottomLeft(offsetX, offsetY) }
    fun clickCenterLeft(offsetX: Int = 0) = apply { this.getMatcher().clickCenterLeft(offsetX) }

    fun swipeDown() = apply { this.getMatcher().swipeDown() }
    fun swipeLeft() = apply { this.getMatcher().swipeLeft() }
    fun swipeRight() = apply { this.getMatcher().swipeRight() }
    fun swipeUp() = apply { this.getMatcher().swipeUp() }
    fun perform(action: ViewAction) = apply { this.getMatcher().perform(action) }

    //assertions
    fun isDisplayed() = apply { this.getMatcher().isDisplayed() }
    fun isNotDisplayed() = apply { this.getMatcher().isNotDisplayed() }
    fun isCompletelyDisplayed() = apply { this.getMatcher().isCompletelyDisplayed() }
    fun isDisplayingAtLeast(percentage: Int) =
        apply { this.getMatcher().isDisplayingAtLeast(percentage) }

    fun isClickable() = apply { this.getMatcher().isClickable() }
    fun isNotClickable() = apply { this.getMatcher().isNotClickable() }
    fun isEnabled() = apply { this.getMatcher().isEnabled() }
    fun isNotEnabled() = apply { this.getMatcher().isNotEnabled() }
    fun assertMatches(condition: Matcher<View>) =
        apply { this.getMatcher().assertMatches(condition) }

    fun hasContentDescription(contentDescription: String) =
        apply { this.getMatcher().hasContentDescription(contentDescription) }

    fun hasContentDescription(resourceId: Int) =
        apply { this.getMatcher().hasContentDescription(resourceId) }

    fun hasContentDescription(charSequenceMatcher: Matcher<CharSequence>) =
        apply { this.getMatcher().hasContentDescription(charSequenceMatcher) }

    fun contentDescriptionContains(text: String) =
        apply { this.getMatcher().contentDescriptionContains(text) }

    fun setExecutor(
        ultronRecyclerView: UltronRecyclerViewV2,
        itemViewMatcher: Matcher<View>
    ) {
        this.executor = RecyclerViewItemMatchingExecutorV2(ultronRecyclerView, itemViewMatcher)
    }

    fun setExecutor(
        ultronRecyclerView: UltronRecyclerViewV2,
        position: Int
    ) {
        this.executor = RecyclerViewItemPositionalExecutorV2(ultronRecyclerView, position)
    }

    fun getMatcher(): Matcher<View> {
        return executor!!.getItemMatcher()
    }

    companion object {
        inline fun <reified T : UltronRecyclerViewItemV2> getInstance(
            ultronRecyclerView: UltronRecyclerViewV2,
            itemViewMatcher: Matcher<View>,
            autoScroll: Boolean = true,
            scrollOffset: Int = 0
        ): T {
            val item = this.createUltronRecyclerViewItemInstance<T>()
            item.setExecutor(ultronRecyclerView, itemViewMatcher)
            if (autoScroll) item.scrollToItem(scrollOffset)
            return item
        }

        inline fun <reified T : UltronRecyclerViewItemV2> getInstance(
            ultronRecyclerView: UltronRecyclerViewV2,
            position: Int,
            autoScroll: Boolean = true,
            scrollOffset: Int = 0
        ): T {
            val item = this.createUltronRecyclerViewItemInstance<T>()
            item.setExecutor(ultronRecyclerView, position)
            if (autoScroll) item.scrollToItem(scrollOffset)
            return item
        }

        inline fun <reified T : UltronRecyclerViewItemV2> createUltronRecyclerViewItemInstance(): T {
            return try {
                T::class.java.newInstance()
            } catch (ex: Exception) {
                val desc = when {
                    T::class.isInner -> {
                        "${T::class.simpleName} is an inner class so you have to delete inner modifier (It is often when kotlin throws 'has no zero argument constructor' but real reason is an inner modifier)"
                    }
                    T::class.constructors.find { it.parameters.isEmpty() } == null -> {
                        "${T::class.simpleName} doesn't have a constructor without params (create an empty constructor)"
                    }
                    else -> ex.message
                }
                throw UltronException(
                    """
                    |Couldn't create an instance of ${T::class.simpleName}. 
                    |Possible reason: $desc 
                    |Original exception: ${ex.message}, cause ${ex.cause}
                """.trimMargin()
                )
            }
        }
    }
}