# RuleSequence + SetUpTearDownRule => full control under your tests

Умело комбинируя эти 2 правила, вы можете:
- контролировать выполенние пред- и пост-условий для каждого теста
- контролировать момент запуска activity приложения.
- не писать методы @Before и @After, заменив их на добавление одной лямбды в SetUpTearDownRule
- добавлять сколько угодно правил SetUpTearDownRule внутрь RuleSequence,
  комбинирую условия для тестов желаемым образом.
- контролировать выполенние пред- и пост-условий для каждого теста
- контролировать момент запуска activity приложения.
- не писать методы @Before и @After, заменив их на добавление одной лямбды в SetUpTearDownRule
- добавлять сколько угодно правил SetUpTearDownRule внутрь RuleSequence,
  комбинирую условия для тестов желаемым образом.

## RuleSequence

Это правило, которе является удобной и продвинутой альтернативой х
*RuleChain* из JUnit 4. Оно позволяет выстраивать строгий порядок
выполнения правил.

 Корень проблемы лежит в том, что порядок запуска
правил в JUnit 4 определяется механизмом рефлексии JVM и по сути
сводится к зависимости порядка запуска от имени переменной, которое мы
присвоем объекту с аннотацией @Rule. В Junit 4, эту проблему решает
RuleChain. Но делает это он совершенно не удобным и не интуитивным
способом. RuleChain особенно плохо ложится в механизм наследования
классов. Поэтому и появился
[RuleSequence](https://github.com/alex-tiurin/espresso-page-object/blob/master/espressopageobject/src/main/java/com/atiurin/espressopageobject/testlifecycle/rulesequence/RuleSequence.kt).

Очередность выполнения правил в RuleSequence зависит от порядка их
добавления. При этом есть три списка правил с разным приоритетом:
- first - правила, добавленные в этот список, выполняются всегда раньше
  остальных
- normal - в этот список правила добаляются по умолчанию
- last - эти правила всегда выполняются последними

См. полный код с примером использования в следующих классах:
- [BaseTest](https://github.com/alex-tiurin/espresso-page-object/blob/master/app/src/androidTest/java/com/atiurin/espressopageobjectexample/tests/BaseTest.kt)
- [DemoEspressoTest](https://github.com/alex-tiurin/espresso-page-object/blob/master/app/src/androidTest/java/com/atiurin/espressopageobjectexample/tests/DemoEspressoTest.kt)

## SetUpTearDownRule

Это правило позволяет добавлять ламбда-выражения, которые гарантированно
будут выдолнены до начала теста и после его окончания, вне зависимасти
от успешности прохождения теста. Более того, в комбинации с
[RuleSequence](https://github.com/alex-tiurin/espresso-page-object/blob/master/espressopageobject/src/main/java/com/atiurin/espressopageobject/testlifecycle/rulesequence/RuleSequence.kt)
эти лямбды гарантированно могут выполнятся до старта activity
приложения. Больше не надо писать
`activityRule.launchActivity(Intent())`

Для того, чтобы задать лямбду, которая будет выполняться для всех
тестов, необходимо добавить ее без ключа к правилу.


```kotlin
    open val setupRule = SetUpTearDownRule()
        .addSetUp {
            Log.info("Login valid user will be executed before any test is started")
            AccountManager(InstrumentationRegistry.getInstrumentation().targetContext).login(
                CURRENT_USER.login, CURRENT_USER.password
            )
        }
```

Елси вам нужно задать лямду, которая выполняется для определенных
тестов, необходимо сделать следующее:
1. добавить лямбду со строковым ключом к объекту SetUpTearDownRule
2. добавить желаемому тесту аннотацию SetUp и указать в качестве value
   ключ ламбда-выражения.

```kotlin
    setupRule.addSetUp (FIRST_CONDITION){ Log.info("$FIRST_CONDITION setup, executed for test with annotation @SetUp(FIRST_CONDITION)")  }

    @SetUp(FIRST_CONDITION)
    @Test
    fun friendsItemCheck() {
        FriendsListPage.assertStatus("Janice", "Oh. My. God")
    }
```

Такой же подход работает и для TearDown ламбда-выражений. В приведенном
ниже примере, обе лямбды будут выполнены после завершения теста
**testWithTearDown**.

```kotlin
    @get:Rule
    open val setupRule = SetUpTearDownRule()
            .addTearDown { Log.info("Common setup for all @Tests") }
            .addTearDown(SECOND_CONDITION) {Log.info("$SECOND_CONDITION teardowm executed last")}

    @TearDown(SECOND_CONDITION)
    @Test
    fun testWithTearDown() {
        FriendsListPage.assertStatus("Janice", "Oh. My. God")
    }
```
Порядок выполнения лямбда-выражений зависит от порядка их добавления к
объекту **SetUpTearDownRule**

*Причемание: в аннотациях @SetUp и @TearDown можно указывать массив ключей лямбда-выражений.*

```kotlin
@SetUp(FIRST_CONDITION, SECOND_CONDITION)
@TearDown(FIRST_CONDITION, SECOND_CONDITION)
```

Полный пример кода см.
[DemoEspressoTest](https://github.com/alex-tiurin/espresso-page-object/blob/master/app/src/androidTest/java/com/atiurin/espressopageobjectexample/tests/DemoEspressoTest.kt)
и [BaseTest](https://github.com/alex-tiurin/espresso-page-object/blob/master/app/src/androidTest/java/com/atiurin/espressopageobjectexample/tests/BaseTest.kt)

Чтобы полностью понять как это работает, рекоммендую запустить тесты
класса *DemoEspressoTest* и посмотреть logcat c tag =
**EspressoPageObject**.