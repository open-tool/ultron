package com.atiurin.sampleapp.test.espresso.recycler

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.atiurin.sampleapp.activity.MainActivity
import com.atiurin.sampleapp.data.repositories.ContactRepository
import com.atiurin.sampleapp.screens.kaspresso.KaspressoEspressoChatScreen
import com.atiurin.sampleapp.screens.kaspresso.KaspressoRecyclerViewScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.Test

class KaspressoRecyclerViewTest : TestCase() {
    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun openFirstChatTest() = run {
        step("Open 1st chat") {
            KaspressoRecyclerViewScreen {
                recyclerView {
                    emptyFirstChild {
                        click()
                    }
                }
            }
        }
        step("Assert chat screen is open") {
            KaspressoEspressoChatScreen {
                toolbarTitle.isDisplayed()
            }
        }
    }

    @Test
    fun sendMessageTest() = run {
        val contact = ContactRepository.getLast()
        KaspressoRecyclerViewScreen {
            recyclerView {
                childWith<KaspressoRecyclerViewScreen.KContactListItem> {
                    withDescendant {
                        withText(contact.name)
                    }
                }.perform {
                    name {
                        hasText(contact.name)
                        isDisplayed()
                    }
                    status {
                        hasText(contact.status)
                        isDisplayed()
                    }
                    avatar.isDisplayed()
                }
            }
        }
        step("Open chat with $contact"){
            KaspressoRecyclerViewScreen {
                recyclerView {
                    childWith<KaspressoRecyclerViewScreen.KContactListItem> {
                        withDescendant {
                            withText(contact.name)
                        }
                    }.click()
                }
            }
        }
        val messageText = "SOME MESSAGE"
        step("Send message '$messageText'"){
            KaspressoEspressoChatScreen {
                messageTextInput.replaceText(messageText)
                sendMessageButton.click()
                messagesList {
                    emptyChildWith {
                        withDescendant {
                            withText(messageText)
                        }
                    }perform {
                        isDisplayed()
                    }
                }
            }
        }
    }
}
