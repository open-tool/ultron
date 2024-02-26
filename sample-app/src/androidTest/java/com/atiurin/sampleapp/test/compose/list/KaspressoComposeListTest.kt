package com.atiurin.sampleapp.test.compose.list

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.printToLog
import com.atiurin.sampleapp.activity.ComposeListActivity
import com.atiurin.sampleapp.compose.contactsListTestTag
import com.atiurin.sampleapp.data.repositories.ContactRepository
import com.atiurin.sampleapp.screens.kaspresso.ContactLazyListItemNode
import com.atiurin.sampleapp.screens.kaspresso.KaspressoComposeChatScreen
import com.atiurin.sampleapp.screens.kaspresso.KaspressoComposeListScreen
import com.atiurin.sampleapp.screens.kaspresso.scenario.KaspressoComposeOpenContactChatScenario
import com.kaspersky.components.alluresupport.withForcedAllureSupport
import com.kaspersky.components.composesupport.config.addComposeSupport
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Rule
import org.junit.Test

class KaspressoComposeListTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComposeListActivity>()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun openFirstChatTest() = run {
        onComposeScreen<KaspressoComposeListScreen>(composeRule) {
            contactsList {
                firstChild<ContactLazyListItemNode> {
                    performClick()
                }
            }
        }
        onComposeScreen<KaspressoComposeChatScreen>(composeRule) {
            name.assertIsDisplayed()
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun openChatTestWithContact() = run {
        val contact = ContactRepository.getLast()

        onComposeScreen<KaspressoComposeListScreen>(composeRule) {
            step("Assert Item UI") {
                contactsList {
                    composeRule.onNode(hasTestTag(contactsListTestTag), useUnmergedTree = false)
                        .printToLog("ULTRON")
                    childWith<ContactLazyListItemNode> {
                        hasText(contact.name)
                    } perform {
                        name {
                            assertTextEquals(contact.name)
                            assertIsDisplayed()
                        }
                        status {
                            assertTextEquals(contact.status)
                            assertIsDisplayed()
                        }
                        avatar {
                            assertIsDisplayed()
                        }
                    }
                }
            }
            step("Open chat with contact $contact") {
                contactsList {
                    childWith<ContactLazyListItemNode> {
                        hasText(contact.name)
                    } perform {
                        performClick()
                    }
                    onComposeScreen<KaspressoComposeChatScreen>(composeRule) {
                        name.assertIsDisplayed()
                    }
                }
            }
        }
        step("Assert chat screen ui") {
            onComposeScreen<KaspressoComposeChatScreen>(composeRule) {
                name.assertTextEquals(contact.name)
                status.assertTextEquals(contact.status + " INVALID")
            }
        }

    }

    @Test
    fun scenarioTest() = run {
        val contact = ContactRepository.getLast()
        scenario(KaspressoComposeOpenContactChatScenario(contact, composeRule))
    }
}