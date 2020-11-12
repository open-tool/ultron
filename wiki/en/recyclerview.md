# Interaction with RecyclerView
Before we go forward we need to define some terms:
- RecyclerView - list of some items (a standard Android framework class)
- RecyclerViewItem - single item of RecyclerView list (there is a class RecyclerViewItem in espresso-page-object lib)
- RecyclerItemChild - child element of RecyclerViewItem (just a term, there is no special class to work with child elements)

![RecyclerViewItem](https://github.com/alex-tiurin/espresso-page-object/blob/master/wiki/img/recyclerViewItem.png)

In case you don't need to work with RecyclerItemChild you just need to create a method that returns RecyclerViewItem instance.

```kotlin
fun getListItem(text: String): RecyclerViewItem {
    return RecyclerViewItem(
        withId(R.id.recycler_friends),
        hasDescendant(allOf(withId(R.id.tv_name),withText(text)))
    )
}

fun someUserAction(): SomePage = apply{
    getListItem(text).longClick()
    ...
}
```

In case you need to make some actions or assertions on RecyclerItemChild you need to do the following:
- create a RecyclerViewItem subclass and describe a property of this subclass that represents a RecyclerItemChild,
- create a method that returns RecyclerViewItem subclass instance.

```kotlin
object FriendsListPage : Page<FriendsListPage> {
    private val friendsList = withId(R.id.recycler_friends)

    class FriendRecyclerItem(list: Matcher<View>, item: Matcher<View>) : RecyclerViewItem(list, item) {
        val name = getChildMatcher(withId(R.id.tv_name))
        val status = getChildMatcher(withId(R.id.tv_status))
    }

    private fun getListItem(title: String): FriendRecyclerItem {
        return FriendRecyclerItem(
            withId(R.id.recycler_friends),
            hasDescendant(allOf(withId(R.id.tv_name),withText(title)))
        )
    }

    fun assertStatus(name: String, status: String) = apply {
        step("Assert friend with name '$name' has status '$status'") {
            this.getListItem(name).status.hasText(status)
        }
    }
}
```
Note that RecyclerItemChild element is created by `getChildMatcher` method of RecyclerViewItem.class

```kotlin
val name = getChildMatcher(withId(R.id.tv_name))
```

## Positionable RecyclerViewItem

Sometimes you need to get RecyclerViewItem by it's position in RecyclerView list. For example, you need to take a first item in a list.

In this case you need to use another constructor of RecyclerViewItem.class

```kotlin
object ChatPage : Page<ChatPage> {
    private val messagesList = withId(R.id.messages_list)

    class ChatRecyclerItem : RecyclerViewItem {
        constructor(list: Matcher<View>, item: Matcher<View>) : super(list, item)
        constructor(list: Matcher<View>, position: Int) : super(list, position)

        val text = getChildMatcher(withId(R.id.message_text))
    }

    private fun getListItemAtPosition(position: Int): ChatRecyclerItem {
        return ChatRecyclerItem(messagesList, position)
    }

    fun assertMessageTextAtPosition(position: Int, text: String) = apply {
        this.getListItemAtPosition(position).text.isDisplayed().hasText(text)
    }
}
```
