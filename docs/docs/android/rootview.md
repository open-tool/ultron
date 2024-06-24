---
sidebar_position: 5
---

# withSuitableRoot

Method allows to avoiding nontrivial element lookup exceptions

In some cases, we encounter non-trivial exceptions in finding elements that are part of the Espresso framework. Such problems and their solution will be considered.

# Waited for the root of the view hierarchy to have window focus and not request layout for 10 seconds.

If you observe such an exception, then this indicates a complex problem for testing the user interface. One of the well-known reasons is that programmers add their views to the application context, and not to the activity or fragment. At phase of view interaction creation, Espresso assigns a root view where your matcher will be matched. Unfortunately, the views attached to the application context may not have the same root view that was set at the time view interaction was created. To solve this problem, the following solution was created:

```kotlin
val toolbarTitle = withId(R.id.toolbar_title)

fun assertToolbarTitleWithSuitableRoot(text: String) {
    toolbarTitle.withSuitableRoot().hasText(text)
}
```

withSuitableRoot() extension returns a view interaction with the correct root view in which the element you are looking for will be  located. If the root view is not found, the test will be interrupted with espresso exception - NoMatchingRootException: Matcher ...did not match any of the following roots...

You can also use the root matcher to set the root for Espresso view interaction.

```kotlin
val toolbarTitle = withId(R.id.toolbar_title)
onView(toolbarTitle).inRoot(withSuitableRoot(toolbarTitle)).check {
    // Your checks here
}
```

The same works for UltronRecyclerViewItem:

```kotlin
val recycler = withRecyclerView(R.id.recycler_friends)

class FriendRecyclerItem : UltronRecyclerViewItem() {
    val name by child { withId(R.id.tv_name) }
    val status by child { withId(R.id.tv_status) }
    val avatar by child { withId(R.id.avatar) }
}

fun getListItem(positions: Int): FriendRecyclerItem {
    return recycler.getItem(positions)
}

// Usage:

getListItem(0).withSuitableRoot().isDisplayed()
getListItem(0).name.withSuitableRoot().isDisplayed().click()
```
