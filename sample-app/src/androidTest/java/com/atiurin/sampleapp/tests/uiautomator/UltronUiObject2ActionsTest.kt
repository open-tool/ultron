package com.atiurin.sampleapp.tests.uiautomator

import android.view.ViewConfiguration
import android.widget.LinearLayout
import androidx.test.filters.FlakyTest
import androidx.test.platform.app.InstrumentationRegistry
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.activity.UiElementsActivity
import com.atiurin.sampleapp.framework.Log
import com.atiurin.sampleapp.framework.ultronext.appendText
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.framework.utils.TestDataUtils
import com.atiurin.sampleapp.pages.UiObject2ElementsPage
import com.atiurin.sampleapp.tests.UiElementsTest
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2.Companion.bySelector
import com.atiurin.ultron.utils.getTargetString
import org.junit.Assert
import org.junit.Test

class UltronUiObject2ActionsTest: UiElementsTest() {
    val page = UiObject2ElementsPage()

    //getParent
    @Test
    fun getParent_parentExist(){
        Assert.assertEquals(LinearLayout::class.qualifiedName, page.button.getParent()?.getClassName())
    }

    //getChildren
    @Test
    fun getChildren_returnsAllChildren(){
        val children = page.radioGroup.getChildren()
        Assert.assertEquals(3, children.size)
        var foundElements = 0
        children.forEach {
            val resName = it.getResourceName()!!
            Log.debug(">>> $resName")
            if (resName.endsWith("radio_visible")){
                foundElements++
                it.click()
                page.button.isDisplayed()
            } else if (resName.endsWith("radio_invisible")){
                foundElements++
                it.click()
                page.button.isNotDisplayed()
            } else if (resName.endsWith("radio_gone")) foundElements++
        }
        Assert.assertEquals(3, foundElements)
    }

    @Test
    fun getChildren_noChildExist(){
        val children = page.button.getChildren()
        Assert.assertTrue(children.isEmpty())
    }

    @Test
    fun getChildrenWithResultHandler(){
        page.button.getChildren()
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

    //findObject
    @Test
    fun findObject_existedChildObject(){
        val child = page.radioGroup.findObject(bySelector(R.id.radio_invisible))
        Assert.assertNotNull(child)
        child!!.click()
        page.button.isNotDisplayed()
    }

    @Test
    fun findObject_notExistedChildObject(){
        val child = page.radioGroup.findObject(bySelector(R.id.button1))
        Assert.assertNull(child)
    }

    @Test
    fun findObject_notExistedParentObject(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).findObject(bySelector(R.id.button1)) }
    }

    //findObjects
    @Test
    fun findObjects_existedChildObject(){
        val children = page.radioGroup.findObjects(bySelector(R.id.radio_invisible))
        Assert.assertEquals(1, children.size)
        children.forEach {
            it.click()
            page.button.isNotDisplayed()
        }
    }
    @Test
    fun findObjects_notExistedChildObject(){
        val children = page.radioGroup.findObjects(bySelector(R.id.button1))
        Assert.assertTrue(children.isEmpty())
    }

    //getText
    @Test
    fun getText_objectHasText(){
        Assert.assertEquals(getTargetString(R.string.button_text), page.button.getText())
    }

    @Test
    fun getText_objectHasNoText(){
        Assert.assertEquals(null, page.swipableImageView.getText())
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
        Assert.assertEquals(expected, page.button.getApplicationPackage())
    }

    @Test
    fun getApplicationPackage_notExistedObject(){
        AssertUtils.assertException {  page.notExistedObject.withTimeout(100).getApplicationPackage() }
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

    //getVisibleCenter
    @Test
    fun getVisibleCenter_existedObject(){
        val bounds = page.button.getVisibleBounds()!!
        val point = page.button.getVisibleCenter()!!
        Assert.assertEquals(bounds.exactCenterX().toInt(), point.x)
        Assert.assertEquals(bounds.exactCenterY().toInt(), point.y)
    }

    @Test
    fun getVisibleCenter_notExistedObject(){
        AssertUtils.assertException {  page.notExistedObject.withTimeout(100).getVisibleCenter() }
    }

    //getResourceName
    @Test
    fun getResourceName_existedResourceName(){
        val resName = page.button.getResourceName()
        val pkgName = InstrumentationRegistry.getInstrumentation().targetContext.applicationInfo.packageName
        Assert.assertEquals("$pkgName:id/button1",resName)
    }

    @Test
    fun getResourceName_notExistedResourceName(){
        Assert.assertNull(page.button.getParent()?.getResourceName())
    }

    @Test
    fun getResourceName_notExistedObject(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).getResourceName() }
    }

    //getContentDescription
    @Test
    fun getContentDescription_existedContentDescription(){
        val expectedContDesc = getTargetString(R.string.button_default_content_desc)
        Assert.assertEquals(expectedContDesc, page.button.getContentDescription())
    }

    @Test
    fun getContentDescription_notExistedResourceName(){
        Assert.assertNull(page.button.getParent()?.getContentDescription())
    }

    @Test
    fun getContentDescription_notExistedObject(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).getContentDescription() }
    }

    //click
    @Test
    fun click_onClickable() {
        page.button.click()
        page.eventStatus.textContains(TestDataUtils.getResourceString(R.string.button_event_click))
    }

    @Test
    fun click_withDuration_onClickable() {
        page.button.click(duration = 50)
        page.eventStatus.textContains(TestDataUtils.getResourceString(R.string.button_event_click))
    }

    @Test
    fun click_onNotExistedObject() {
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).click() }
    }
    //longClick
    @Test
    fun longClick_onLongClickable() {
        val duration = ViewConfiguration.getLongPressTimeout().toLong()
        page.button.click(duration*2)
        page.eventStatus.textContains(getTargetString(R.string.button_event_long_click))
    }

    @Test
    fun longClick_onNotExistedObject() {
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).longClick() }
    }

    //clear
    @Test
    fun clear_editableObject(){
        page.editTextContentDesc.clear().textIsNullOrEmpty()
    }

    @Test
    fun clear_UneditableObject(){
        val btnText = getTargetString(R.string.button_text)
        page.button.hasText(btnText).clear().hasText(btnText)
    }

    @Test
    fun clear_notExistedObject(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).clear() }
    }

    //addText
    @Test
    fun addText_toEditableObject(){
        val startText = "start "
        val textToAdd = "added new Text"
        page.editTextContentDesc
            .replaceText(startText)
            .addText(textToAdd)
            .hasText(startText + textToAdd)
    }

    @Test
    fun addText_toUneditableObject(){
        val btnText = getTargetString(R.string.button_text)
        page.button.addText("some new text").hasText(btnText)
    }

    @Test
    fun addText_toNotExistedObject(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).addText("asdasd") }
    }

    @Test
    fun legacySetText_toEditable(){
        val text = "text to replace "
        page.editTextContentDesc
            .legacySetText(text)
            .hasText(text)
    }

    @Test
    fun legacySetText_toUneditableObject(){
        AssertUtils.assertException { page.button.withTimeout(100).legacySetText("some new text") }
    }
    @Test
    fun legacySetText_toNotExistedObject(){
        AssertUtils.assertException { page.notExistedObject.withTimeout(100).legacySetText("some new text") }
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
        val btnText = getTargetString(R.string.button_text)
        page.button.replaceText("some new text").hasText(btnText)
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
    @FlakyTest
    @Test
    fun swipeUpTest(){
        page.eventStatus.hasText(getTargetString(R.string.button_text))
        page.editTextContentDesc.replaceText("some text")
        page.swipableImageView.isEnabled().isDisplayed().swipeUp(speed = 2000)
        page.eventStatus.textContains(UiElementsActivity.Event.SWIPE_UP.name)
    }

    @FlakyTest
    @Test
    fun swipeDownTest(){
//        Thread.sleep(2000)
        page.eventStatus.hasText(getTargetString(R.string.button_text))
        page.editTextContentDesc.replaceText("some text")
        page.swipableImageView.isEnabled().isDisplayed().swipeDown(speed = 2000)
        page.eventStatus.textContains(UiElementsActivity.Event.SWIPE_DOWN.name)
    }

    @Test
    @FlakyTest
    fun swipeRightTest(){
        page.eventStatus.hasText(getTargetString(R.string.button_text))
        page.editTextContentDesc.replaceText("some text")
        page.swipableImageView.isEnabled().isDisplayed().swipeRight(speed = 2000)
        page.eventStatus.textContains(UiElementsActivity.Event.SWIPE_RIGHT.name)
    }

    @Test
    @FlakyTest
    fun swipeLeftTest(){
        page.eventStatus.hasText(getTargetString(R.string.button_text))
        page.editTextContentDesc.replaceText("some text")
        page.swipableImageView.isEnabled().isDisplayed().swipeLeft(speed = 2000)
        page.eventStatus.textContains(UiElementsActivity.Event.SWIPE_LEFT.name)
    }

    @Test
    fun customAppendText_toEditableObject(){
        val startText = "start "
        val textToAdd = "added new Text"
        page.editTextContentDesc
            .replaceText(startText)
            .appendText(textToAdd)
            .hasText(startText + textToAdd)
    }


}