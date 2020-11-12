# Стабильность действий и проверок

Стабильность всех операций выполняется через за счет использования failureHandler.

Как это работает? В течении заданного тайм-аута (по умолчанию 5 секунд), при выполнении операции будут перехватываться следующие исключения

Для действий и проверок:
- PerformException
- NoMatchingViewException

Для проверок мы дополнительно перехватываеим AssertionFailedError и всех его наследников.

Действие будет повторяться каждые 50мс, пока не выполниться успешно или не достигнется тайм-аут.

Такой подход позволяет снизить flakiness ваших тестов и поднять их стабильность.

Вы можете отключить подобное поведение добавив перед тестом строки
```kotlin
ViewActionConfig.allowedExceptions.clear()// disable failure handler
ViewAssertionConfig.allowedExceptions.clear()// disable failure handler
```
Можете расширить список обрабатываемых исключений:
```kotlin
ViewActionConfig.allowedExceptions.add(AmbiguousViewMatcherException::class.java)
ViewAssertionConfig.allowedExceptions.add(AmbiguousViewMatcherException::class.java)
```
Можете поменять время тайм-аута действия и проверки:
```kotlin
ViewActionConfig.ACTION_TIMEOUT = 10_000L
ViewAssertionConfig.ASSERTION_TIMEOUT = 10_000L
```