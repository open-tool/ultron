package com.atiurin.sampleapp.tests.compose

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import com.atiurin.sampleapp.activity.ActionsStatus
import com.atiurin.sampleapp.activity.ComposeElementsActivity
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.likesCounterButton
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.likesCounterContentDesc
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.likesCounterTextContainerContentDesc
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.simpleCheckbox
import com.atiurin.sampleapp.compose.RegionName
import com.atiurin.sampleapp.framework.ultronext.ProgressBar
import com.atiurin.sampleapp.framework.ultronext.getProgress
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.ComposeElementsPage
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.core.common.assertion.softAssertion
import com.atiurin.ultron.core.common.assertion.verifySoftAssertions
import com.atiurin.ultron.core.common.options.ClickOption
import com.atiurin.ultron.core.common.options.ContentDescriptionContainsOption
import com.atiurin.ultron.core.common.options.PerformCustomBlockOption
import com.atiurin.ultron.core.common.options.TextContainsOption
import com.atiurin.ultron.core.common.options.TextEqualsOption
import com.atiurin.ultron.core.compose.createUltronComposeRule
import com.atiurin.ultron.core.compose.nodeinteraction.UltronComposeSemanticsNodeInteraction
import com.atiurin.ultron.core.compose.nodeinteraction.click
import com.atiurin.ultron.core.compose.operation.ComposeOperationType
import com.atiurin.ultron.core.compose.operation.UltronComposeOperationParams
import com.atiurin.ultron.core.compose.option.ComposeSwipeOption
import com.atiurin.ultron.core.config.UltronCommonConfig
import com.atiurin.ultron.extensions.assertContentDescriptionContains
import com.atiurin.ultron.extensions.assertContentDescriptionEquals
import com.atiurin.ultron.extensions.assertDoesNotExist
import com.atiurin.ultron.extensions.assertExists
import com.atiurin.ultron.extensions.assertHasClickAction
import com.atiurin.ultron.extensions.assertHasNoClickAction
import com.atiurin.ultron.extensions.assertHeightIsAtLeast
import com.atiurin.ultron.extensions.assertHeightIsEqualTo
import com.atiurin.ultron.extensions.assertIsDisplayed
import com.atiurin.ultron.extensions.assertIsEnabled
import com.atiurin.ultron.extensions.assertIsNotEnabled
import com.atiurin.ultron.extensions.assertIsNotFocused
import com.atiurin.ultron.extensions.assertIsNotSelected
import com.atiurin.ultron.extensions.assertIsOff
import com.atiurin.ultron.extensions.assertIsSelectable
import com.atiurin.ultron.extensions.assertIsToggleable
import com.atiurin.ultron.extensions.assertTextContains
import com.atiurin.ultron.extensions.assertTextEquals
import com.atiurin.ultron.extensions.assertValueEquals
import com.atiurin.ultron.extensions.assertWidthIsAtLeast
import com.atiurin.ultron.extensions.assertWidthIsEqualTo
import com.atiurin.ultron.extensions.captureToImage
import com.atiurin.ultron.extensions.clearText
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.extensions.clickBottomCenter
import com.atiurin.ultron.extensions.clickBottomLeft
import com.atiurin.ultron.extensions.clickBottomRight
import com.atiurin.ultron.extensions.clickCenterLeft
import com.atiurin.ultron.extensions.clickCenterRight
import com.atiurin.ultron.extensions.clickTopCenter
import com.atiurin.ultron.extensions.clickTopLeft
import com.atiurin.ultron.extensions.clickTopRight
import com.atiurin.ultron.extensions.copyText
import com.atiurin.ultron.extensions.doubleClick
import com.atiurin.ultron.extensions.execute
import com.atiurin.ultron.extensions.getNode
import com.atiurin.ultron.extensions.getNodeConfigProperty
import com.atiurin.ultron.extensions.getText
import com.atiurin.ultron.extensions.inputText
import com.atiurin.ultron.extensions.longClick
import com.atiurin.ultron.extensions.pasteText
import com.atiurin.ultron.extensions.perform
import com.atiurin.ultron.extensions.performMouseInput
import com.atiurin.ultron.extensions.replaceText
import com.atiurin.ultron.extensions.selectText
import com.atiurin.ultron.extensions.swipe
import com.atiurin.ultron.extensions.swipeDown
import com.atiurin.ultron.extensions.swipeLeft
import com.atiurin.ultron.extensions.swipeRight
import com.atiurin.ultron.extensions.swipeUp
import com.atiurin.ultron.extensions.typeText
import com.atiurin.ultron.extensions.withTimeout
import org.junit.Assert
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class ComposeUIElementsTest : BaseTest() {
    val page = ComposeElementsPage

    @get:Rule
    val composeRule = createUltronComposeRule<ComposeElementsActivity>()
    val initialText = "Like count = 0"
    val expectedText = "Like count = 1"

    @Test
    fun simpleClick() {
        hasText(initialText).assertIsDisplayed().click()
        hasText(expectedText).assertIsDisplayed()
    }

    @Test
    fun contentDescTest() {
        hasContentDescription(likesCounterContentDesc).click()
        hasText(expectedText).assertIsDisplayed()
    }

    @Test
    fun testTagTest() {
        hasText(initialText).click()
        page.likesCounter.assertTextEquals(expectedText).assertIsDisplayed()
    }

    @Test
    fun getTextTest() {
        hasTestTag(likesCounterButton).click()
        val text = hasTestTag(likesCounterButton).getText()
        Assert.assertEquals(expectedText, text)
    }

    @Test
    fun clickCheckBox() {
        hasTestTag(simpleCheckbox).assertIsOff().click().assertIsOn()
    }

    @Test
    fun longClick_longClickable() {
        page.longAndDoubleClickButton.longClick()
        page.status.assertTextEquals(ActionsStatus.LongClicked.name)
    }

    @Test
    fun doubleClick_doubleClickable() {
        page.longAndDoubleClickButton.doubleClick()
        page.status.assertTextEquals(ActionsStatus.DoubleClicked.name)
    }

    @Test
    fun regionsClickTopLeft() {
        page.regionsNode.clickTopLeft(ClickOption(xOffset = 20, yOffset = 20))
        page.status.assertTextEquals(RegionName.TopLeft.name)
    }

    @Test
    fun regionsClickTopCenter() {
        page.regionsNode.clickTopCenter(ClickOption(yOffset = 20))
        page.status.assertTextEquals(RegionName.TopCenter.name)
    }

    @Test
    fun regionsClickTopRight() {
        page.regionsNode.clickTopRight(ClickOption(yOffset = 20))
        page.status.assertTextEquals(RegionName.TopRight.name)
    }

    @Test
    fun regionsClickCenterLeft() {
        page.regionsNode.clickCenterLeft()
        page.status.assertTextEquals(RegionName.CenterLeft.name)
    }

    @Test
    fun regionsClickCenterRight() {
        page.regionsNode.clickCenterRight()
        page.status.assertTextEquals(RegionName.CenterRight.name)
    }

    @Test
    fun regionsClickBottomLeft() {
        page.regionsNode.clickBottomLeft(ClickOption(xOffset = 16))
        page.status.assertTextEquals(RegionName.BottomLeft.name)
    }

    @Test
    fun regionsClickBottomCenter() {
        page.regionsNode.clickBottomCenter()
        page.status.assertTextEquals(RegionName.BottomCenter.name)
    }

    @Test
    fun regionsClickBottomRight() {
        page.regionsNode.clickBottomRight()
        page.status.assertTextEquals(RegionName.BottomRight.name)
    }

    //flaky on emulator
    @Test
    @Ignore
    fun copyText() {
        val startText = "begin"
        page.editableText.apply {
            replaceText(startText)
            selectText(TextRange(0, 2))
            assertIsDisplayed()
            copyText()
            assertIsDisplayed()
            clearText()
            click()
            pasteText()
            assertTextContains(startText)
        }
    }

    @Test
    fun swipeDown() {
        page.swipeableNode.swipeDown()
        page.status.assertTextEquals(ActionsStatus.SwipeDown.name)
    }

    @Test
    fun swipeUp() {
        page.swipeableNode.swipeUp()
        page.status.assertTextEquals(ActionsStatus.SwipeUp.name)
    }

    @Test
    fun swipeRight() {
        page.swipeableNode.swipeRight()
        page.status.assertTextEquals(ActionsStatus.SwipeRight.name)
    }

    @Test
    fun swipeLeft() {
        page.swipeableNode.swipeLeft()
        page.status.assertTextEquals(ActionsStatus.SwipeLeft.name)
    }

    @Test
    fun swipe_option() {
        page.swipeableNode.swipeLeft(ComposeSwipeOption(durationMs = 1000L))
        page.status.assertTextEquals(ActionsStatus.SwipeLeft.name)
    }

    @Test
    fun swipe_general() {
        page.swipeableNode.swipe(ComposeSwipeOption(
            startXOffset = 0.1f,
            startYOffset = 0.1f,
            endXOffset = 0.9f,
            endYOffset = 0.1f,
            durationMs = 1000L
        ))
        page.status.assertTextEquals(ActionsStatus.SwipeRight.name)
    }

    @Test
    fun inputText() {
        val text = "some text"
        page.editableText.inputText(text).assertTextContains(text)
    }

    @Test
    fun typeText() {
        val text = "some text"
        page.editableText.typeText(text).assertTextContains(text)
    }

    @Test
    fun setSelection() {
        page.editableText.replaceText("qwerty").setSelection(0, 3, true).assertIsDisplayed().cutText().assertTextContains("rty")
    }

    @Test
    fun captureToImage() {
        val image = page.longAndDoubleClickButton.captureToImage()
        Assert.assertNotNull(image)
    }

    @Test
    fun setProgress() {
        page.progressBar.setProgress(0.7f).assertIsDisplayed()
        page.status.assertTextEquals("set progress 0.7")
    }

    @Test
    fun performCustomSemanticsAction() {
        val progress = 0.7f
        val progressBar = ProgressBar(hasTestTag(ComposeElementsActivity.progressBar))
        progressBar.setProgress(progress)
        val current = progressBar.getProgress()
        Assert.assertEquals(current, progress )

    }


    @Test
    fun performCustomSemanticsAssertion() {
        val progress = 0.7f
        val progressBar = ProgressBar(hasTestTag(ComposeElementsActivity.progressBar))
        progressBar.setProgress(progress)
        progressBar.assertProgress(progress)
    }

    @Test
    fun performExtendedAssertion() {
        val progress = 0.7f
        page.progressBar.apply {
            setProgress(progress)
            assertProgress(progress)
        }
    }

    @Test
    fun performWithLambda() {
        val progress = 0.7f
        val result = page.progressBar.perform {
            it.performSemanticsAction(SemanticsActions.SetProgress) {
                it.invoke(progress)
            }
        }
        Assert.assertTrue(result is UltronComposeSemanticsNodeInteraction)
        page.progressBar.assertProgress(progress)
    }

    @Test
    fun semanticsMatcher_performDeprecated() {
        val text = page.status.perform<String>({
            it.fetchSemanticsNode().config[SemanticsProperties.Text].first().text
        }, option = PerformCustomBlockOption(ComposeOperationType.CUSTOM, ""))
        Assert.assertTrue(text.isNotBlank())
    }

    @Test
    fun ultronComposeSemanticsNodeInteraction_performDeprecated() {
        val text = page.status.assertExists().perform(option = PerformCustomBlockOption(ComposeOperationType.CUSTOM, "")) {
            it.fetchSemanticsNode().config[SemanticsProperties.Text].first().text
        }
        Assert.assertTrue(text.isNotBlank())
    }

    @Test
    fun ultronComposeSemanticsNodeInteraction_execute() {
        val text = page.status.assertExists().execute {
            it.fetchSemanticsNode().config[SemanticsProperties.Text].first().text
        }
        Assert.assertTrue(text.isNotBlank())
    }

    @Test
    fun semanticsMatcher_execute() {
        val text = page.status.execute {
            it.fetchSemanticsNode().config[SemanticsProperties.Text].first().text
        }
        Assert.assertTrue(text.isNotBlank())
    }

    @Test
    fun performMouseInput() {
        page.swipeableNode.performMouseInput { swipeUp() }
        page.status.assertTextEquals(ActionsStatus.SwipeUp.name)
    }

    @Test
    fun getNode_exits() {
        val node = page.status.getNode()
        Assert.assertEquals(ComposeElementsActivity.Constants.statusText, node.config[SemanticsProperties.TestTag])
    }

    @Test
    fun getNodeConfigProperty_exist() {
        val testTag = page.status.getNodeConfigProperty(SemanticsProperties.TestTag)
        Assert.assertEquals(ComposeElementsActivity.Constants.statusText, testTag)
    }

    @Test
    fun assertIsDisplayed() {
        page.status.assertIsDisplayed()
    }

    @Test
    fun assertExists() {
        page.status.assertExists()
    }

    @Test
    fun assertExists_notExisted() {
        AssertUtils.assertException { hasText("some not existed node").withTimeout(100).assertExists() }
    }

    @Test
    fun assertDoesNotExist() {
        hasText("some not existed node").assertDoesNotExist()
    }

    @Test
    fun assertDoesNotExist_existed() {
        AssertUtils.assertException { page.editableText.withTimeout(100).assertDoesNotExist() }
    }

    @Test
    fun assertIsEnabled() {
        page.editableText.assertIsEnabled()
    }

    @Test
    fun assertIsEnabled_disabledButton() {
        AssertUtils.assertException { page.disabledButton.withTimeout(100).assertIsEnabled() }
    }

    @Test
    fun assertIsNotEnabled() {
        page.disabledButton.assertIsNotEnabled()
    }

    @Test
    fun assertIsNotEnabled_enabledButton() {
        AssertUtils.assertException { page.longAndDoubleClickButton.withTimeout(100).assertIsNotEnabled() }
    }

    @Test
    fun assertIsFocused() {
        page.editableText.click().assertIsFocused()
    }

    @Test
    fun assertIsFocused_notFocused() {
        AssertUtils.assertException { page.editableText.withTimeout(100).assertIsFocused() }
    }

    @Test
    fun assertIsNotFocused() {
        page.editableText.assertIsNotFocused()
    }

    @Test
    fun assertIsNotFocused_focused() {
        AssertUtils.assertException { page.editableText.click().withTimeout(100).assertIsNotFocused() }
    }


    @Test
    fun assertIsSelected() {
        page.maleRadioButton.click().assertIsSelected()
    }

    @Test
    fun assertIsSelected_notSelected() {
        page.maleRadioButton.click()
        AssertUtils.assertException { page.femaleRadioButton.withTimeout(100).assertIsSelected() }
    }

    @Test
    fun assertIsNotSelected_notSelected() {
        page.maleRadioButton.click()
        page.femaleRadioButton.assertIsNotSelected()
    }

    @Test
    fun assertIsNotSelected_selected() {
        AssertUtils.assertException { page.maleRadioButton.click().withTimeout(100).assertIsNotSelected() }
    }

    @Test
    fun assertIsSelectable() {
        page.femaleRadioButton.assertIsSelectable()
    }

    @Test
    fun assertIsSelectable_notSelectable() {
        AssertUtils.assertException { page.status.withTimeout(100).assertIsSelectable() }
    }

    @Test
    fun assertIsToggleable() {
        page.simpleCheckbox.assertIsToggleable()
    }

    @Test
    fun assertIsToggleable_notToggleable() {
        AssertUtils.assertException { page.editableText.withTimeout(100).assertIsToggleable() }
    }

    @Test
    fun assertIsOn() {
        page.simpleCheckbox.click().assertIsOn()
    }

    @Test
    fun assertIsOn_checkboxIsOff() {
        AssertUtils.assertException { page.simpleCheckbox.withTimeout(100).assertIsOn() }
    }

    @Test
    fun assertIsOff() {
        page.simpleCheckbox.assertIsOff()
    }

    @Test
    fun assertIsOff_checkboxIsOn() {
        AssertUtils.assertException { page.simpleCheckbox.click().withTimeout(100).assertIsOff() }
    }

    @Test
    fun assertHasClickAction() {
        page.longAndDoubleClickButton.assertHasClickAction()
    }

    @Test
    fun assertHasClickAction_noClickAction() {
        AssertUtils.assertException { page.status.withTimeout(100).assertHasClickAction() }
    }

    @Test
    fun assertHasNoClickAction() {
        page.status.assertHasNoClickAction()
    }

    @Test
    fun assertHasNoClickAction_hasClickAction() {
        AssertUtils.assertException { page.longAndDoubleClickButton.withTimeout(100).assertHasNoClickAction() }
    }

    @Test
    fun assertTextEquals() {
        page.editableText.assertTextEquals("Label", "")
    }

    @Test
    fun assertTextEquals_includeEditableFalse() {
        page.editableText.assertTextEquals("Label", option = TextEqualsOption(false))
    }

    @Test
    fun assertTextEquals_includeEditableFalse_editableProvided() {
        AssertUtils.assertException {
            page.editableText.withTimeout(100).assertTextEquals("Label", "", option = TextEqualsOption(false))
        }
    }

    @Test
    fun assertTextEquals_wrongTextProvided() {
        AssertUtils.assertException {
            page.editableText.withTimeout(100).assertTextEquals("some invalid text", "")
        }
    }

    @Test
    fun assertTextEquals_editableNotEmpty_ValidText() {
        val text = "editable text"
        page.editableText.replaceText(text).assertTextEquals("Label", text)
    }

    @Test
    fun assertTextEquals_editableNotEmpty_ValidText_mixedOrder() {
        val text = "editable text"
        page.editableText.replaceText(text).assertTextEquals(text, "Label")
    }

    @Test
    fun assertTextEquals_editableNotEmpty_includeEditableFalse() {
        val text = "editable text"
        AssertUtils.assertException {
            page.editableText.withTimeout(100).replaceText(text).assertTextEquals("Label", text, option = TextEqualsOption(false))
        }
    }

    @Test
    fun assertTextContains_label() {
        page.editableText.assertTextContains("Label")
    }

    @Test
    fun assertTextContains_editable() {
        val text = "some text"
        page.editableText.replaceText(text).assertTextContains(text)
    }

    @Test
    fun assertTextContains_wrongText() {
        val text = "some text"
        AssertUtils.assertException { page.editableText.withTimeout(100).assertTextContains(text) }
    }

    @Test
    fun assertTextContains_emptyText() {
        page.editableText.assertTextContains("")
    }

    @Test
    fun assertTextContains_substringTrue_validSubstringProvided() {
        val text = "some text"
        page.editableText.replaceText(text).assertTextContains(text.substring(0, 4), TextContainsOption(substring = true))
    }

    @Test
    fun assertTextContains_substringTrue_wrongSubstringProvided() {
        AssertUtils.assertException {
            page.editableText.replaceText("valid text").withTimeout(100).assertTextContains("wrong text", TextContainsOption(substring = true))
        }
    }

    @Test
    fun assertTextContains_substringFalse_validSubstringProvided() {
        val text = "some text"
        AssertUtils.assertException {
            page.editableText.replaceText(text).withTimeout(100).assertTextContains(text.substring(0, 4), TextContainsOption(substring = false))
        }
    }

    @Test
    fun assertTextContains_ignoreCase_lowercase() {
        val text = "SoMe TexT"
        page.editableText.replaceText(text).assertTextContains(text.lowercase(), TextContainsOption(ignoreCase = true))
    }

    @Test
    fun assertTextContains_ignoreCase_uppercase() {
        val text = "SoMe TexT"
        page.editableText.replaceText(text).assertTextContains(text.uppercase(), TextContainsOption(ignoreCase = true))
    }

    @Test
    fun assertTextContains_ignoreCase_and_substring() {
        val text = "SoMe TexT"
        page.editableText.replaceText(text).assertTextContains(text.substring(0, 4).lowercase(), TextContainsOption(substring = true, ignoreCase = true))
    }

    @Test
    fun assertTextContains_ignoreCaseFalse() {
        val text = "SoMe TexT"
        AssertUtils.assertException {
            page.editableText.replaceText(text).withTimeout(100).assertTextContains(text.lowercase(), TextContainsOption(ignoreCase = false))
        }
    }

    @Test
    fun assertContentDescriptionEquals() {
        page.likesCounter.assertContentDescriptionEquals(likesCounterContentDesc, likesCounterTextContainerContentDesc)
    }

    @Test
    fun assertContentDescriptionEquals_notEnoughElements() {
        AssertUtils.assertException {
            page.likesCounter.withTimeout(100).assertContentDescriptionEquals(likesCounterContentDesc)
        }
    }

    @Test
    fun assertContentDescriptionContains() {
        page.likesCounter.assertContentDescriptionContains(likesCounterContentDesc)
    }

    @Test
    fun assertContentDescriptionContains_substringTrue_validSubstringProvided() {
        page.likesCounter.assertContentDescriptionContains(likesCounterContentDesc.substring(1, 5), ContentDescriptionContainsOption(substring = true))
    }

    @Test
    fun assertContentDescriptionContains_substringTrue_wrongSubstringProvided() {
        AssertUtils.assertException {
            page.likesCounter.withTimeout(100).assertContentDescriptionContains("wrong substring", ContentDescriptionContainsOption(substring = true))
        }
    }

    @Test
    fun assertContentDescriptionContains_ignoreCaseTrue_lowercase() {
        page.likesCounter.assertContentDescriptionContains(likesCounterContentDesc.lowercase(), ContentDescriptionContainsOption(ignoreCase = true))
    }

    @Test
    fun assertContentDescriptionContains_ignoreCaseTrue_uppercase() {
        page.likesCounter.assertContentDescriptionContains(likesCounterContentDesc.uppercase(), ContentDescriptionContainsOption(ignoreCase = true))
    }

    @Test
    fun assertContentDescriptionContains_ignoreCaseFalse() {
        AssertUtils.assertException {
            page.likesCounter.withTimeout(100).assertContentDescriptionContains(likesCounterContentDesc.lowercase(), ContentDescriptionContainsOption(ignoreCase = false))
        }
    }

    @Test
    fun assertValueEquals() {
        page.simpleCheckbox.assertValueEquals("default")
    }

    @Test
    fun assertValueEquals_invalidValue() {
        AssertUtils.assertException { page.simpleCheckbox.withTimeout(100).assertValueEquals("invalid") }
    }


    @Test
    fun assertRangeInfoEquals() {
        page.progressBar.setProgress(0.7f).assertRangeInfoEquals(ProgressBarRangeInfo(0.7f, range = 0f..0.7f, 100))
    }

    @Test
    fun assertRangeInfoEquals_invalidInfo() {
        AssertUtils.assertException {
            page.progressBar.setProgress(0.7f).withTimeout(100).assertRangeInfoEquals(ProgressBarRangeInfo(0.0f, range = 0f..0.0f, 100))
        }
    }

    @Test
    fun assertHeightIsEqualTo() {
        page.swipeableNode.assertHeightIsEqualTo(100.dp)
    }

    @Test
    fun assertHeightIsEqualTo_invalidValue() {
        AssertUtils.assertException { page.swipeableNode.withTimeout(100).assertHeightIsEqualTo(50.dp) }
    }

    @Test
    fun assertWidthIsEqualTo() {
        page.swipeableNode.assertWidthIsEqualTo(100.dp)
    }

    @Test
    fun assertWidthIsEqualTo_invalidValue() {
        AssertUtils.assertException { page.swipeableNode.withTimeout(100).assertWidthIsEqualTo(50.dp) }
    }

    @Test
    fun assertHeightIsAtLeast() {
        page.swipeableNode.assertHeightIsAtLeast(10.dp)
    }

    @Test
    fun assertHeightIsAtLeast_invalidValue() {
        AssertUtils.assertException { page.swipeableNode.withTimeout(100).assertHeightIsAtLeast(500.dp) }
    }

    @Test
    fun assertWidthIsAtLeast() {
        page.swipeableNode.assertWidthIsAtLeast(10.dp)
    }

    @Test
    fun assertWidthIsAtLeast_invalidValue() {
        AssertUtils.assertException { page.swipeableNode.withTimeout(100).assertWidthIsAtLeast(500.dp) }
    }

    @Test
    fun assertMatches() {
        val text = "some text"
        page.editableText.replaceText(text).assertMatches(hasText(text))
    }

    @Test
    fun assertMatches_invalid() {
        AssertUtils.assertException {
            page.editableText.replaceText("some text").withTimeout(100)
                .assertMatches(hasText("invalid text"))
        }
    }

    @Test
    fun customPerformParamsMapping() {
        val params = UltronComposeOperationParams(
            operationName = "operationName",
            operationDescription = "operationDescription",
            operationType = ComposeOperationType.ASSERT_MATCHES
        )
        page.status.withTimeout(100).withResultHandler {
            val op = it.operation
            Assert.assertEquals(params.operationName, op.name)
            Assert.assertEquals(params.operationDescription, op.description)
            Assert.assertEquals(params.operationType, op.type)
        }.perform(params) {
            it.assertTextContains("Some invalid text")
        }
    }

    @Test
    fun softAssertionTest() {
        UltronCommonConfig.testContext.softAnalyzer.clear()
        softAssertion(false) {
            hasText("NotExistText").withTimeout(100).assertIsDisplayed()
            hasTestTag("NotExistTestTag").withTimeout(100).assertHasClickAction()
        }
        runCatching {
            verifySoftAssertions()
        }.onFailure { exception ->
            val message = exception.message ?: throw RuntimeException("Empty exception message: $exception")
            Assert.assertTrue(message.contains("NotExistText"))
            Assert.assertTrue(message.contains("NotExistTestTag"))
        }

    }
}