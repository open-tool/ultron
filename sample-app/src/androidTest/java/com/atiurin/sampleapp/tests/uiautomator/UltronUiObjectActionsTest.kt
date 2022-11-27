package com.atiurin.sampleapp.tests.uiautomator

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiSelector
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.activity.UiElementsActivity
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.framework.utils.TestDataUtils
import com.atiurin.sampleapp.pages.UiObjectElementsPage
import com.atiurin.sampleapp.tests.UiElementsTest
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.uiautomator.uiobject.UltronUiObject.Companion.uiSelector
import com.atiurin.ultron.utils.getTargetResourceName
import com.atiurin.ultron.utils.getTargetString
import org.junit.Assert
import org.junit.Test

class UltronUiObjectActionsTest: UiElementsTest() {
    val page = UiObjectElementsPage()
    //getChild
    @Test
    fun getChild_existedChild(){
        val child = page.radioGroup.getChild(uiSelector(R.id.radio_gone))
        Assert.assertNotNull(child)
        child.click()
    }

    @Test
    fun getChild_notExistedChild(){
        val child = page.radioGroup.getChild(uiSelector(R.id.button1))
        Assert.assertNotNull(child)
        AssertUtils.assertException { child.withTimeout(100).click() }
    }

    //getChildCount
    @Test
    fun getChildCount_childExist(){
        Assert.assertEquals(3, page.radioGroup.getChildCount())
    }

    @Test
    fun getChildCount_noChildExist(){
        Assert.assertEquals(0, page.button.getChildCount())
    }

    //getFromParent
    @Test
    fun findObject_existedChildObject(){
        val child = page.radioGroup.getFromParent(uiSelector(R.id.radio_invisible))
        Assert.assertNotNull(child)
        child.click()
        page.button.notExists()
    }

    @Test
    fun getFromParent_notExistedChildObject(){
        val child = page.radioGoneButton.getFromParent(uiSelector(R.id.button1))
        Assert.assertNotNull(child)
        AssertUtils.assertException { child.withTimeout(100).click() }
    }

    //getText
    @Test
    fun getText_objectHasText(){
        Assert.assertEquals(getTargetString(R.string.button_text), page.button.getText())
    }

    @Test
    fun getText_objectHasNoText(){
        Assert.assertTrue( page.swipableImageView.getText()!!.isEmpty())
    }

    @Test
    fun getText_notExistedObject(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).getText() }
    }

    //getClassName
    @Test

    fun getClassName_existObject(){
        Assert.assertEquals("android.widget.Button", page.button.getClassName())
    }

    @Test
    fun getClassName_notExistObject(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).getClassName() }
    }

    //getApplicationPackage
    @Test
    fun getApplicationPackage_existedObject(){
        val expected = InstrumentationRegistry.getInstrumentation().targetContext.applicationInfo.packageName
        Assert.assertEquals(expected, page.button.getPackageName())
    }

    @Test
    fun getApplicationPackage_notExistedObject(){
        AssertUtils.assertException {  page.notExistedObject.withTimeout(100).getPackageName() }
    }

    //getVisibleBounds
    @Test
    fun getVisibleBounds_existedObject(){
        Assert.assertNotNull(page.button.getVisibleBounds())
    }

    @Test
    fun getVisibleBounds_notExistedObject(){
        AssertUtils.assertException {  page.notExistedObject.withTimeout(100).getVisibleBounds() }
    }

    //getContentDescription
    @Test
    fun getContentDescription_existedContentDescription(){
        val expectedContDesc = getTargetString(R.string.button_default_content_desc)
        Assert.assertEquals(expectedContDesc, page.button.getContentDescription())
    }

    @Test
    fun getContentDescription_notExistedResourceName(){
        Assert.assertTrue(page.checkBoxClickable.getContentDescription()!!.isEmpty())
    }

    @Test
    fun getContentDescription_notExistedObject(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).getContentDescription() }
    }

    //click
    @Test
    fun click_onClickable() {
        page.button.exists().click()
        page.eventStatus.textContains(TestDataUtils.getResourceString(R.string.button_event_click))
    }

    @Test
    fun click_withDuration_onClickable() {
        page.button.exists().click()
        page.eventStatus.textContains(TestDataUtils.getResourceString(R.string.button_event_click))
    }

    @Test
    fun click_onNotExistedObject() {
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).click() }
    }
    //longClick
    @Test
    fun longClick_onLongClickable() {
        page.button.exists().longClick()
        page.eventStatus.textContains(getTargetString(R.string.button_event_long_click))
    }

    @Test
    fun longClick_onNotExistedObject() {
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).longClick() }
    }

    @Test
    fun legacySetText_toEditable(){
        val text = "text to replace "
        page.editTextContentDesc
            .clearTextField()
            .legacyAddText(text)
            .hasText(text)
    }

    @Test
    fun legacySetText_toNotExistedObject(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).legacyAddText("some new text") }
    }
    //replaceText
    @Test
    fun replaceText_toEditable(){
        val text = "replaceText to new"
        page.editTextContentDesc
            .replaceText(text)
            .hasText(text)
    }
    @Test
    fun replaceText_toUneditableObject(){
        AssertUtils.assertException { page.button.withTimeout(100).replaceText("some new text") }
    }

    @Test
    fun replaceText_toNotExistedObject(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).replaceText("some new text") }
    }

    //perform
    @Test
    fun perform_existedObject(){
        page.button.perform({ this.click() }, "Click on button")
        page.eventStatus.textContains(TestDataUtils.getResourceString(R.string.button_event_click))
    }

    @Test
    fun perform_notExistedObject(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).perform({this.click()}, "Click on button") }
    }

    //swipe
    @Test
    fun swipeUpTest(){
        page.eventStatus.hasText(getTargetString(R.string.button_text))
        page.swipableImageView.exists().isEnabled().swipeUp(40)
        page.eventStatus.textContains(UiElementsActivity.Event.SWIPE_UP.name)
    }

    @Test
    fun swipeDownTest(){
        page.eventStatus.hasText(getTargetString(R.string.button_text))
        page.swipableImageView.exists().isEnabled().swipeDown(40)
        page.eventStatus.textContains(UiElementsActivity.Event.SWIPE_DOWN.name)
    }

    @Test
    fun swipeRightTest(){
        page.eventStatus.hasText(getTargetString(R.string.button_text))
        page.swipableImageView.exists().swipeRight(40)
        page.eventStatus.textContains(UiElementsActivity.Event.SWIPE_RIGHT.name)
    }

    @Test
    fun swipeLeftTest(){
        page.eventStatus.hasText(getTargetString(R.string.button_text))
        page.swipableImageView.exists().swipeLeft(40)
        page.eventStatus.textContains(UiElementsActivity.Event.SWIPE_LEFT.name)
    }
}