package com.atiurin.sampleapp.screens.kaspresso.scenario

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.atiurin.sampleapp.data.entities.Contact
import com.atiurin.sampleapp.screens.kaspresso.ContactLazyListItemNode
import com.atiurin.sampleapp.screens.kaspresso.KaspressoComposeChatScreen
import com.atiurin.sampleapp.screens.kaspresso.KaspressoComposeListScreen
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen

class KaspressoComposeOpenContactChatScenario(
    private val contact: Contact,
    private val composeRule: SemanticsNodeInteractionsProvider
) : Scenario() {
    @OptIn(ExperimentalTestApi::class)
    override val steps: TestContext<Unit>.() -> Unit
        get() = {
            onComposeScreen<KaspressoComposeListScreen>(composeRule) {
                step("Assert Item UI") {
                    contactsList {
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
                    status.assertTextEquals(contact.status)
                }
            }
        }
}