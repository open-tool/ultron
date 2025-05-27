---
sidebar_position: 3
---

# RecyclerView

## Terms
Before we go forward we need to define some terms:
- RecyclerView - list of some items (a standard Android framework class). Ultron has a class that wraps an interaction with RecyclerView - `UltronRecyclerView`. 
- RecyclerViewItem - single item of RecyclerView list (there is a class `UltronRecyclerViewItem`)
- RecyclerViewItem.child - child element of RecyclerViewItem (just a term, there is no special class to work with child elements). So _RecyclerViewItem.child_ could be considered as a simple android View.

![Terms](https://user-images.githubusercontent.com/12834123/107883156-4008d000-6efe-11eb-9764-8c57e767e5e2.png)

## UltronRecyclerView

Create an instance of `UltronRecyclerView` using the method `withRecyclerView(..)` method:

```kotlin
withRecyclerView(R.id.recycler_friends).assertSize(CONTACTS.size)
```

### Parameters for `withRecyclerView` method

The withRecyclerView method allows creating an instance of UltronRecyclerView with customizable parameters:

- `recyclerViewMatcher: Matcher`/ `resourceId: Int`, - A Matcher / @IntegerRes that identifies the target RecyclerView in the layout.
- `loadTimeout: Long` - The maximum time (in milliseconds) to wait for RecyclerView items to load. The default value is defined by `UltronConfig.Espresso.RECYCLER_VIEW_LOAD_TIMEOUT`.
- `itemSearchLimit: Int` - The maximum number of items to search through when locating an item in the RecyclerView. The default value is defined by `UltronConfig.Espresso.RECYCLER_VIEW_ITEM_SEARCH_LIMIT`.
- `operationsTimeoutMs: Long` - The maximum time (in milliseconds) to wait for operations on RecyclerView to complete. The default value is defined by `UltronConfig.Espresso.RECYCLER_VIEW_OPERATIONS_TIMEOUT`.
- `implementation: UltronRecyclerViewImpl`- Specifies the implementation of UltronRecyclerView to use. The default value is `UltronConfig.Espresso.RECYCLER_VIEW_IMPLEMENTATION`, which is set in the configuration.


### UltronRecyclerViewImpl

`UltronRecyclerViewImpl` has two available modes:

- `STANDARD`: This is the default implementation. It is already 4 times faster than the previous version. When multiple identical child elements are found within an UltronRecyclerViewItem, the first matching element is selected without throwing an AmbiguousViewMatcherException.

- `PERFORMANCE`: Optimized for higher performance. However, if multiple child elements matching the same criteria are found, an exception (AmbiguousViewMatcherException) will be thrown.

The choice of implementation affects not only performance but also how child elements of `UltronRecyclerViewItem` are handled.
For now, the actual difference in performance between these modes is minimal, but it could be highly valuable if your RecyclerView item contains many child elements.

### _Best practice_ - save `UltronRecyclerView` as page class properties   

```kotlin
object FriendsListPage : Page<FriendsListPage>() {
    // param loadTimeout in ms specifies a time of waiting while RecyclerView items will be loaded
    val recycler = withRecyclerView(R.id.recycler_friends, loadTimeout = 10_000L) 
    fun someStep(){
        recycler.assertEmpty()
        recycler.hasContentDescription("Description")
    }
}
```

`UltronRecyclerView` api

```kotlin
// ----- assertions -----
assertEmpty()                                 // Asserts RecyclerView has no item
assertSize(expected: Int)                     // Asserts RecyclerView list has [expected] items count during
assertHasItemAtPosition(position: Int)        // Asserts RecyclerView list has item at [position]
assertMatches(matcher: Matcher<View>)         // Assert RecyclerView matches custom condition
assertItemNotExist(matcher: Matcher<View>, timeoutMs: Long) // watch java doc to understand how it works
assertItemNotExistImmediately(matcher: Matcher<View>, timeoutMs: Long)
isDisplayed()
isNotDisplayed()
doesNotExist()
isEnabled()
isNotEnabled()
hasContentDescription(contentDescription: String)
hasContentDescription(resourceId: Int)
hasContentDescription(charSequenceMatcher: Matcher<CharSequence>)
contentDescriptionContains(text: String)
// ----- item providers for simple UltronRecyclerViewItem -----
// all item provider methods has params [autoScroll: Boolean = true, scrollOffset: Int = 0]. It's shown only once but all of them has it
item(matcher: Matcher<View>, autoScroll: Boolean = true, scrollOffset: Int = 0): UltronRecyclerViewItem 
item(position: Int, ..): UltronRecyclerViewItem 
firstItem(..): UltronRecyclerViewItem
lastItem(..): UltronRecyclerViewItem

// Sometimes it is impossible to provide unique matcher for RecyclerView item
// There is a set of methods to access not unique items by matcher and index
// index is a value from 0 to lastIndex of matched items
itemMatched(matcher: Matcher<View>, index: Int): UltronRecyclerViewItem
firstItemMatched(matcher: Matcher<View>, ..): UltronRecyclerViewItem
lastItemMatched(matcher: Matcher<View>, ..): UltronRecyclerViewItem

// ----- item providers for UltronRecyclerViewItem subclasses -----
// following methods return a generic type T which is a subclass of UltronRecyclerViewItem
getItem(matcher: Matcher<View>, autoScroll: Boolean = true, scrollOffset: Int = 0): T  
getItem(position: Int, ..): T  
getFirstItem(..): T 
getLastItem(..): T

// ----- in case it's impossible to define unique matcher for `UltronRecyclerViewItem` -----
getItemMatched(matcher: Matcher<View>, index: Int, ..): T
getFirstItemMatched(matcher: Matcher<View>, ..): T
getLastItemMatched(matcher: Matcher<View>, ..): T
```
## UltronRecyclerViewItem

`UltronRecyclerView` provides an access to `UltronRecyclerViewItem`. 

### Simple Item

If you don't need to interact with item child just use methods like `item`, `firstItem`, `lastItem`, `itemMatched` and etc

```kotlin
recycler.item(position = 10, autoScroll = true).click() // find item at position 10 and scroll to this item 
recycler.item(matcher = hasDescendant(withText("Janice"))).isDisplayed()
recycler.firstItem().click() //take first RecyclerView item
recycler.lastItem().isCompletelyDisplayed()

// if it's impossible to specify unique matcher for target item
val matcher = hasDescendant(withText("Friend"))
recycler.itemMatched(matcher, index = 2).click() //return 3rd matched item, because index starts from zero
recycler.firstItemMatched(matcher).isDisplayed()
recycler.lastItemMatched(matcher).isDisplayed()
recycler.getItemsAdapterPositionList(matcher) // return positions of all matched items
```
You don't need to worry about scroll to item. By default autoscroll in all item accessor method equals true.

### Complex item with children

It's often required to interact with item child. The best solution will be to describe children as properties of `UltronRecyclerViewItem` subclass.

```kotlin
class FriendRecyclerItem : UltronRecyclerViewItem() {
    val avatar by child { withId(R.id.avatar) }
    val name by child { withId(R.id.tv_name) }
    val status by child { withId(R.id.tv_status) }
}
```
**Note: you have to use delegated initialisation with `by child`.**

Now you're able to get `FriendRecyclerItem` object using methods `getItem`, `getFirstItem`, `getLastItem` etc

```kotlin
recycler.getItem<FriendRecyclerItem>(position = 10, autoScroll = true).status.hasText("UNAGI")
recycler.getItem<FriendRecyclerItem>(matcher = hasDescendant(withText("Janice"))).status.textContains("Oh. My")
recycler.getFirstItem<FriendRecyclerItem>().avatar.click() //take first RecyclerView item
recycler.getLastItem<FriendRecyclerItem>().isCompletelyDisplayed()

// if it's impossible to specify unique matcher for target item
val matcher = hasDescendant(withText(containsString("Friend")))
recycler.getItemMatched<FriendRecyclerItem>(matcher, index = 2).name.click() //return 3rd matched item, because index starts from zero
recycler.getFirstItemMatched<FriendRecyclerItem>(matcher).name.hasText("Friend1")
recycler.getLastItemMatched<FriendRecyclerItem>(matcher).avatar.isDisplayed()
```
### _Best practice_ - add a method to Page class that returns `FriendRecyclerItem` 

```kotlin
object FriendsListPage : Page<FriendsListPage>() {
    val recycler = withRecyclerView(R.id.recycler_friends)
    fun getListItem(contactName: String): FriendRecyclerItem {
        return recycler.getItem(hasDescendant(allOf(withId(R.id.tv_name), withText(contactName))))
    }
    fun getListItem(positions: Int): FriendRecyclerItem {
        return recycler.getItem(positions)
    }
}
```
use `getListItem` inside `FriendsListPage` steps
```kotlin
object FriendsListPage : Page<FriendsListPage>() {
    ...
    fun assertStatus(name: String, status: String) = apply {
        getListItem(name).status.hasText(status).isDisplayed()
    }
}
```

`UltronRecyclerViewItem` api

```kotlin
//actions 
scrollToItem(offset: Int = 0)
click()
longClick()
doubleClick()
swipeUp()
swipeDown()
swipeLeft()
swipeRight()
perform(viewAction: ViewAction)

//assertions
isDisplayed()
isNotDisplayed()
isCompletelyDisplayed()
isDisplayingAtLeast(percentage: Int)
isClickable()
isNotClickable()
isEnabled()
isNotEnabled()
assertMatches(condition: Matcher<View>)
hasContentDescription(contentDescription: String)
hasContentDescription(resourceId: Int)
hasContentDescription(charSequenceMatcher: Matcher<CharSequence>)
contentDescriptionContains(text: String)

//general
getViewHolder(): RecyclerView.ViewHolder?
getChild(childMatcher: Matcher<View>): Matcher<View> //return matcher to a child element
withTimeout(timeoutMs: Long) //set custom timeout for the next operation
withResultHandler(..) // allows you to process action on item by your own way

// click options
clickTopLeft(offsetX: Int = 0, offsetY: Int = 0)
clickTopCenter(offsetY: Int)
clickTopRight(offsetX: Int = 0, offsetY: Int = 0)
clickCenterRight(offsetX: Int = 0)
clickBottomRight(offsetX: Int = 0, offsetY: Int = 0)
clickBottomCenter(offsetY: Int = 0)
clickBottomLeft(offsetX: Int = 0, offsetY: Int = 0)
clickCenterLeft(offsetX: Int = 0)
```
