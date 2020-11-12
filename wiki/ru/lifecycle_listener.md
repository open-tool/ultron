# Lifecycle Listeners

В библиотеке есть интерфейс, который позволяет прослушивать все действия
и проверки в момент их выполнения. Для этого интерфейс предоставляет 4
метода.

```kotlin
internal interface LifecycleListener{
    /**
     * executed before any action or assertion
     */
    fun before(operation: Operation)
    /**
     * called when action or assertion has been executed successfully
     */
    fun afterSuccess(operationResult: OperationResult)

    /**
     * called when action or assertion failed
     */
    fun afterFailure(operationResult: OperationResult)

    /**
     * called in any case of action or assertion result
     */
    fun after(operationResult: OperationResult)
}
```

*Operation*  и *OperationResult* - интерфейсы. Они предоставляют доступ ко всей необходимой информации об операции и ее результате.

Для того, чтобы воспользоваться интерфейсом `LifecycleListener`, вам необходимо создать
наследника абстрактного класса `AbstractLifecycleListener`. Например,
создадим листенер, который будет делать скриншот в случае неуспешного
выполнения действия или проверки.
```kotlin
class ScreenshotLifecycleListener : AbstractLifecycleListener(){
    override fun afterFailure(description: Description, throwable: Throwable) {
        takeScreenshot(description.type.toString()) // takeScreenshot() isn't implemented
    }
}
```

Теперь, созданный listener необходимо добавить в список используемых.
 ```kotlin
companion object {
    @BeforeClass @JvmStatic
    fun beforeClass() {
        val listener = ScreenshotLifecycleListener()
        ViewActionLifecycle.addListener(listener)
        ViewAssertionLifecycle.addListener(listener)
    }
}
```

По умолчанию в список используемых listeners для действий и проверок
добавлен `LogLifecycleListener`.

Если вы хотите сбросить список используемых listeners, то необходимо
сделать следующее:

```kotlin
ViewActionLifecycle.clearListeners()
ViewAssertionLifecycle.clearListeners()
```

*Обратить внимание, что тяжелый listener может снизить скорость
выполнения ваших тестов!*