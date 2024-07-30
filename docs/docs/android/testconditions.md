---
sidebar_position: 6
---

# Test Conditions Management

It is a feature that includes 3 parts

- RuleSequence
- SetUpRule & TearDownRule
- @SetUp @TearDown annotations

RuleSequence + SetUps & TearDowns for tests = full control of your tests

- control the execution of pre- and postconditions of each test
- control the moment of activity launching. It is one of the most  important point in android automation.
- don't write @Before and @After methods by changing it to the lambdas of SetUpRule or TearDownRule object
- combine conditions of your test using annotations

## RuleSequence

This rule is a modern replacement of JUnit 4 *RuleChain*. It allows to control an order of rules execution.

The RuleChain is not flexible. It is unpleasant to use RuleChain especially with class inheritance. That's why
[RuleSequence](https://github.com/alex-tiurin/ultron/blob/master/ultron/src/main/java/com/atiurin/ultron/testlifecycle/rulesequence/RuleSequence.kt)
has been created.

The order of rules execution depends on its addition order.
RuleSequence contains three rules lists with their own priority.
- first - rules from this list will be executed first of all
- normal - rules will be added to this list by default
- last - rules from this list will be executed last

It is recommended to create `RuleSequence` in `BaseTest`. You will be able to add rules to `RuleSequence` in `BaseTest` and in `BaseTest` subclasses.

```kotlin
abstract class BaseTest {
    val setupRule = SetUpRule(name = "some name").add {
            // some resonable precondition for all tests, eg login or smth like that
        }

    @get:Rule
    open val ruleSequence = RuleSequence(setupRule)
}
```
It's better to add rules in subclasses inside `init` section.
```kotlin
class DemoTest : BaseTest() {
    private val activityRule = ActivityScenarioRule(MainActivity::class.java)

    init {
        ruleSequence.addLast(activityRule)
    }
}
```
**Note**: while using `RuleSequence`(as it was with `RuleChain`) you don't need to specify `@get:Rule` annotation for other rules.

Full code sample:
- [BaseTest](https://github.com/alex-tiurin/ultron/blob/master/sample-app/src/androidTest/java/com/atiurin/sampleapp/tests/BaseTest.kt)
- [DemoEspressoTest](https://github.com/alex-tiurin/ultron/blob/master/sample-app/src/androidTest/java/com/atiurin/sampleapp/tests/espresso/DemoEspressoTest.kt)

To learn more about order of rules execution see [Deep dive into rules order with RuleSequence](https://github.com/alex-tiurin/ultron/wiki/Deep-dive-into-rules-order-with-RuleSequence)


## SetUpRule

This rule allows you to specify lambdas which will be definitely invoked before a test is started.
Moreover in combination with **RuleSequence** setup lambdas could be invoked before an activity is launched.

### Precondition for each tests

Add lambda to `SetUpRule` without any string key and it will be executed before each test in class.

```kotlin
open val setupRule = SetUpRule("Login user rule")
    .add(name = "Login valid user $CURRENT_USER") {
        Log.info("Login valid user will be executed before any test is started")
        AccountManager(InstrumentationRegistry.getInstrumentation().targetContext).login(
            CURRENT_USER.login, CURRENT_USER.password
        )
    }
```
### Precondition for specific test

1. add lambda with string key to `SetUpRule`
2. add `@SetUp` annotation with specified key to desired test

```kotlin
setupRule.add(FIRST_CONDITION){ 
    Log.info("$FIRST_CONDITION setup, executed for test with annotation @SetUp(FIRST_CONDITION)")  
}

@SetUp(FIRST_CONDITION)
@Test
fun someTest() {
    // some test steps
}
```

**Attention**: dont forget to add `SetUpRule` to `RuleSequence`

```kotlin
ruleSequence.add(setupRule)
```

## TearDownRule

This rule allows you to specify lambdas which will be definitely invoked after a test is finished.

### Postcondition for all tests

Add lambda to `TearDownRule` without any string key and it will be executed after each test in class.

```kotlin
open val tearDownRule = TearDownRule(name = "Logout user from app")
    .add {
        AccountManager(InstrumentationRegistry.getInstrumentation().targetContext).logout()
    }
```
### Postcondition for specific test

1. add lambda with string key to `TearDownRule`
2. add `@TearDown` annotation with specified key to desired test

```kotlin
tearDownRule.add (LAST_CONDITION){ 
    Log.info("$LAST_CONDITION tearDown, executed for test with annotation @TearDown(LAST_CONDITION)")  
}

@TearDown(LAST_CONDITION)
@Test
fun someTest() {
    // some test steps
}
```

**Attention**: dont forget to add `TearDownRule` to `RuleSequence`

```kotlin
ruleSequence.addLast(tearDownRule)
```

## Add your SetUps and TearDowns to Allure report

Lets clearly define a term **condition**. It's any code block that you've `add` for`SetUpRule` or `TearDownRule`.

For example:
```kotlin
SetUpRule(name = "sample set up").add { 
   //codition code
}
```
It's possible to add all SetUps and TearDowns to Allure report with applying a recommended config:

```kotlin
UltronAllureConfig.applyRecommended()
```

You can read about Allure configuration  [here](../common/allure.md)

What it gives us:

- Rule `name` param will be used as name of Allure step.

```kotlin
SetUpRule(name = "External step name").add {...}
```

- Condition `name` param will be used as a name of inner step

```kotlin
SetUpRule(name = "External step name").add(name = "Internal step name") { 
   //condition code
}
```

