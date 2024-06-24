---
sidebar_position: 2
---

# WebView

There are 3 different objects to interact with.

* `UltronWebDocument` - wraps operations with WebView DOM document (execute JS script and etc).
* `UltronWebElement` - represents a DOM element. Provides operations with element (`webClick`, `replaceText`, `exists` etc)
* `UltronWebElements` - represents a list of similar WebElements.

## How to use?

### UltronWebDocument

It contains a set of static methods. For example
```kotlin
UltronWebDocument.evalJS("document.getElementById(\"title\").innerHTML = '$title';")
UltronWebDocument.assertThat(
            webContent(
                elementById(
                    "apple_link",
                    withTextContent("Apple")
                )
            )
        )
```
Full list:

```kotlin
forceJavascriptEnabled(webViewMatcher, timeoutMs, ..)            // performs a force enable of Javascript on a WebView
evalJS(script: String, webViewMatcher, timeoutMs, ..)            // evaluate JS on webView
assertThat(WebAssertion, webViewMatcher, ..)                     // use any webAssertion to assert it safely
selectActiveElement(..): ElementReference                        // finds the currently active element in the document
selectFrameByIndex(index: Int, ..): WindowReference              // selects a subframe of the currently selected window by it's index
selectFrameByIdOrName(idOrName: String, ..): WindowReference     // selects a subframe of the current window by it's name or id
```

### UltronWebElement
`UltronWebElement` has a list of factory methods that help us to create an instance of UltronWebElement. Full list is here - [UltronWebElement](https://github.com/open-tool/ultron/blob/603150ab12a703a19245ad08a48b036ce562dfd8/ultron/src/main/java/com/atiurin/ultron/core/espressoweb/webelement/UltronWebElement.kt#L311)

```kotlin
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebElement.Companion.id
//other imports

id("text_input").webKeys("Ultron")
className("css_button").webClick()
xpath("some_xpath_link").hasAttribute("href", "https://github.com/alex-tiurin/ultron")
```
It's preferable to use `id` or `xpath` to create `UltronWebElement` instance because they provide very profitable method `hasAttribute`

Full operations list

```kotlin
//actions
clearElement()                                // clears content from an editable element
replaceText(String)                           // simulates javascript clear and key events sent to a certain element
webKeys(String)                               // simulates javascript key events sent to a certain element
getText()                                     // returns the visible text beneath a given DOM element
webScrollIntoView()                           // executes scroll to view
webScrollIntoViewBoolean()                    // returns if the desired element is in view after scrolling
webClick()                                    // simulates the javascript events to click on a particular element

//assertions
containsText(String)                          // asserts that DOM element contains visible text beneath it self 
exists()                                      // asserts that element exists in webView
hasText(String)                               // asserts that DOM element has visible text beneath it self
hasAttribute(String, Matcher<String>)         // assert any html attribute value
assertThat(WebAssertion)                      // use any webAssertion to assert it safely 

isSuccess(block: UltronWebElement.() -> Unit) // transforms any action or assertion to Boolean value 
reset()                                       // removes the Element and Window references from this interaction
//------ general ------ 
withTimeout(timeoutMs: Long)                  // set custom timeout
withResultHandler(resultHandlerBlock)         // provides the ability to process operation result in custom way
withContextual(UltronWebElement)              // set a parent element
withAssertion(assertion: OperationAssertion)  // define custom assertion of action success
withAssertion(name: String = "", isListened: Boolean = false, block: () -> Unit)
```

### UltronWebElements

It helps to find similar elements.
```kotlin
classNames("link").getElements()
   .find { ultronWebElement ->
        ultronWebElement.isSuccess {
            withTimeout(100).hasText("Apple")
        }
   }?.webClick()
```
It has only 2 usable methods

```kotlin
getElements(): List<UltronWebElement>
getSize(): Int
```
## Boolean operation result

There is `isSuccess` method that allows us to get the result of any operation as boolean value. In case of false it could be executed to long (5 sec by default). So it reasonable to specify custom timeout for some operations.

```kotlin
val isWebElementExist = xpath("some_xpath").isSuccess { withTimeout(2_000).exists() }
if (isWebElementExist) {
    //do some reasonable actions
}
```
## Best practice

Specify web elements as properties of PageObject class.

```kotlin
object WebViewPage : Page<WebViewPage>() {
    private val button = id("button")
    private val textInput = id("text_input")
    private val title = xpath("some_xpath")
}
```

Use this properties in page steps
```kotlin
object WebViewPage : Page<WebViewPage>() {
    //page elements
    fun someUserStepOnWebView(expectedEventText: String){
         textInput.replaceText(expectedEventText)
         button.webClick()
         title.hasText(expectedEventText)
    }
}
```

## Extend framework with your own Web operations


It's described in another page [here](../common/extension.md#espresso-web)
