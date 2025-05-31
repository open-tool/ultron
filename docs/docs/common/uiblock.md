---
sidebar_position: 2
---

# UI Block

UI blocks are a powerful tool for describing and interacting with user interface elements. They allow you to define UI elements within the context of their parent blocks, rather than the entire screen, which makes tests more readable, maintainable, and reliable.

For example, consider a UI block that represents a user’s name and status. We can define this block once and reuse it across different screens.

![UI Block](/img/uiblock.png)

We can describe this block and use it on different screens. 

_Supported: Compose (CMP & Android), Espresso, Espresso Web, UiAutomator (UiObject2)_

## Compose
***
Create a class that inherits from `UltronComposeUiBlock`.

```kotlin

class ContactCard(blockMatcher: SemanticsMatcher, blockDescription: String) 
    : UltronComposeUiBlock(blockMatcher, blockDescription) {
    val name = child(hasTestTag(contactNameTag)).withName("Name in '$blockDescription'")
    val status = child(hasTestTag(contactStatusTag))
}
```

`UltronComposeUiBlock` accepts two parameters:

- `blockMatcher` – describes how to locate this block in the Compose element tree. This is a required parameter and must always be provided.
- `blockDescription` – a description of the block that clearly identifies the UI container. This parameter is optional, with a default value of `blockDescription = ""`.

**Note**: To describe child elements of a UI block, you need to use the `child()` method.

As shown in the example above, we added a custom name to the `name` field. If an error occurs, this name will appear in the description of the element we tried to interact with. We recommend including the value of `blockDescription` in the element description. This provides better context about the specific element being checked (or any other operation performed).

The next step is to integrate the block into a screen.

```kotlin
object SomeComposeScreen : Screen<SomeComposeScreen>(){
    val card = ContactCard(hasTestTag(contactCardTag), "SomeComposeScreen contact card")
    
    fun assertContactCard(contact: Contact){
        softAssertion {
            card.name.assertTextEquals(contact.name)
            card.status.assertTextEquals(contact.status)
        }
    }
}
```

As seen in `SomeComposeScreen`, we no longer need to know how to locate `name` and `status`. It's enough to describe how to locate the parent UI block – `ContactCard`.

In addition to individual UI elements, child blocks can also represent other UI blocks. To describe a child UI block, you can use one of the overloaded `child` methods.

- In Multiplatform, only the method requiring an explicit approach to creating the child block is available.
- In Android, you can simplify this further using reflection.

### Compose Multiplatform

```kotlin
class ProfileBlock(blockMatcher: SemanticsMatcher, blockDescription: String) 
    : UltronComposeUiBlock(blockMatcher, blockDescription) {
    val card = child(
        childMatcher = hasTestTag(contactCardTag),
        uiBlockFactory = { updatedMatcher ->
            ContactCard(
                blockMatcher = updatedMatcher, 
                blockDescription = "Contact card '$blockDescription'"
            )
        }
    )
}
```

This method offers greater flexibility for creating child UI blocks.  
`updatedMatcher` – an updated matcher used to locate the `ContactCard` only within the `ProfileBlock`.

### Compose Android Only

Reflection capabilities in Android are more advanced than in Multiplatform, allowing for simpler descriptions of child UI blocks.

```kotlin
class ProfileBlock(blockMatcher: SemanticsMatcher, blockDescription: String) 
    : UltronComposeUiBlock(blockMatcher, blockDescription) {
    val card = child(
        ContactCard(
            blockMatcher = hasTestTag(contactCardTag), 
            blockDescription = "Contact card '$blockDescription'"
        )
    )
}
```

There are limitations to using this method:

The class must meet the following conditions to be instantiated:
1. It must not be a nested or inner class. It should be defined at the top level or as a file-level class.
2. It must have one of the following constructors:
    - A constructor with one parameter of type *SemanticsMatcher*.
    - A constructor with two parameters: `blockMatcher` of type *SemanticsMatcher* and `blockDescription` of type *String*.

We can use the `ProfileBlock` on the screen.

```kotlin
object SomeComposeScreen : Screen<SomeComposeScreen>(){
    val profile = ProfileBlock(hasTestTag(profileTag), "SomeComposeScreen profile card")
    
    fun assertContactCardInProfile(contact: Contact){
        softAssertion {
            profile.card.name.assertTextEquals(contact.name)
            profile.card.status.assertTextEquals(contact.status)
        }
    }
}
```

The `UltronComposeUiBlock` class has a `uiBlock` property, which facilitates proper interaction with block elements.

```kotlin
object SomeComposeScreen : Screen<SomeComposeScreen>(){
    val profile = ProfileBlock(hasTestTag(profileTag), "SomeComposeScreen profile block")
    
    fun assertProfileContactIsDisplayed(){
        profile.card.uiBlock.assertIsDisplayed()
    }
}
```

## Espresso
***

Create a class that inherits from `UltronEspressoUiBlock`

```kotlin
class ContactCard(blockMatcher: Matcher<View>, blockDescription: String) 
    : UltronEspressoUiBlock(blockMatcher, blockDescription) {
    val name = child(withId(R.id.name)).withName("Name in '$blockDescription'")
    val status = child(withId(R.id.name))
}
```
Add the block to the screen.

```kotlin
object SomeEspressoScreen : Screen<SomeEspressoScreen>(){
    val card = ContactCard(withId(R.id.card), "SomeComposeScreen contact card")
    
    fun assertContactCard(contact: Contact){
        softAssertion {
            card.name.hasText(contact.name)
            card.status.hasText(contact.status)
        }
    }
}
```
Using reflection simplifies the implementation of child UI blocks by automating instantiation under specific conditions.

Child UI block with reflection. 
```kotlin
class ProfileBlock(blockMatcher: Matcher<View>, blockDescription: String) 
    : UltronEspressoUiBlock(blockMatcher, blockDescription) {
    val card = child(
        ContactCard(
            blockMatcher = withId(R.id.contactCard), 
            blockDescription = "Contact card of '$blockDescription'"
        )
    )
}
```
Child UI block with factory method
```kotlin
class ProfileBlock(blockMatcher: Matcher<View>, blockDescription: String) 
    : UltronEspressoUiBlock(blockMatcher, blockDescription) {
    val card = child(
        childMatcher = withId(R.id.contactCard),
        uiBlockFactory = { updatedMatcher ->
            ContactCard(
                blockMatcher = updatedMatcher, 
                blockDescription = "Contact card '$blockDescription'"
            )
        }
    )
}
```
Define block on screen
```kotlin
object SomeEspressoScreen : Screen<SomeEspressoScreen>(){
    val profile = ProfileBlock(withId(R.id.profileBlock), "SomeEspressoScreen profile block")
    
    fun assertContactCardInProfile(contact: Contact){
        softAssertion {
            profile.uiBlock.isDisplayed()
            profile.card.uiBlock.isDisplayed()
            profile.card.name.hasText(contact.name)
            profile.card.status.hasText(contact.status)
        }
    }
}
```

## Espresso Web
***

Create a class that inherits from `UltronWebElementUiBlock`

```kotlin
class WebContactCard(blockElement: UltronWebElement, blockDescription: String)
    : UltronWebElementUiBlock(blockElement, blockDescription){
    val name = child(id("name")).withName("Name in '$blockDescription'")
    val status = child(className("status"))
}
```
Add the block to the screen.

```kotlin
object SomeWebScreen : Screen<SomeWebScreen>(){
    val card = WebContactCard(id("card"), "SomeWebScreen contact card")
    
    fun assertContactCard(contact: Contact){
        softAssertion {
            card.name.hasText(contact.name)
            card.status.hasText(contact.status)
        }
    }
}
```
Child UI block with reflection
```kotlin
class WebProfileBlock(blockMatcher: UltronWebElement, blockDescription: String) 
    : UltronWebElementUiBlock(blockMatcher, blockDescription) {
    val card = child(
        WebContactCard(
            blockElement = id("card"), 
            blockDescription = "Contact card of '$blockDescription'"
        )
    )
}
```
Child UI block with factory method
```kotlin
class WebProfileBlock(blockMatcher: UltronWebElement, blockDescription: String) 
    : UltronWebElementUiBlock(blockMatcher, blockDescription) {
    val card = child(
        childMatcher = id("card"),
        uiBlockFactory = { updatedElement ->
            ContactCard(
                blockElement = updatedElement, 
                blockDescription = "Contact card '$blockDescription'"
            )
        }
    )
}
```

## UiAutomator
***
Only **UiObject2** is supported.

Create a class that inherits from `UltronUiObject2UiBlock`

```kotlin
class UiAutomatorContactCard(blockDesc: String, blockSelector: () -> BySelector)
    : UltronUiObject2UiBlock(blockDesc, blockSelector){
    val name = child(bySelector(R.id.name)).withName("Name in '$blockDesc'")
    val status = child(By.desc("status content desc"))
}
```
Add the block to the screen.

```kotlin
object SomeUiAutomatorScreen : Screen<SomeUiAutomatorScreen>(){
    val card = UiAutomatorContactCard(
       blockDesc="SomeUiAutomatorScreen contact card",
       blockSelector=bySelector(R.id.card)
    )
    
    fun assertContactCard(contact: Contact){
        softAssertion {
            card.name.hasText(contact.name)
            card.status.hasText(contact.status)
        }
    }
}
```
Child UI block with reflection
```kotlin
class UiAutomatorProfileBlock(blockDesc: String, blockSelector: () -> BySelector)
   : UltronUiObject2UiBlock(blockDesc, blockSelector){
    val card = child(
        UiAutomatorContactCard(
            blockDesc = "Contact card of '$blockDesc'",
            blockSelector = { bySelector(R.id.card) }
        )
    )
}
```
Child UI block with factory method
```kotlin
class UiAutomatorProfileBlock(blockDesc: String, blockSelector: () -> BySelector)
   : UltronUiObject2UiBlock(blockDesc, blockSelector){
    val card = child(
        selector = bySelector(R.id.card),
        description = "Contact card of '$desc'",
        uiBlockFactory = { desc, selector ->
            UiAutomatorContactCard(desc, selector)
        }
    )
}
```