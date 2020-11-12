# Interaction with AdapterView and usage of onData

In case you need to interact with AdapterView and use standard Espresso `onData(Matcher<View>)` method (read more about it  [here](https://developer.android.com/training/testing/espresso/lists))

```kotlin

object SomePage : Page<SomePage>{
    val adapterElement = onData(withText(R.id.textId))
}
```

All actions and assertions like click(), longClick(), isDisplayed() and so on will be allowed for `adapterElement`