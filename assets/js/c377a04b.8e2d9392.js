"use strict";(self.webpackChunkmy_website=self.webpackChunkmy_website||[]).push([[361],{8321:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>a,contentTitle:()=>r,default:()=>h,frontMatter:()=>i,metadata:()=>l,toc:()=>c});var s=t(4848),o=t(8453);const i={sidebar_position:1},r="Introduction",l={id:"index",title:"Introduction",description:"Docusaurus themed imageDocusaurus themed image",source:"@site/docs/index.md",sourceDirName:".",slug:"/",permalink:"/docs/",draft:!1,unlisted:!1,tags:[],version:"current",sidebarPosition:1,frontMatter:{sidebar_position:1},sidebar:"tutorialSidebar",next:{title:"Connect to project",permalink:"/docs/intro/connect"}},a={},c=[{value:"What are the benefits of using the framework?",id:"what-are-the-benefits-of-using-the-framework",level:2},{value:"A few words about syntax",id:"a-few-words-about-syntax",level:3},{value:"1. Simple compose operation (refer to the wiki here)",id:"1-simple-compose-operation-refer-to-the-wiki-here",level:4},{value:"2. Compose list operation (refer to the wiki here)",id:"2-compose-list-operation-refer-to-the-wiki-here",level:4},{value:"3. Simple Espresso assertion and action.",id:"3-simple-espresso-assertion-and-action",level:4},{value:"4. Action on RecyclerView list item",id:"4-action-on-recyclerview-list-item",level:4},{value:"5. Espresso WebView operations",id:"5-espresso-webview-operations",level:4},{value:"6. UI Automator operations",id:"6-ui-automator-operations",level:4},{value:"Acquiring the result of any operation as Boolean value",id:"acquiring-the-result-of-any-operation-as-boolean-value",level:3},{value:"Why are all Ultron actions and assertions more stable?",id:"why-are-all-ultron-actions-and-assertions-more-stable",level:3},{value:"3 steps to develop a test using Ultron",id:"3-steps-to-develop-a-test-using-ultron",level:2},{value:"Allure report",id:"allure-report",level:2}];function d(e){const n={a:"a",code:"code",em:"em",h1:"h1",h2:"h2",h3:"h3",h4:"h4",hr:"hr",img:"img",li:"li",ol:"ol",p:"p",pre:"pre",strong:"strong",ul:"ul",...(0,o.R)(),...e.components};return(0,s.jsxs)(s.Fragment,{children:[(0,s.jsx)(n.h1,{id:"introduction",children:"Introduction"}),"\n",(0,s.jsxs)(n.p,{children:[(0,s.jsx)(n.img,{alt:"Docusaurus themed image",src:t(443).A+"#gh-light-mode-only",width:"12610",height:"2326"}),(0,s.jsx)(n.img,{alt:"Docusaurus themed image",src:t(5859).A+"#gh-dark-mode-only",width:"12610",height:"2326"})]}),"\n",(0,s.jsxs)(n.p,{children:["Ultron is the simplest framework to develop UI tests for ",(0,s.jsx)(n.strong,{children:"Android"})," & ",(0,s.jsx)(n.strong,{children:"Compose Multiplatform"}),"."]}),"\n",(0,s.jsx)(n.p,{children:"It's constructed upon the Espresso, UI Automator and Compose UI testing frameworks. Ultron introduces a range of remarkable new features. Furthermore, Ultron puts you in complete control of your tests!"}),"\n",(0,s.jsx)(n.p,{children:"You don't need to learn any new classes or special syntax. All magic actions and assertions are provided from crunch. Ultron can be easially customised and extended."}),"\n",(0,s.jsx)(n.h2,{id:"what-are-the-benefits-of-using-the-framework",children:"What are the benefits of using the framework?"}),"\n",(0,s.jsxs)(n.ul,{children:["\n",(0,s.jsxs)(n.li,{children:["Exceptional support for ",(0,s.jsx)(n.a,{href:"https://github.com/open-tool/ultron/wiki/Compose",children:(0,s.jsx)(n.strong,{children:"Compose"})})]}),"\n",(0,s.jsxs)(n.li,{children:["Out-of-the-box generation of ",(0,s.jsx)(n.a,{href:"https://github.com/open-tool/ultron/wiki/Allure",children:(0,s.jsx)(n.strong,{children:"Allure report"})})," (Now, for Android UI tests only)"]}),"\n",(0,s.jsx)(n.li,{children:"A straightforward and expressive syntax"}),"\n",(0,s.jsxs)(n.li,{children:["Ensured ",(0,s.jsx)(n.strong,{children:"Stability"})," for all actions and assertions"]}),"\n",(0,s.jsx)(n.li,{children:"Complete control over every action and assertion"}),"\n",(0,s.jsxs)(n.li,{children:["Incredible interaction with ",(0,s.jsx)(n.a,{href:"https://github.com/open-tool/ultron/wiki/RecyclerView",children:(0,s.jsx)(n.strong,{children:"RecyclerView"})})," and ",(0,s.jsx)(n.a,{href:"https://github.com/open-tool/ultron/wiki/Compose#ultron-compose-lazycolumnlazyrow",children:(0,s.jsx)(n.strong,{children:"Compose\xa0lists"})}),"."]}),"\n",(0,s.jsxs)(n.li,{children:["An ",(0,s.jsx)(n.strong,{children:"Architectural"})," approach to developing UI tests"]}),"\n",(0,s.jsx)(n.li,{children:"An incredible mechanism for setups and teardowns (You can even set up preconditions for a single test within a test class, without affecting the others)"}),"\n",(0,s.jsx)(n.li,{children:(0,s.jsx)(n.a,{href:"https://github.com/open-tool/ultron/wiki/Ultron-Extension",children:"The ability to effortlessly extend the framework with your own operations"})}),"\n",(0,s.jsx)(n.li,{children:"Accelerated UI Automator operations"}),"\n",(0,s.jsxs)(n.li,{children:["Ability to monitor each stage of operation execution with ",(0,s.jsx)(n.a,{href:"https://github.com/open-tool/ultron/wiki/Listeners",children:"Listeners"})]}),"\n",(0,s.jsx)(n.li,{children:(0,s.jsx)(n.a,{href:"https://github.com/open-tool/ultron/wiki/Custom-operation-assertions",children:"Custom operation assertions"})}),"\n"]}),"\n",(0,s.jsx)(n.hr,{}),"\n",(0,s.jsx)(n.h3,{id:"a-few-words-about-syntax",children:"A few words about syntax"}),"\n",(0,s.jsxs)(n.p,{children:["The standard syntax provided by Google is intricate and not intuitive. This is especially evident when dealing with ",(0,s.jsx)(n.strong,{children:"LazyList"})," and ",(0,s.jsx)(n.strong,{children:"RecyclerView"})," interactions."]}),"\n",(0,s.jsx)(n.p,{children:"Let's explore some examples:"}),"\n",(0,s.jsxs)(n.h4,{id:"1-simple-compose-operation-refer-to-the-wiki-here",children:["1. Simple compose operation (refer to the wiki ",(0,s.jsx)(n.a,{href:"https://github.com/open-tool/ultron/wiki/Compose#ultron-compose",children:"here"}),")"]}),"\n",(0,s.jsx)(n.p,{children:(0,s.jsx)(n.em,{children:"Compose framework"})}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:'composeTestRule.onNode(hasTestTag("Continue")).performClick()\ncomposeTestRule.onNodeWithText("Welcome").assertIsDisplayed()\n'})}),"\n",(0,s.jsx)(n.p,{children:(0,s.jsx)(n.em,{children:"Ultron"})}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:'hasTestTag("Continue").click()\nhasText("Welcome").assertIsDisplayed()\n'})}),"\n",(0,s.jsxs)(n.h4,{id:"2-compose-list-operation-refer-to-the-wiki-here",children:["2. Compose list operation (refer to the wiki ",(0,s.jsx)(n.a,{href:"https://github.com/open-tool/ultron/wiki/Compose#ultron-compose-lazycolumnlazyrow",children:"here"}),")"]}),"\n",(0,s.jsx)(n.p,{children:(0,s.jsx)(n.em,{children:"Compose framework"})}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:"val itemMatcher = hasText(contact.name)\ncomposeRule\n    .onNodeWithTag(contactsListTestTag)\n    .performScrollToNode(itemMatcher)\n    .onChildren()\n    .filterToOne(itemMatcher)\n    .assertTextContains(contact.name)\n"})}),"\n",(0,s.jsx)(n.p,{children:(0,s.jsx)(n.em,{children:"Ultron"})}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:"composeList(hasTestTag(contactsListTestTag))\n    .item(hasText(contact.name))\n    .assertTextContains(contact.name)\n"})}),"\n",(0,s.jsx)(n.h4,{id:"3-simple-espresso-assertion-and-action",children:"3. Simple Espresso assertion and action."}),"\n",(0,s.jsx)(n.p,{children:(0,s.jsx)(n.em,{children:"Espresso"})}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:"onView(withId(R.id.send_button)).check(isDisplayed()).perform(click())\n"})}),"\n",(0,s.jsx)(n.p,{children:(0,s.jsx)(n.em,{children:"Ultron"})}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:"withId(R.id.send_button).isDisplayed().click()\n"})}),"\n",(0,s.jsx)(n.p,{children:"This presents a cleaner approach. Ultron's operation names mirror Espresso's, while also providing additional operations."}),"\n",(0,s.jsxs)(n.p,{children:["Refer to the ",(0,s.jsx)(n.a,{href:"https://github.com/open-tool/ultron/wiki/Espresso-operations",children:"wiki"})," for further details."]}),"\n",(0,s.jsx)(n.h4,{id:"4-action-on-recyclerview-list-item",children:"4. Action on RecyclerView list item"}),"\n",(0,s.jsx)(n.p,{children:(0,s.jsx)(n.em,{children:"Espresso"})}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:'onView(withId(R.id.recycler_friends))\n    .perform(\n        RecyclerViewActions\n            .actionOnItem<RecyclerView.ViewHolder>(\n                hasDescendant(withText("Janice")),\n                click()\n            )\n        )\n'})}),"\n",(0,s.jsx)(n.p,{children:(0,s.jsx)(n.em,{children:"Ultron"})}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:'withRecyclerView(R.id.recycler_friends)\n    .item(hasDescendant(withText("Janice")))\n    .click()\n'})}),"\n",(0,s.jsxs)(n.p,{children:["Explore the ",(0,s.jsx)(n.a,{href:"https://github.com/open-tool/ultron/wiki/RecyclerView",children:"wiki"})," to unveil Ultron's magic with RecyclerView interactions."]}),"\n",(0,s.jsx)(n.h4,{id:"5-espresso-webview-operations",children:"5. Espresso WebView operations"}),"\n",(0,s.jsx)(n.p,{children:(0,s.jsx)(n.em,{children:"Espresso"})}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:'onWebView()\n    .withElement(findElement(Locator.ID, "text_input"))\n    .perform(webKeys(newTitle))\n    .withElement(findElement(Locator.ID, "button1"))\n    .perform(webClick())\n    .withElement(findElement(Locator.ID, "title"))\n    .check(webMatches(getText(), containsString(newTitle)))\n'})}),"\n",(0,s.jsx)(n.p,{children:(0,s.jsx)(n.em,{children:"Ultron"})}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:'id("text_input").webKeys(newTitle)\nid("button1").webClick()\nid("title").hasText(newTitle)\n'})}),"\n",(0,s.jsxs)(n.p,{children:["Refer to the ",(0,s.jsx)(n.a,{href:"https://github.com/open-tool/ultron/wiki/WebView",children:"wiki"})," for more details."]}),"\n",(0,s.jsx)(n.h4,{id:"6-ui-automator-operations",children:"6. UI Automator operations"}),"\n",(0,s.jsx)(n.p,{children:(0,s.jsx)(n.em,{children:"UI Automator"})}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:'val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())\ndevice\n    .findObject(By.res("com.atiurin.sampleapp:id", "button1"))\n    .click()\n'})}),"\n",(0,s.jsx)(n.p,{children:(0,s.jsx)(n.em,{children:"Ultron"})}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:"byResId(R.id.button1).click() \n"})}),"\n",(0,s.jsxs)(n.p,{children:["Refer to the ",(0,s.jsx)(n.a,{href:"https://github.com/open-tool/ultron/wiki/UI-Automator-operation",children:"wiki"})]}),"\n",(0,s.jsx)(n.hr,{}),"\n",(0,s.jsx)(n.h3,{id:"acquiring-the-result-of-any-operation-as-boolean-value",children:"Acquiring the result of any operation as Boolean value"}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:"val isButtonDisplayed = withId(R.id.button).isSuccess { isDisplayed() }\nif (isButtonDisplayed) {\n    //do some reasonable actions\n}\n"})}),"\n",(0,s.jsx)(n.hr,{}),"\n",(0,s.jsx)(n.h3,{id:"why-are-all-ultron-actions-and-assertions-more-stable",children:"Why are all Ultron actions and assertions more stable?"}),"\n",(0,s.jsx)(n.p,{children:"The framework captures a list of specified exceptions and attempts to repeat the operation during a timeout period (default is 5 seconds). Of course, you have the ability to customize the list of handled exceptions. You can also set a custom timeout for any operation."}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:'withId(R.id.result).withTimeout(10_000).hasText("Passed")\n'})}),"\n",(0,s.jsx)(n.hr,{}),"\n",(0,s.jsx)(n.h2,{id:"3-steps-to-develop-a-test-using-ultron",children:"3 steps to develop a test using Ultron"}),"\n",(0,s.jsx)(n.p,{children:"We advocate for a proper test framework architecture, division of responsibilities between layers, and other best practices. Therefore, when using Ultron, we recommend the following approach:"}),"\n",(0,s.jsxs)(n.ol,{children:["\n",(0,s.jsxs)(n.li,{children:["Create a Page Object and specify screen UI elements as ",(0,s.jsx)(n.code,{children:"Matcher<View>"})," objects."]}),"\n"]}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:'object ChatPage : Page<ChatPage>() {\n    private val messagesList = withId(R.id.messages_list)\n    private val clearHistoryBtn = withText("Clear history")\n    private val inputMessageText = withId(R.id.message_input_text)\n    private val sendMessageBtn = withId(R.id.send_button)\n}\n'})}),"\n",(0,s.jsxs)(n.p,{children:["It's recommended to make all Page Objects as ",(0,s.jsx)(n.code,{children:"object"})," and descendants of Page class.\nThis allows for the utilization of convenient Kotlin features. It also helps you to keep Page Objects stateless."]}),"\n",(0,s.jsxs)(n.ol,{start:"2",children:["\n",(0,s.jsx)(n.li,{children:"Describe user step methods in Page Object."}),"\n"]}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:"object ChatPage : Page<ChatPage>() {\n    fun sendMessage(text: String) = apply {\n        inputMessageText.typeText(text)\n        sendMessageBtn.click()\n        getMessageListItem(text).text\n             .isDisplayed()\n             .hasText(text)\n    }\n\n    fun clearHistory() = apply {\n        openContextualActionModeOverflowMenu()\n        clearHistoryBtn.click()\n    }\n}\n"})}),"\n",(0,s.jsxs)(n.p,{children:["Refer to the full code sample ",(0,s.jsx)(n.a,{href:"https://github.com/open-tool/ultron/blob/master/sample-app/src/androidTest/java/com/atiurin/sampleapp/pages/ChatPage.kt",children:"ChatPage.class"})]}),"\n",(0,s.jsxs)(n.ol,{start:"3",children:["\n",(0,s.jsx)(n.li,{children:"Call user steps in test"}),"\n"]}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:'    @Test\n    fun friendsItemCheck(){\n        FriendsListPage {\n            assertName("Janice")\n            assertStatus("Janice","Oh. My. God")\n        }\n    }\n    @Test\n    fun sendMessage(){\n        FriendsListPage.openChat("Janice")\n        ChatPage {\n            clearHistory()\n            sendMessage("test message")\n        }\n    }\n'})}),"\n",(0,s.jsxs)(n.p,{children:["Refer to the full code sample ",(0,s.jsx)(n.a,{href:"https://github.com/open-tool/ultron/blob/master/sample-app/src/androidTest/java/com/atiurin/sampleapp/tests/espresso/DemoEspressoTest.kt",children:"DemoEspressoTest.class"})]}),"\n",(0,s.jsx)(n.p,{children:"In essence, your project's architecture will look like this:"}),"\n",(0,s.jsx)(n.p,{children:(0,s.jsx)(n.a,{href:"https://github.com/open-tool/ultron/assets/12834123/b0882d34-a18d-4f1f-959b-f75796d11036",children:"acrchitecture"})}),"\n",(0,s.jsx)(n.hr,{}),"\n",(0,s.jsx)(n.h2,{id:"allure-report",children:"Allure report"}),"\n",(0,s.jsx)(n.p,{children:"Ultron has built in support to generate artifacts for Allure reports. Just apply the recommended configuration and set testIntrumentationRunner."}),"\n",(0,s.jsxs)(n.p,{children:["For the complete guide, refer to the ",(0,s.jsx)(n.a,{href:"/docs/common/allure",children:"Allure description"})]}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-kotlin",children:"@BeforeClass @JvmStatic\nfun setConfig() {\n    UltronConfig.applyRecommended()\n    UltronAllureConfig.applyRecommended()\n    UltronComposeConfig.applyRecommended() \n}\n"})}),"\n",(0,s.jsx)(n.p,{children:(0,s.jsx)(n.img,{src:"https://github.com/open-tool/ultron/assets/12834123/c05c813a-ece6-45e6-a04f-e1c92b82ffb1",alt:"allure"})}),"\n",(0,s.jsx)(n.p,{children:(0,s.jsx)(n.img,{src:"https://github.com/open-tool/ultron/assets/12834123/1f751f3d-fc58-4874-a850-acd9181bfb70",alt:"allure compose"})})]})}function h(e={}){const{wrapper:n}={...(0,o.R)(),...e.components};return n?(0,s.jsx)(n,{...e,children:(0,s.jsx)(d,{...e})}):d(e)}},5859:(e,n,t)=>{t.d(n,{A:()=>s});const s=t.p+"assets/images/ultron_banner_dark-19d655c3ec0e489a21954509ebf83391.png"},443:(e,n,t)=>{t.d(n,{A:()=>s});const s=t.p+"assets/images/ultron_banner_light-5ce63312ccf79dcbe3e8a666f2f6c2ea.png"},8453:(e,n,t)=>{t.d(n,{R:()=>r,x:()=>l});var s=t(6540);const o={},i=s.createContext(o);function r(e){const n=s.useContext(i);return s.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function l(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(o):e.components||o:r(e.components),s.createElement(i.Provider,{value:n},e.children)}}}]);