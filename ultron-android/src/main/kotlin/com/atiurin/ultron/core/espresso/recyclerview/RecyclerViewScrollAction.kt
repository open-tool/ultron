package com.atiurin.ultron.core.espresso.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.atiurin.ultron.exceptions.UltronOperationException
import com.atiurin.ultron.extensions.instantScrollToPosition
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

internal class RecyclerViewScrollAction(
    private val itemMatcher: Matcher<View>,
    private val itemSearchLimit: Int = -1,
    private val offset: Int = 0
) : ViewAction {

    override fun getConstraints(): Matcher<View> {
        return allOf(
            isAssignableFrom(RecyclerView::class.java),
            isDisplayed()
        )
    }

    override fun getDescription(): String {
        return "Scroll RecyclerView to item $itemMatcher${if (itemSearchLimit >= 0) " with offset = $offset and search limit = $itemSearchLimit" else ""}"
    }

    override fun perform(uiController: UiController, view: View) {

        val recyclerView = view as RecyclerView
        val viewHolderMatcher: Matcher<RecyclerView.ViewHolder> = viewHolderMatcher(itemMatcher)

        val matchedItem =
            itemsMatching(recyclerView, viewHolderMatcher, 1, itemSearchLimit).firstOrNull()
                ?: throw UltronOperationException("The scroll action could not be performed because no matching element was found using the matcher: $itemMatcher")

        val finalPositionToScroll = matchedItem.position + offset

        recyclerView.instantScrollToPosition(finalPositionToScroll, 0.5f)
        runBlocking { delay(10) }
    }
}