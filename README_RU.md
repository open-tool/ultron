# Espresso page object

[![Download](https://api.bintray.com/packages/alex-tiurin/espresso-page-object/espressopageobject/images/download.svg)](https://bintray.com/alex-tiurin/espresso-page-object/espressopageobject/_latestVersion)
![Android CI](https://github.com/alex-tiurin/espresso-page-object/workflows/AndroidCI/badge.svg)

Это библиотека, предоставляющая доступ к простому и понятному  DSL для работы с Espresso фреймворком.
Вам не нужно запоминать новые классы, изучать новый синтаксис. Все действия у вас появляются из коробки.
Для продвинутых пользователей библиотека предоставляет хорошую возможность в кастомизации и расширении возможностей DSL.
Стабильных Вам тестов!

## Что дает подлючение библиотеки?

- Стабильность выполнения всех действий и проверок
- Архитектурный подход к написанию тестов
- Простой и понятный синтаксис

Стандартный синтаксис Espresso сложен и не интуитивен в понимании. Особенно это проявляется при взаимодейстивии с RecyclerView

Рассмотрим 2 примера:

1. Обычный клик на кнопку.

Чистый Espresso

```kotlin
onView(withId(R.id.send_button)).perform(click())
```
 Espresso page object
```kotlin
withId(R.id.send_button).click()
```

2. Клик на элемент списка RecyclerView

Чистый Espresso

```kotlin
onView(withId(R.id.recycler_friends))
    .perform(
        RecyclerViewActions
            .actionOnItem<RecyclerView.ViewHolder>(
                hasDescendant(withText("Janice")),
                click()
            )
        )
```
 Espresso page object
```kotlin
withRecyclerView(withId(R.id.recycler_friends))
    .atItem(hasDescendant(withText("Janice")))
    .click()
```

## Особенности библиотеки

-  [Как взаимодействовать с RecyclerView](https://github.com/alex-tiurin/espresso-page-object/blob/master/wiki/ru/recyclerview.md)
-  [AdapterView и onData](https://github.com/alex-tiurin/espresso-page-object/blob/master/wiki/ru/adapterview.md)
-  [Как обеспечивается стабильность всех действий и проверок](https://github.com/alex-tiurin/espresso-page-object/blob/master/wiki/ru/operations_stability.md)
-  [Lifecycle listener. Прослушиваем все операции и их результаты](https://github.com/alex-tiurin/espresso-page-object/blob/master/wiki/ru/lifecycle_listener.md)
-  [RuleSequence + SetUpTearDownRule. Полный контроль над вашими тестами](https://github.com/alex-tiurin/espresso-page-object/blob/master/wiki/ru/rulesequence_setupterdownrule.md)

## 3 шага для написания теста с использованием espresso-page-object

Я стараюсь пропогандировать правильное построение архитектуры тествого фреймворка, разделение ответственности между слоями и прочие правильные штуки.

Поэтому рекомендую использовать следующий подход при использовании библиотеки.

1. Создайте PageObject class и определите `Matcher<View>` UI элементов экрана в нем

```kotlin
object ChatPage : Page<ChatPage>() {
    private val messagesList = withId(R.id.messages_list)
    private val clearHistoryBtn = withText("Clear history")
    private val inputMessageText = withId(R.id.message_input_text)
    private val sendMessageBtn = withId(R.id.send_button)
}
```
Некоторые элементы, такие как заголовки открытого чата, могут вычисляться динамически, в зависимости от данных приложения.
Тогда для их определния в класс PageObject необходимо добавить метод, возвращающий объект `Matcher<View>`
```kotlin
object ChatPage : Page<ChatPage>() {
    private fun getTitle(title: String): Matcher<View> {
        return allOf(withId(R.id.toolbar_title), withText(title))
    }
}
```
Рекомендуется сделать все классы PageObject синглтонами и потомками абстрактного класса Page.
В этом случае вы сможете использовать магию котлина.

2. Добавьте методы действий пользоватя в класс PageObject

```kotlin
object ChatPage : Page<ChatPage>() {
    fun sendMessage(text: String) = apply {
        inputMessageText.typeText(text)
        sendMessageBtn.click()
        this.getListItem(text).text
            .isDisplayed()
            .hasText(text)
    }

    fun clearHistory() = apply {
        openOptionsMenu()
        clearHistoryBtn.click()
    }
}
```
См. полный код [ChatPage.class](https://github.com/alex-tiurin/espresso-page-object/blob/master/app/src/androidTest/java/com/atiurin/espressopageobjectexample/pages/ChatPage.kt)

3. Добавьте действия пользователя в тест

```kotlin
    @Test
    fun friendsItemCheck(){
         FriendsListPage {
            assertName("Janice")
            assertStatus("Janice","Oh. My. God")
        }
    }
    @Test
    fun sendMessage(){
        FriendsListPage().openChat("Janice")
        ChatPage {
            clearHistory()
            sendMessage("test message")
        }
    }
```

См. полный код с примером теста [DemoEspressoTest](https://github.com/alex-tiurin/espresso-page-object/blob/master/app/src/androidTest/java/com/atiurin/espressopageobjectexample/tests/DemoEspressoTest.kt)

Для подготовки тестовых данных используйте RuleSequence + SetUpTearDownRule.

В целом все сводится к тому, что архитектура вашего проекта будет выглядеть следующим образом.

![Architecture](https://github.com/alex-tiurin/espresso-page-object/blob/master/wiki/img/architecture.png)

## Подключение к проекту
Gradle
```groovy
repositories {
    jcenter()
}

dependencies {
    androidTestImplementation 'com.atiurin.espresso:espressopageobject:0.1.19'
}
```
Maven
```
<dependency>
  <groupId>com.atiurin.espresso</groupId>
  <artifactId>espressopageobject</artifactId>
  <version>0.1.19</version>
  <type>pom</type>
</dependency>
```
Версия 0.1.17 имеет новую внутренную структуру классов, поэтому для миграции на нее необходимо поддержать пару незначительных изменений в вашем проекте.

Подробности об этом - [Migration to 0.1.17](https://github.com/alex-tiurin/espresso-page-object/wiki/Migration-to-0.1.17)
## AndroidX

Необходимо, чтобы ваш проект использовал AndroidX библиотеки. С android support могут возникнуть проблемы.



