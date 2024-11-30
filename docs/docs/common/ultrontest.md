---
sidebar_position: 2
---

# UltronTest

`UltronTest` is a powerful base class provided by the Ultron framework that enables the definition of common preconditions and postconditions for tests. By extending this class, you can streamline test setup and teardown, ensuring consistent execution across your test suite.

## Features of `UltronTest`

- **Pre-Test Actions:** Define actions to be executed before each test.
- **Post-Test Actions:** Define actions to be executed after each test.
- **Lifecycle Management:** Execute code once before all tests in a class using `beforeFirstTest`.
- **Customizable Test Execution:** Suppress pre-test or post-test actions when needed.

### Example

Here is an example of using `UltronTest`:

```kotlin
class SampleUltronFlowTest : UltronTest() {

    @OptIn(ExperimentalUltronApi::class)
    override val beforeFirstTest = {
        UltronLog.info("Before Class")
    }

    override val beforeTest = {
        UltronLog.info("Before test common")
    }

    override val afterTest = {
        UltronLog.info("After test common")
    }

    /**
     * The order of method execution is as follows::
     * beforeFirstTest, beforeTest, before, go, after, afterTest
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
     * `beforeFirstTest` - Not executed, as it is only run once and was already executed before `someTest1`.
     * `beforeTest` - Not executed because it was suppressed using `suppressCommonBefore`.
     * `afterTest` - Not executed because it was suppressed using `suppressCommonAfter`.
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
     * `beforeFirstTest` - Not executed, since it was executed before `someTest1`
     */
    @Test
    fun someTest3() = test {
        UltronLog.info("UltronTest simpleTest")
    }
}
```

### Key Methods

- **`beforeFirstTest`**: Code executed once before all tests in a class.
- **`beforeTest`**: Code executed before each test.
- **`afterTest`**: Code executed after each test.
- **`test`**: Executes a test with options to suppress pre-test or post-test actions.

### Key Features of the `test` Method

- **Test Context Recreation:**  
  The `test` method automatically recreates the `UltronTestContext` for each test execution, ensuring a clean and isolated state for the test context.

- **Soft Assertion Reset:**  
  Any exceptions captured during `softAssertions` in the previous test are cleared at the start of each new `test` execution, maintaining a clean state.

- **Lifecycle Management:**
  It invokes `beforeTest` and `afterTest` methods around your test logic unless explicitly suppressed.
---

### Purpose of `before`, `go`, and `after`
- **`before`:** Defines preconditions or setup actions that must be performed before the main test logic is executed.
These actions might include preparing data, navigating to a specific screen, or setting up the environment.
  ```kotlin
  before {
      UltronLog.info("Setting up preconditions for TestMethod 2")
  }
  ```

- **`go`:** Encapsulates the core logic or actions of the test. This is where the actual operations being tested are performed, such as interacting with UI elements or executing specific functionality.
  ```kotlin
  go {
      UltronLog.info("Executing the main logic of TestMethod 2")
  }
  ```

- **`after`:** Block is used for postconditions or cleanup actions that need to occur after the main test logic has executed. This might include verifying results, resetting the environment, or clearing resources.
  ```kotlin
  after {
      UltronLog.info("Cleaning up after TestMethod 2")
  }
  ```

These methods help clearly separate test phases, making tests easier to read and maintain.

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

The `softAssertion` mechanism does not inherently depend on `UltronTest`.
You can use `softAssertion` independently of the `UltronTest` base class. However, in such cases, you must manually clear exceptions between tests to ensure they do not persist across test executions.
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

## Benefits of `UltronTest` usage

- Simplifies test setup and teardown with consistent preconditions and postconditions.
- Enhances error handling by allowing multiple assertions within a single test.
- Improves test readability and maintainability.

By leveraging `UltronTest` and `softAssertion`, you can build robust and flexible UI tests for your applications.

