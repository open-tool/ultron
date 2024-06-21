---
sidebar_position: 5
---

# LazyList

## Ultron LazyColumn/LazyRow

It's pretty much familiar with `UltronRecyclerView` approach. The difference is in internal structure of `RecyclerView `and `LazyColumn/LazyRow`.
Due to implementation features of LazyColumn/LazyRow we can't predict where matched item is located in list without scrolling (actually we can but it takes additional efforts from development)

Before we go forward we need to clarify some terms:

- ComposeList - list of some items. It's typically implemented in application as LazyColumnt or LazyRow. Ultron has a class that wraps an interaction with list - `UltronComposeList`.
- ComposeListItem - single item of ComposeList (there is a class `UltronComposeListItem`)
- ComposeListItemChild - child element of ComposeListItem (just a term, there is no special class to work with child elements). So _ComposeListItemChild_ could be considered as a simple compose node.

![lazyColumn](https://user-images.githubusercontent.com/12834123/188237127-32e501ca-ae8b-4cd4-8114-e3e17843dc55.PNG)

***
## UltronComposeList

Create an instance of `UltronComposeList` by calling a method `composeList(..)`

```kotlin
composeList(hasTestTag(contactsListTestTag)).assertNotEmpty()
```
### _Best practice_ - define `UltronComposeList` object as page class property

```kotlin
object ContactsListPage : Page<ContactsListPage >() {
   val lazyList = composeList(hasContentDescription(contactsListContentDesc))
    fun someStep(){
        lazyList.assertNotEmpty() 
        lazyList.assertContentDescriptionEquals(contactsListContentDesc)
    }
}
```

### `UltronComposeList` API
```kotlin
withTimeout(timeoutMs: Long) // defines a timeout for all operations 
//assertions
fun assertIsDisplayed() 
fun assertIsNotDisplayed()
fun assertExists() 
fun assertDoesNotExist()
fun assertContentDescriptionEquals(vararg expected: String)
fun assertContentDescriptionContains(expected: String, option: ContentDescriptionContainsOption? = null)
fun assertNotEmpty()
fun assertEmpty()
fun assertVisibleItemsCount(expected: Int) 

//item providers for simple UltronComposeListItem
fun item(matcher: SemanticsMatcher): UltronComposeListItem
fun visibleItem(index: Int): UltronComposeListItem
fun firstVisibleItem(): UltronComposeListItem
fun lastVisibleItem(): UltronComposeListItem

// ----- item providers for UltronComposeListItem subclasses -----
// following methods return a generic type T which is a subclass of UltronComposeListItem
fun getItem(matcher: SemanticsMatcher): T
fun getVisibleItem(index: Int): T
fun getFirstVisibleItem(): T 
fun getLastVisibleItem(): T

//interaction provider
visibleChild(matcher: SemanticsMatcher)  // provides an interaction on visible matched item

//actions
fun getVisibleItemsCount(): Int
fun scrollToNode(itemMatcher: SemanticsMatcher)
fun scrollToIndex(index: Int) 
fun scrollToKey(key: Any)
/**
* Provide a scope with references to list SemanticsNode and SemanticsNodeInteraction.
* It is possible to evaluate any action or assertion on this node.
*/
fun <T> performOnList(block: (SemanticsNode, SemanticsNodeInteraction) -> T): T
```

### useUnmergedTree
It is really important to understand the difference btwn merged and unmerged tree. There is a property `useUnmergedTree` that defines a behaviour.
```kotlin
composeList(hasTestTag(contactsListTestTag), useUnmergedTree = false)
```

- By default `UltronComposeList` uses unmerged tree (`useUnmergedTree = true`). All child elements contain info in seperate nodes.
- In case we use merged tree (`useUnmergedTree = false`) all child elements of item is merged to single node. So you're not able to identify a text value of concrete child.

Why it's important? Cause you need to use different SemanticsMatchers to find appropriate child.

```kotlin
mergedTreeList.item(hasText(contact.name)) // contact.name could be placed in wrong child
unmergedList.item(hasAnyDescendant(hasText(contact.name) and hasTestTag(contactNameTestTag))) //it's longer but certainly provides target node
```
***
## UltronComposeListItem
`UltronComposeList` provides an access to `UltronComposeListItem`

There is a set of methods to create `UltronComposeListItem`. It's listed upper in `UltronComposeList` api.

### Simple `UltronComposeListItem`

If you don't need to interact with item child just use methods like  `item`, `firstItem`, `visibleItem`, `firstVisibleItem`, `lastVisibleItem`
```kotlin
listWithMergedTree.item(hasText(contact.name)).assertTextContains(contact.name)
listWithMergedTree.firstVisibleItem()
    .assertIsDisplayed()
    .assertTextContains(contact.name)
    .assertTextContains(contact.status)
```
You don't need to worry about scroll to item. It's executed automatically.

### Complex `UltronComposeListItem` with children

It's often required to interact with item child. The best solution will be to describe children as properties of UltronComposeListItem subclass.
```kotlin
class ComposeFriendListItem : UltronComposeListItem(){
    val name by child { hasTestTag(contactNameTestTag) }
    val status by child { hasTestTag(contactStatusTestTag) }
}
```
**Note: you have to use delegated initialisation with `by child`.**

Now you're able to get `ComposeFriendListItem` object using methods `getItem`, `getVisibleItem`, `getFirstVisibleItem`, `getLastVisibleItem`

```kotlin
lazyList.getFirstVisibleItem<ComposeFriendListItem>()
lazyList.getVisibleItem<ComposeFriendListItem>(index)
lazyList.getItem<ComposeFriendListItem>(hasTestTag(..))
```

### _Best practice_

> Add a method to `Page` class that returns `UltronComposeListItem` subclass

Mark such methods with `private` visibility modifier. e.g. `getContactItem`
```kotlin
object ComposeListPage : Page<ComposeListPage>() {
    private val lazyList = composeList(hasContentDescription(contactsListContentDesc))
    private fun getContactItem(contact: Contact): ComposeFriendListItem = lazyList.getItem(hasTestTag(contact.id))

    class ComposeFriendListItem : UltronComposeListItem(){
        val name by lazy { getChild(hasTestTag(contactNameTestTag)) }
        val status by lazy { getChild(hasTestTag(contactStatusTestTag)) }
    }
}
```
Use `getContactItem` in `Page` steps like `assertContactStatus`
```kotlin
object ComposeListPage : Page<ComposeListPage>() {
    private fun getContactItem(contact: Contact): ComposeFriendListItem = lazyList.getItem(hasTestTag(contact.id))
    ...
    fun assertContactStatus(contact: Contact) = apply {
         getContactItem(contact).status.assertTextEquals(contact.status)
    }
}
```

## `UltronComposeListItem` API

It's pretty much the same as [simple node api](../compose/api.md), but extends it mostly for internal features.

***
## Efficient Strategies for Locating Items in Compose LazyList

Let's start with approaches that you can use without additional efforts. For example, you have identified `LazyList` in your tests code like

```kotlin
val lazyList = composeList(listMatcher = hasTestTag("listTestTag"))

class ComposeListItem : UltronComposeListItem() {
    val name by lazy { getChild(hasTestTag(contactNameTestTag)) }
    val status by lazy { getChild(hasTestTag(contactStatusTestTag)) }
}
```

### 1. `..visibleItem`

This is probably the most unstable approach. It's only suitable in case you didn't interact with `LazyList` and would like to reach an item that is on the screen.

Use the following methods:

```kotlin
lazyList.firstVisibleItem()
lazyList.visibleItem(index = 3)
lazyList.lastVisibleItem()

lazyList.getFirstVisibleItem<ComposeListItem>()
lazyList.getVisibleItem<ComposeListItem>(index = 3)
lazyList.getLastVisibleItem<ComposeListItem>()
```

### 2. Item by unique `SemanticsMatcher`

A more stable way to find the item is to use `SemanticsMatcher`. It allows you to find the item not only on the screen.

```kotlin
lazyList.item(hasAnyDescendant(hasText("Some unique text")) 
lazyList.getItem<ComposeListItem>(hasAnyDescendant(hasText("Some unique text")) 
```

***

The next two approaches require additional code in the application. These are the most stable and preferable ways.

### 3. Set up `positionPropertyKey`

By default, a compose list item doesn't have a property that stores its position in the list. We can add this property in a really simple way.

Here is the application code:
```kotlin
// create custom SemanticsPropertyKey
val ListItemPositionPropertyKey = SemanticsPropertyKey<Int>("ListItemPosition")
var SemanticsPropertyReceiver.listItemPosition by ListItemPositionPropertyKey

// specify it for item and store item index in this property
@Composable
fun ContactsListWithPosition(contacts: List<Contact>
) {
    LazyColumn(
        modifier = Modifier.semantics { testTag = "listTestTag" }
    ) {
        itemsIndexed(contacts) { index, contact ->
            Column(
                modifier = Modifier.semantics {
                    listItemPosition = index
                }
            ) {
                // item content
            }
        }
    }
}
```

After that, you need to specify the custom `SemanticsPropertyKey` in the test code:

```kotlin
val lazyList = composeList(
    listMatcher = hasTestTag("listTestTag"),
    positionPropertyKey = ListItemPositionPropertyKey
)
```

It allows you to reach the item by its position in the list:

```kotlin
lazyList.firstItem()
lazyList.item(position = 25)
lazyList.getFirstItem<ComposeListItem>()
lazyList.getItem<ComposeListItem>(position = 7)
```

### 4. Set up item `testTag`

It is recommended to build `testTag` in a separate function based on data object. 

For example, let's assume we have a `Contact` data class that stores data to be presented in the item.

```kotlin
data class Contact(val id: Int, val name: String, val status: String, val avatar: String)
```

We can create function to build `testTag` based on `contact.id`

```kotlin
fun getContactItemTestTag(contact: Contact) = "contactId=${contact.id}"
```

We can use this function in the application code to specify `testTag` and in the test code to find the item by `testTag`:

```kotlin
// application code
@Composable
fun ContactsListWithPosition(contacts: List<Contact>
) {
    LazyColumn(
        modifier = Modifier.semantics { testTag = "listTestTag" }
    ) {
        itemsIndexed(contacts) { index, contact ->
            Column(
                modifier = Modifier.semantics {
                    listItemPosition = index
                    testTag = getContactItemTestTag(contact)
                }
            ) {
                // item content
            }
        }
    }
}

//test code
val lazyList = composeList(listMatcher = hasTestTag("listTestTag"))

lazyList.item(hasTestTag(getContactItemTestTag(contact)))
lazyList.getItem<ComposeListItem>(hasTestTag(getContactItemTestTag(contact)))

```