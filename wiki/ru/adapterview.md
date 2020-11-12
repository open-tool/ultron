# Взаимодействие с AdapterView и использование onData

В Espresso взаиможействе с элементами AdapterView осуществляется с помощью `onData(Matcher<View>)` (подробнее об этом [тут](https://developer.android.com/training/testing/espresso/lists))

```kotlin

object SomePage : Page<SomePage>{
    val adapterElement = onData(withText(R.id.textId))
}
```
Все функции click(), longClick(), isDisplayed() и т.д. будут доступны для `adapterElement`