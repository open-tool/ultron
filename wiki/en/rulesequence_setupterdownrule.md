# RuleSequence + SetUpTearDownRule => full control under your tests

- control the execution of pre- and postconditions of each test
- control the moment of activity launching. It is one of the most  important point in android automation.
- don't write @Before and @After methods by changing it to the single
  lambda of SetUpTearDownRule object
- combine conditions of your test in unlimited number of SetUpTearDownRule objects and add  them to RuleSequence

## RuleSequence

This rule is a modern replacement of JUnit 4 *RuleChain*. It allows to
control an order of rules execution.

The RuleChain is not flexible.It is unpleasant to use RuleChain
especially with class inheritance. That's why
[RuleSequence](https://github.com/alex-tiurin/espresso-page-object/blob/master/espressopageobject/src/main/java/com/atiurin/espressopageobject/testlifecycle/rulesequence/RuleSequence.kt)
has been created.

The order of rules execution depends on its addition order.
RuleSequence contains three rules lists with their own priority.
- first - rules from this list will be executed first of all
- normal - rules will be added to this list by default
- last - rules from this list will be executed last

Full code sample:
- [BaseTest](https://github.com/alex-tiurin/espresso-page-object/blob/master/app/src/androidTest/java/com/atiurin/espressopageobjectexample/tests/BaseTest.kt)
- [DemoEspressoTest](https://github.com/alex-tiurin/espresso-page-object/blob/master/app/src/androidTest/java/com/atiurin/espressopageobjectexample/tests/DemoEspressoTest.kt)

## SetUpTearDownRule

This rule allows you to specify lambdas which will be definitely invoked
before a test is started and after the test is finished (whether passing
or failing). Moreover in combination with
[RuleSequence](https://github.com/alex-tiurin/espresso-page-object/blob/master/espressopageobject/src/main/java/com/atiurin/espressopageobject/testlifecycle/rulesequence/RuleSequence.kt)
setup lambdas could be invoked before an activity is launched. So, there
is no need to call `activityRule.launchActivity(Intent())`

To setup a lambda for all tests add it without any string key

```kotlin
    open val setupRule = SetUpTearDownRule()
        .addSetUp {
            Log.info("Login valid user will be executed before any test is started")
            AccountManager(InstrumentationRegistry.getInstrumentation().targetContext).login(
                CURRENT_USER.login, CURRENT_USER.password
            )
        }
```

In case you would like to add lambda for specific test:
1. add lambda with string key to SetUpTearDownRule
2. add setup annotation with specified key to desired test.

```kotlin
    setupRule.addSetUp (FIRST_CONDITION){ Log.info("$FIRST_CONDITION setup, executed for test with annotation @SetUp(FIRST_CONDITION)")  }

    @SetUp(FIRST_CONDITION)
    @Test
    fun friendsItemCheck() {
        FriendsListPage.assertStatus("Janice", "Oh. My. God")
    }
```

The same approach works for TearDown lambdas. In a case below both
lambdas will will be invoked after test **testWithTearDown** will have
been finished.

```kotlin
    open val setupRule = SetUpTearDownRule()
            .addTearDown { Log.info("Common setup for all @Tests") }
            .addTearDown(SECOND_CONDITION) {Log.info("$SECOND_CONDITION teardowm executed last")}

    @TearDown(SECOND_CONDITION)
    @Test
    fun testWithTearDown() {
        FriendsListPage.assertStatus("Janice", "Oh. My. God")
    }
```

The order of lambdas execution depends on its addition order to the rule.

*Note: you can specify several lambdas for single test in @SetUp and
@TearDown*

```kotlin
@SetUp(FIRST_CONDITION, SECOND_CONDITION)
@TearDown(FIRST_CONDITION, SECOND_CONDITION)
```

Full code sample
[DemoEspressoTest](https://github.com/alex-tiurin/espresso-page-object/blob/master/app/src/androidTest/java/com/atiurin/espressopageobjectexample/tests/DemoEspressoTest.kt)
and
[BaseTest](https://github.com/alex-tiurin/espresso-page-object/blob/master/app/src/androidTest/java/com/atiurin/espressopageobjectexample/tests/BaseTest.kt)

To definitely understand how it works you can run tests of
*DemoEspressoTest* class and watch logcat output with tag =
**EspressoPageObject**.
