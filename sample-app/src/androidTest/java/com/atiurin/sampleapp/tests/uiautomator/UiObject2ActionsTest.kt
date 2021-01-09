package com.atiurin.sampleapp.tests.uiautomator

import android.widget.LinearLayout
import androidx.test.platform.app.InstrumentationRegistry
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.framework.Log
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.framework.utils.TestDataUtils
import com.atiurin.sampleapp.pages.UiObject2ElementsPage
import com.atiurin.sampleapp.tests.UiElementsTest
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2.Companion.byResId
import com.atiurin.ultron.utils.getTargetString
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class UltronUiObject2ActionsTest: UiElementsTest() {
    val page = UiObject2ElementsPage()

    @Before
    fun speedUpAutomator(){
//        UltronConfig.UiAutomator.UIAUTOMATOR_OPERATION_POLLING_TIMEOUT = 50L
        UltronConfig.UiAutomator.speedUp()
    }
    //getParent
    @Test
    fun getParent_parentExist(){
        Assert.assertEquals(LinearLayout::class.qualifiedName, page.button.getParent()?.getClassName())
    }

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

    //getChildren
    @Test
    fun getChildren_noChildExist(){
        val children = page.button.getChildren()
        Assert.assertTrue(children.isEmpty())
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
        val child = page.radioGroup.findObject(byResId(R.id.radio_invisible))
        Assert.assertNotNull(child)
        child!!.click()
        page.button.isNotDisplayed()
    }

    @Test
    fun findObject_notExistedChildObject(){
        val child = page.radioGroup.findObject(byResId(R.id.button1))
        Assert.assertNull(child)
    }

    @Test
    fun findObject_notExistedParentObject(){
        AssertUtils.assertException { page.notExistedObject.findObject(byResId(R.id.button1), timeoutMs = 100) }
    }

    //findObjects
    @Test
    fun findObjects_existedChildObject(){
        val children = page.radioGroup.findObjects(byResId(R.id.radio_invisible))
        Assert.assertEquals(1, children.size)
        children.forEach {
            it.click()
            page.button.isNotDisplayed()
        }
    }
    @Test
    fun findObjects_notExistedChildObject(){
        val children = page.radioGroup.findObjects(byResId(R.id.button1))
        Assert.assertTrue(children.isEmpty())
    }

    //getApplicationPackage
    @Test
    fun getApplicationPackage_existedObject(){
        val expected = InstrumentationRegistry.getInstrumentation().targetContext.applicationInfo.packageName
        Assert.assertEquals(expected, page.button.getApplicationPackage())
    }

    @Test
    fun getApplicationPackage_notExistedObject(){
        AssertUtils.assertException {  page.notExistedObject.getApplicationPackage(100) }
    }

    //getVisibleBounds
    @Test
    fun getVisibleBounds_existedObject(){
        Assert.assertNotNull(page.button.getVisibleBounds())
    }

    @Test
    fun getVisibleBounds_notExistedObject(){
        AssertUtils.assertException {  page.notExistedObject.getVisibleBounds() }
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
        AssertUtils.assertException {  page.notExistedObject.getVisibleCenter() }
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
        AssertUtils.assertException { page.notExistedObject.getResourceName(100) }
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
        AssertUtils.assertException { page.notExistedObject.getContentDescription(100) }
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
        AssertUtils.assertException { page.notExistedObject.click(100) }
    }
    //longClick
    @Test
    fun longClick_onLongClickable() {
        page.button.longClick()
        page.eventStatus.textContains(getTargetString(R.string.button_event_long_click))
    }

    @Test
    fun longClick_onNotExistedObject() {
        AssertUtils.assertException { page.notExistedObject.longClick(100) }
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
        AssertUtils.assertException { page.notExistedObject.clear() }
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
        AssertUtils.assertException { page.notExistedObject.addText("asdasd") }
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
        AssertUtils.assertException { page.button.legacySetText("some new text", 100) }
    }
    @Test
    fun legacySetText_toNotExistedObject(){
        AssertUtils.assertException { page.notExistedObject.legacySetText("some new text", 100) }
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
        page.button.replaceText("some new text", 1000).hasText(btnText)
    }

    @Test
    fun replaceText_toNotExistedObject(){
        AssertUtils.assertException { page.notExistedObject.replaceText("some new text", 100) }
    }

    //perform
    @Test
    fun perform_existedObject(){
        page.button.perform({ this.click() }, "Click on button")
        page.eventStatus.textContains(TestDataUtils.getResourceString(R.string.button_event_click))
    }

    @Test
    fun perform_notExistedObject(){
        AssertUtils.assertException { page.notExistedObject.perform({this.click()}, "Click on button", timeoutMs = 100) }
    }
}