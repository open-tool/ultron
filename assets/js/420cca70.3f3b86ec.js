"use strict";(self.webpackChunkmy_website=self.webpackChunkmy_website||[]).push([[231],{9172:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>r,default:()=>p,frontMatter:()=>i,metadata:()=>a,toc:()=>c});var s=n(4848),o=n(8453);const i={sidebar_position:1},r="Multiplatform",a={id:"compose/multiplatform",title:"Multiplatform",description:"Multiplatform support is in Alpha state.",source:"@site/docs/compose/multiplatform.md",sourceDirName:"compose",slug:"/compose/multiplatform",permalink:"/docs/compose/multiplatform",draft:!1,unlisted:!1,tags:[],version:"current",sidebarPosition:1,frontMatter:{sidebar_position:1},sidebar:"tutorialSidebar",previous:{title:"Compose",permalink:"/docs/compose/"},next:{title:"Android",permalink:"/docs/compose/android"}},l={},c=[{value:"<code>runComposeUiTest</code> vs <code>runUltronUiTest</code>",id:"runcomposeuitest-vs-runultronuitest",level:3},{value:"Compose Page Object",id:"compose-page-object",level:3},{value:"Ultron Page Object",id:"ultron-page-object",level:3}];function d(e){const t={a:"a",blockquote:"blockquote",code:"code",h1:"h1",h3:"h3",p:"p",pre:"pre",strong:"strong",...(0,o.R)(),...e.components};return(0,s.jsxs)(s.Fragment,{children:[(0,s.jsx)(t.h1,{id:"multiplatform",children:"Multiplatform"}),"\n",(0,s.jsxs)(t.blockquote,{children:["\n",(0,s.jsx)(t.p,{children:"Multiplatform support is in Alpha state."}),"\n"]}),"\n",(0,s.jsxs)(t.p,{children:["Compose Multiplatform provides robust tools for building and testing UI components across various platforms. One significant aspect of this is the ability to write and run common tests for your UI elements (",(0,s.jsx)(t.a,{href:"https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html#write-and-run-common-tests",children:"official doc sample"}),")."]}),"\n",(0,s.jsxs)(t.h3,{id:"runcomposeuitest-vs-runultronuitest",children:[(0,s.jsx)(t.code,{children:"runComposeUiTest"})," vs ",(0,s.jsx)(t.code,{children:"runUltronUiTest"})]}),"\n",(0,s.jsxs)(t.p,{children:["With standart Compose Testing framework you have to use ",(0,s.jsx)(t.code,{children:"runComposeUiTest"})," method to interact with UI elements."]}),"\n",(0,s.jsxs)(t.p,{children:["Here is simplified basic test sample with Compose Multiplatform. Typically it's placed in common app module, like ",(0,s.jsx)(t.code,{children:"composeApp/src/commonTest/kotlin"})]}),"\n",(0,s.jsx)(t.pre,{children:(0,s.jsx)(t.code,{className:"language-kotlin",children:'class ComposeExampleTest {\n    @Test\n    fun myTest() = runComposeUiTest {\n        setContent {\n            // reasonable UI content\n        }\n        onNode(hasTestTag("text")).assertTextEquals("Hello")\n        onNode(hasTestTag("button")).performClick()\n        onNode(hasTestTag("text")).assertTextEquals("Compose")\n    }\n}\n'})}),"\n",(0,s.jsxs)(t.p,{children:["Usage of ",(0,s.jsx)(t.code,{children:"runUltronUiTest"})," function simplifies the interaction syntax."]}),"\n",(0,s.jsx)(t.pre,{children:(0,s.jsx)(t.code,{className:"language-kotlin",children:'class UltronExampleTest {\n    @Test\n    fun myTest() = runUltronUiTest {\n        setContent {\n            // reasonable UI content\n        }\n        hasTestTag("text").assertTextEquals("Hello")\n        hasTestTag("button").click()\n        hasTestTag("text").assertTextEquals("Compose")\n    }\n}\n'})}),"\n",(0,s.jsx)(t.p,{children:"More over it makes interactions more reliable and stable."}),"\n",(0,s.jsxs)(t.p,{children:["Additionally, it becomes possible to call these interactions ",(0,s.jsx)(t.strong,{children:"EVERYWHERE"})," you want, e.g. in ",(0,s.jsx)(t.strong,{children:"Page Objects"})]}),"\n",(0,s.jsx)(t.h3,{id:"compose-page-object",children:"Compose Page Object"}),"\n",(0,s.jsxs)(t.p,{children:["Everyone knows that ",(0,s.jsx)(t.strong,{children:"Page Object"})," pattern is a good pattern. But how to use it for multiplatform tests?"]}),"\n",(0,s.jsxs)(t.p,{children:["While ",(0,s.jsx)(t.code,{children:"runComposeUiTest"})," provides the context for interaction with UI elements, like calling ",(0,s.jsx)(t.code,{children:"onNodeWithTag()"}),", moving this logic into a Page Object or any other class/method can lead to issues, as these don\u2019t have direct access to the testing API. This is because the testing API is provided by an object called ",(0,s.jsx)(t.code,{children:"SemanticsNodeInteractionProvider"}),", which needs to be passed into each object to call the testing API."]}),"\n",(0,s.jsx)(t.p,{children:"Here\u2019s an example of a modified test using the Page Object pattern:"}),"\n",(0,s.jsx)(t.pre,{children:(0,s.jsx)(t.code,{className:"language-kotlin",children:'class PageObjectMultiplatformTest {\n    @Test\n    fun myTest() = runComposeUiTest {\n        setContent {\n            // reasonable UI content\n        }\n        ExamplePage(provider = this).someStep()\n    }\n}\n\nclass ExamplePage(val provider: SemanticsNodeInteractionsProvider){\n    fun someStep(){\n        provider.onNodeWithTag("text").assertTextEquals("Hello")\n        provider.onNodeWithTag("button").performClick()\n        provider.onNodeWithTag("text").assertTextEquals("Compose")\n    }\n}\n'})}),"\n",(0,s.jsx)(t.h3,{id:"ultron-page-object",children:"Ultron Page Object"}),"\n",(0,s.jsxs)(t.p,{children:["Ultron eliminates the need to pass ",(0,s.jsx)(t.code,{children:"SemanticsNodeInteractionProvider"})," into each object. You only need to replace the ",(0,s.jsx)(t.code,{children:"runComposeUiTest"})," method with ",(0,s.jsx)(t.code,{children:"runUltronUiTest"}),"."]}),"\n",(0,s.jsx)(t.pre,{children:(0,s.jsx)(t.code,{className:"language-kotlin",children:'class UltronMultiplatformTest {\n    @Test\n    fun myTest() = runUltronUiTest {\n        setContent {\n            // reasonable UI content\n        }\n\n        UltronPage.someStep()\n    }\n}\n\nobject UltronPage {\n    fun someStep(){\n        hasTestTag("text").assertTextEquals("Hello")\n        hasTestTag("button").click()\n        hasTestTag("text").assertTextEquals("Compose")\n    }\n}\n'})})]})}function p(e={}){const{wrapper:t}={...(0,o.R)(),...e.components};return t?(0,s.jsx)(t,{...e,children:(0,s.jsx)(d,{...e})}):d(e)}},8453:(e,t,n)=>{n.d(t,{R:()=>r,x:()=>a});var s=n(6540);const o={},i=s.createContext(o);function r(e){const t=s.useContext(i);return s.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function a(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(o):e.components||o:r(e.components),s.createElement(i.Provider,{value:t},e.children)}}}]);