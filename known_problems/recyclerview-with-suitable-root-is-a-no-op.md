# `withSuitableRoot()` on `UltronRecyclerView` / `UltronRecyclerViewItem` is a no-op

Found 2026-09-23 while chasing a 30-second hang in the Assertum recorder
(`assertum/test-generation-problems/case-listitemchild-command-hangs-until-host-restart.md`).
Affects ultron 2.6.6 (`fad8607`); not fixed in ultron yet.

## What is wrong

Both extension points build the root-pinned interaction and throw it away:

```kotlin
// ultron-android/src/main/kotlin/com/atiurin/ultron/custom/espresso/matcher/SuitableRootMatcher.kt:35
fun UltronRecyclerView.withSuitableRoot() = apply { recyclerViewMatcher.withSuitableRoot() }

// ultron-android/src/main/kotlin/com/atiurin/ultron/core/espresso/recyclerview/UltronRecyclerViewItem.kt:108
fun withSuitableRoot() = apply { this.getMatcher().withSuitableRoot() }
```

`Matcher<View>.withSuitableRoot()` returns a **new** `UltronEspressoInteraction` whose
`ViewInteraction` carries `inRoot(withDecorView(...))`. Inside `apply { }` that value is neither
stored nor returned, so the receiver is unchanged: `recyclerViewInteraction` stays the plain
`onView(matcher)` it was built from, and every interaction derived from it later —
`RecyclerViewItemExecutor.getItemInteraction()` / `getItemChildInteraction()`, both of which do
`UltronEspressoInteraction(onView(...))` — reaches Espresso with the **default** root matcher.

The plain-view path does not have this problem: `ViewInteraction.withSuitableRoot()` in
`extensions/ViewInteractionExt.kt` resolves the decor view on the UI thread and returns
`this.inRoot(withDecorView(is(decorView)))`, and the caller uses the returned interaction.

## How it shows

With more than one window on screen (a bottom sheet, a dialog, a `PopupWindow`), Espresso's default
root matcher can accept none of the roots, and `RootViewPicker` retries with a growing back-off:

```
RootViewPicker: No matching root available - waiting: 10ms for one to appear.
RootViewPicker: No matching root available - waiting: 20ms …
…
RootViewPicker: No matching root available - waiting: 2000ms for one to appear.   (repeats)
```

A `withRecyclerView(list).withSuitableRoot().item(m).getChild(c).click()` therefore waits until the
caller's own timeout, while the same target reached through `onView(...).withSuitableRoot()` is
pinned to its root. The user believes the root is pinned because the method is there and chains.

Note the limit of the fix, too: `SuitableRootMatcher` requires `root.isReady()`, and in Espresso that
means the window has focus (or is `FLAG_NOT_FOCUSABLE`). Pinning the root does not make an unfocused
window reachable — if a focusable window is in front, the pinned interaction still waits. Pinning
only stops the default matcher from rejecting a root that would have been acceptable.

## Suggested fix

Make the root part of the RecyclerView's state and derive every interaction from it:

1. `UltronRecyclerView.withSuitableRoot()` should return a `UltronRecyclerView` built from
   `UltronEspressoInteraction(onView(recyclerViewMatcher).withSuitableRoot())` (the primary
   constructor already takes an interaction), not `apply {}` over a discarded value.
2. `RecyclerViewItemExecutor.getItemInteraction()` / `getItemChildInteraction()` should build their
   `ViewInteraction` with the RecyclerView interaction's root matcher (`getInteractionRootMatcher()`)
   instead of a bare `onView(...)`.
3. `UltronRecyclerViewItem.withSuitableRoot()` then either delegates to the executor or is removed —
   as written it cannot do anything.
4. A test: two windows (an activity plus a shown `PopupWindow` that is `FLAG_NOT_FOCUSABLE`), a
   RecyclerView item child clicked through `withSuitableRoot()`; it must not enter the
   `RootViewPicker` back-off. The sample app has the RecyclerView screens for it.

## Workaround used in the recorder

`assertum/genatest-studio/recorderclient/.../ViewOperationExecutor.kt` (`rootedRecyclerView`,
`recyclerItem`, `recyclerItemChild`) builds the `UltronRecyclerView` from an already-rooted
interaction and re-pins the item / child matchers with `onView(...).withSuitableRoot()` itself.
