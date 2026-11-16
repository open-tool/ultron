package com.atiurin.sampleapp.tests.espresso

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.activity.MainActivity
import com.atiurin.sampleapp.pages.FriendsListPage
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.extensions.dismiss
import com.atiurin.ultron.extensions.doesNotExist
import com.atiurin.ultron.extensions.execute
import com.atiurin.ultron.extensions.isDisplayed
import com.atiurin.ultron.extensions.perform
import com.atiurin.ultron.extensions.scrollDown
import com.atiurin.ultron.extensions.scrollUp
import com.atiurin.ultron.extensions.withTimeout
import com.atiurin.ultron.testlifecycle.activity.UltronActivityRule
import com.google.android.material.snackbar.Snackbar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class NativeViewActionsTest : BaseTest() {
    private val list = withId(R.id.recycler_friends)

    init {
        ruleSequence.addLast(UltronActivityRule(MainActivity::class.java))
    }

    private fun firstVisiblePosition(): Int = list.execute { _, view ->
        ((view as RecyclerView).layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    }

    @Test
    fun scrollDown_scrollUp_matcher() {
        list.scrollDown()
        val afterDown = firstVisiblePosition()
        assertTrue("List wasn't scrolled down", afterDown > 0)
        list.scrollUp()
        assertTrue("List wasn't scrolled up", firstVisiblePosition() < afterDown)
    }

    @Test
    fun scrollDown_scrollUp_ultronRecyclerView() {
        FriendsListPage.recycler.scrollDown()
        val afterDown = firstVisiblePosition()
        assertTrue("List wasn't scrolled down", afterDown > 0)
        FriendsListPage.recycler.scrollUp()
        assertTrue("List wasn't scrolled up", firstVisiblePosition() < afterDown)
    }

    @Test
    fun scrollUp_atTopBoundary_fails() {
        val exception = assertThrows(Throwable::class.java) { list.withTimeout(1000).scrollUp() }
        assertTrue(exception.stackTraceToString(), exception.stackTraceToString().contains("cannot scroll vertically"))
    }

    @Test
    fun scrollDown_onListItem_doesNotScrollParentList() {
        val exception = assertThrows(Throwable::class.java) {
            FriendsListPage.recycler.firstItem().withTimeout(1000).scrollDown()
        }
        assertTrue(exception.stackTraceToString(), exception.stackTraceToString().contains("cannot scroll vertically"))
        assertEquals(0, firstVisiblePosition())
    }

    @Test
    fun dismiss_snackbar() {
        val text = "Native dismiss"
        list.perform { _, view -> Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE).show() }
        withText(text).isDisplayed()
        isAssignableFrom(Snackbar.SnackbarLayout::class.java).dismiss()
        withText(text).doesNotExist()
    }

    @Test
    fun dismiss_viewWithoutDismissAction_fails() {
        val exception = assertThrows(Throwable::class.java) { list.withTimeout(1000).dismiss() }
        assertTrue(exception.stackTraceToString(), exception.stackTraceToString().contains("does not expose accessibility action"))
    }
}
