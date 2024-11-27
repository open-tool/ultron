---
sidebar_position: 2
---

# UltronTest

Using `UltronTest` as a Base Class for Tests

`UltronTest` is a powerful base class provided by the Ultron framework that enables the definition of common preconditions and postconditions for tests. By extending this class, you can streamline test setup and teardown, ensuring consistent execution across your test suite.

## Features of `UltronTest`

- **Pre-Test Actions:** Define actions to be executed before each test.
- **Post-Test Actions:** Define actions to be executed after each test.
- **Lifecycle Management:** Execute code once before all tests in a class using `beforeFirstTests`.
- **Customizable Test Execution:** Suppress pre-test or post-test actions when needed.

### Example

Here is an example of using `UltronTest`:

```kotlin
class SampleUltronFlowTest : UltronTest() {

    @OptIn(ExperimentalUltronApi::class)
    override val beforeFirstTests = {
        UltronLog.info("Before Class")
    }

    override val beforeTest = {
        UltronLog.info("Before test common")
    }

    override val afterTest = {
        UltronLog.info("After test common")
    }

    /**
     * An order of methods execution is follow:
     * beforeFirstTests, beforeTest, before, go, after, afterTest
     */
    @Test
    fun someTest1() = test {
        before {
            UltronLog.info("Before TestMethod 1")
        }.go {
            UltronLog.info("Run TestMethod 1")
        }.after {
            UltronLog.info("After TestMethod 1")
        }
    }
    
    /**
     * An order of methods execution is follow: before, go, after
     * `beforeFirstTests` - not executed, since it was executed before `someTest1`
     * `beforeTest` - not executed, as it was suppressed
     * `afterTest` - not executed, as it was suppressed
     */
    @Test
    fun someTest2() = test(
        suppressCommonBefore = true,
        suppressCommonAfter = true
    ) {
        before {
            UltronLog.info("Before TestMethod 2")
        }.go {
            UltronLog.info("Run TestMethod 2")
        }.after {
            UltronLog.info("After TestMethod 2")
        }
    }

    /**
     * An order of methods execution is follow: beforeTest, test, afterTest
     * `beforeFirstTests` - not executed, since it was executed before `someTest1`
     */
    @Test
    fun someTest3() = test {
        UltronLog.info("UltronTest simpleTest")
    }
}
```

### Key Methods

- **`beforeFirstTests`**: Code executed once before all tests in a class.
- **`beforeTest`**: Code executed before each test.
- **`afterTest`**: Code executed after each test.
- **`test`**: Executes a test with options to suppress pre-test or post-test actions.

---

## Using `softAssertion` for Flexible Error Handling

The `softAssertion` mechanism in Ultron allows tests to catch and verify multiple exceptions during their execution without failing immediately. This feature is particularly useful for validating multiple conditions within a single test.
### Example of `softAssertion`

```kotlin
class SampleTest : UltronTest() {
    @Test
    fun softAssertionTest() {
        softAssertion(failOnException = false) {
            hasText("NotExistText").withTimeout(100).assertIsDisplayed()
            hasTestTag("NotExistTestTag").withTimeout(100).assertHasClickAction()
        }
        verifySoftAssertions()
    }
}
```

The `softAssertion` is not generally depends on `UltronTest`.
You can use it outside `UltronTest` base class. But you have to clear exceptions between tests
```kotlin
class SampleTest {
    @Test
    fun softAssertionTest() {
        UltronCommonConfig.testContext.softAnalyzer.clear()
        softAssertion() {
            //assert smth
        }
    }
}
```

### Explanation

- **Fail on Exception:** By default (`failOnException = true`), `softAssertion` will throw an exception after completing all operations within its block if any failures occur.
- **Manual Verification:** If `failOnException` is set to `false`, you can explicitly verify all caught exceptions at the end of the test using `verifySoftAssertions()`.

This approach ensures granular control over how exceptions are handled and reported, making it easier to analyze and debug test failures.

---

## Benefits

- Simplifies test setup and teardown with consistent preconditions and postconditions.
- Enhances error handling by allowing multiple assertions within a single test.
- Improves test readability and maintainability.

By leveraging `UltronTest` and `softAssertion`, you can build robust and flexible UI tests for your applications.

