# Работа с RecyclerView

Прежде чем начать, определимся с понятиями:
- RecyclerView - список элементов (есть стандартный класс RecyclerView в Android фреймворке)
- RecyclerViewItem - один из элементов списка (есть класс RecyclerViewItem в библиотеке espresso-page-object)
- RecyclerItemChild - дочерний элемент внутри элемента списка (просто понятие, отдельного класса для работы с дочерними элементами нет)

![RecyclerViewItem](https://github.com/alex-tiurin/espresso-page-object/blob/master/wiki/img/recyclerViewItem.png)

Если необходимо работать только с элементами списка и нет нужды работать и проверять его дочерние элементы, то создаем метод, который возвращает экземпляр класса RecyclerView

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

Если необходимо работать с дочерними элементами списка, то создаем наследника RecyclerViewItem и описываем дочерние элементы:

```kotlin
object FriendsListPage : Page<FriendsListPage> {
    val friendsList = withId(R.id.recycler_friends)

    class FriendRecyclerItem(list: Matcher<View>, item: Matcher<View>) : RecyclerViewItem(list, item) {
        val name = getChildMatcher(withId(R.id.tv_name))
        val status = getChildMatcher(withId(R.id.tv_status))
    }

    fun getListItem(title: String): FriendRecyclerItem {
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
Обратите внимание, что дочерние элементы item создаются с использованием метода `getChildMatcher`
```kotlin
val name = getChildMatcher(withId(R.id.tv_name))
```
## Получение RecyclerViewItem по его позиции в списке

Иногда, получить RecyclerViewItem и его дочерние элементы необходимо основываясь на позиции элемента в списке. Например, вам необходми получить первый элементв в списке.

Для этого необходимо использовать другой конструктор класса RecyclerViewItem.

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