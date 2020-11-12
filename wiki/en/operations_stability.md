# Reduce flakiness of all actions and assertions

We reduce flakiness of all operation because of using failureHandler

How does it work? Following exceptions will be handled during timeout (by default 5 seconds)

For actions and assertions:
- PerformException 
- NoMatchingViewException

for assertions we additionally handle AssertionFailedError and it descendants.

Action/assertion will be repeated every 50ms while it won't be successfully executed or timeout will be reached.

This approach allows us to reduce test flakiness.

It is possible to turn off this logic by adding next lines before test:

```kotlin
ViewActionConfig.allowedExceptions.clear()// disable failure handler
ViewAssertionConfig.allowedExceptions.clear()// disable failure handler
```

You can extend the list of caught exceptions:
```kotlin
ViewActionConfig.allowedExceptions.add(AmbiguousViewMatcherException::class.java)
ViewAssertionConfig.allowedExceptions.add(AmbiguousViewMatcherException::class.java)
```
You can change the timeout value for actions and assertions:
```kotlin
ViewActionConfig.ACTION_TIMEOUT = 10_000L
ViewAssertionConfig.ASSERTION_TIMEOUT = 10_000L
```