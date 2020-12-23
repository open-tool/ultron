package com.atiurin.ultron.custom.espresso.matcher

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.w3c.dom.Element

open class ElementWithAttributeMatcher (val attributeName: String, val attributeValueMatcher: Matcher<String>) :
    TypeSafeMatcher<Element>() {
    override fun matchesSafely(element: Element): Boolean {
        return attributeValueMatcher.matches(element.getAttribute(attributeName))
    }

    override fun describeTo(description: Description) {
        description.appendText("with text content: ")
        attributeValueMatcher.describeTo(description)
    }

    companion object {
        fun withAttribute(attributeName: String, attributeValueMatcher: Matcher<String>)
                = ElementWithAttributeMatcher(attributeName, attributeValueMatcher)
    }
}

