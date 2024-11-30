"use strict";(self.webpackChunkmy_website=self.webpackChunkmy_website||[]).push([[321],{2969:(e,n,s)=>{s.r(n),s.d(n,{assets:()=>c,contentTitle:()=>i,default:()=>h,frontMatter:()=>r,metadata:()=>l,toc:()=>d});var t=s(4848),o=s(8453);const r={sidebar_position:2},i="UltronTest",l={id:"common/ultrontest",title:"UltronTest",description:"UltronTest is a powerful base class provided by the Ultron framework that enables the definition of common preconditions and postconditions for tests. By extending this class, you can streamline test setup and teardown, ensuring consistent execution across your test suite.",source:"@site/docs/common/ultrontest.md",sourceDirName:"common",slug:"/common/ultrontest",permalink:"/ultron/docs/common/ultrontest",draft:!1,unlisted:!1,tags:[],version:"current",sidebarPosition:2,frontMatter:{sidebar_position:2},sidebar:"tutorialSidebar",previous:{title:"Allure",permalink:"/ultron/docs/common/allure"},next:{title:"Ultron Extension",permalink:"/ultron/docs/common/extension"}},c={},d=[{value:"Features of <code>UltronTest</code>",id:"features-of-ultrontest",level:2},{value:"Example",id:"example",level:3},{value:"Key Methods",id:"key-methods",level:3},{value:"Key Features of the <code>test</code> Method",id:"key-features-of-the-test-method",level:3},{value:"Purpose of <code>before</code>, <code>go</code>, and <code>after</code>",id:"purpose-of-before-go-and-after",level:3},{value:"Using <code>softAssertion</code> for Flexible Error Handling",id:"using-softassertion-for-flexible-error-handling",level:2},{value:"Example of <code>softAssertion</code>",id:"example-of-softassertion",level:3},{value:"Explanation",id:"explanation",level:3},{value:"Benefits of <code>UltronTest</code> usage",id:"benefits-of-ultrontest-usage",level:2}];function a(e){const n={br:"br",code:"code",h1:"h1",h2:"h2",h3:"h3",hr:"hr",li:"li",p:"p",pre:"pre",strong:"strong",ul:"ul",...(0,o.R)(),...e.components};return(0,t.jsxs)(t.Fragment,{children:[(0,t.jsx)(n.h1,{id:"ultrontest",children:"UltronTest"}),"\n",(0,t.jsxs)(n.p,{children:[(0,t.jsx)(n.code,{children:"UltronTest"})," is a powerful base class provided by the Ultron framework that enables the definition of common preconditions and postconditions for tests. By extending this class, you can streamline test setup and teardown, ensuring consistent execution across your test suite."]}),"\n",(0,t.jsxs)(n.h2,{id:"features-of-ultrontest",children:["Features of ",(0,t.jsx)(n.code,{children:"UltronTest"})]}),"\n",(0,t.jsxs)(n.ul,{children:["\n",(0,t.jsxs)(n.li,{children:[(0,t.jsx)(n.strong,{children:"Pre-Test Actions:"})," Define actions to be executed before each test."]}),"\n",(0,t.jsxs)(n.li,{children:[(0,t.jsx)(n.strong,{children:"Post-Test Actions:"})," Define actions to be executed after each test."]}),"\n",(0,t.jsxs)(n.li,{children:[(0,t.jsx)(n.strong,{children:"Lifecycle Management:"})," Execute code once before all tests in a class using ",(0,t.jsx)(n.code,{children:"beforeFirstTest"}),"."]}),"\n",(0,t.jsxs)(n.li,{children:[(0,t.jsx)(n.strong,{children:"Customizable Test Execution:"})," Suppress pre-test or post-test actions when needed."]}),"\n"]}),"\n",(0,t.jsx)(n.h3,{id:"example",children:"Example"}),"\n",(0,t.jsxs)(n.p,{children:["Here is an example of using ",(0,t.jsx)(n.code,{children:"UltronTest"}),":"]}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:'class SampleUltronFlowTest : UltronTest() {\n\n    @OptIn(ExperimentalUltronApi::class)\n    override val beforeFirstTest = {\n        UltronLog.info("Before Class")\n    }\n\n    override val beforeTest = {\n        UltronLog.info("Before test common")\n    }\n\n    override val afterTest = {\n        UltronLog.info("After test common")\n    }\n\n    /**\n     * The order of method execution is as follows::\n     * beforeFirstTest, beforeTest, before, go, after, afterTest\n     */\n    @Test\n    fun someTest1() = test {\n        before {\n            UltronLog.info("Before TestMethod 1")\n        }.go {\n            UltronLog.info("Run TestMethod 1")\n        }.after {\n            UltronLog.info("After TestMethod 1")\n        }\n    }\n    \n    /**\n     * An order of methods execution is follow: before, go, after\n     * `beforeFirstTest` - Not executed, as it is only run once and was already executed before `someTest1`.\n     * `beforeTest` - Not executed because it was suppressed using `suppressCommonBefore`.\n     * `afterTest` - Not executed because it was suppressed using `suppressCommonAfter`.\n     */\n    @Test\n    fun someTest2() = test(\n        suppressCommonBefore = true,\n        suppressCommonAfter = true\n    ) {\n        before {\n            UltronLog.info("Before TestMethod 2")\n        }.go {\n            UltronLog.info("Run TestMethod 2")\n        }.after {\n            UltronLog.info("After TestMethod 2")\n        }\n    }\n\n    /**\n     * An order of methods execution is follow: beforeTest, test, afterTest\n     * `beforeFirstTest` - Not executed, since it was executed before `someTest1`\n     */\n    @Test\n    fun someTest3() = test {\n        UltronLog.info("UltronTest simpleTest")\n    }\n}\n'})}),"\n",(0,t.jsx)(n.h3,{id:"key-methods",children:"Key Methods"}),"\n",(0,t.jsxs)(n.ul,{children:["\n",(0,t.jsxs)(n.li,{children:[(0,t.jsx)(n.strong,{children:(0,t.jsx)(n.code,{children:"beforeFirstTest"})}),": Code executed once before all tests in a class."]}),"\n",(0,t.jsxs)(n.li,{children:[(0,t.jsx)(n.strong,{children:(0,t.jsx)(n.code,{children:"beforeTest"})}),": Code executed before each test."]}),"\n",(0,t.jsxs)(n.li,{children:[(0,t.jsx)(n.strong,{children:(0,t.jsx)(n.code,{children:"afterTest"})}),": Code executed after each test."]}),"\n",(0,t.jsxs)(n.li,{children:[(0,t.jsx)(n.strong,{children:(0,t.jsx)(n.code,{children:"test"})}),": Executes a test with options to suppress pre-test or post-test actions."]}),"\n"]}),"\n",(0,t.jsxs)(n.h3,{id:"key-features-of-the-test-method",children:["Key Features of the ",(0,t.jsx)(n.code,{children:"test"})," Method"]}),"\n",(0,t.jsxs)(n.ul,{children:["\n",(0,t.jsxs)(n.li,{children:["\n",(0,t.jsxs)(n.p,{children:[(0,t.jsx)(n.strong,{children:"Test Context Recreation:"}),(0,t.jsx)(n.br,{}),"\n","The ",(0,t.jsx)(n.code,{children:"test"})," method automatically recreates the ",(0,t.jsx)(n.code,{children:"UltronTestContext"})," for each test execution, ensuring a clean and isolated state for the test context."]}),"\n"]}),"\n",(0,t.jsxs)(n.li,{children:["\n",(0,t.jsxs)(n.p,{children:[(0,t.jsx)(n.strong,{children:"Soft Assertion Reset:"}),(0,t.jsx)(n.br,{}),"\n","Any exceptions captured during ",(0,t.jsx)(n.code,{children:"softAssertions"})," in the previous test are cleared at the start of each new ",(0,t.jsx)(n.code,{children:"test"})," execution, maintaining a clean state."]}),"\n"]}),"\n",(0,t.jsxs)(n.li,{children:["\n",(0,t.jsxs)(n.p,{children:[(0,t.jsx)(n.strong,{children:"Lifecycle Management:"}),"\nIt invokes ",(0,t.jsx)(n.code,{children:"beforeTest"})," and ",(0,t.jsx)(n.code,{children:"afterTest"})," methods around your test logic unless explicitly suppressed."]}),"\n"]}),"\n"]}),"\n",(0,t.jsx)(n.hr,{}),"\n",(0,t.jsxs)(n.h3,{id:"purpose-of-before-go-and-after",children:["Purpose of ",(0,t.jsx)(n.code,{children:"before"}),", ",(0,t.jsx)(n.code,{children:"go"}),", and ",(0,t.jsx)(n.code,{children:"after"})]}),"\n",(0,t.jsxs)(n.ul,{children:["\n",(0,t.jsxs)(n.li,{children:["\n",(0,t.jsxs)(n.p,{children:[(0,t.jsxs)(n.strong,{children:[(0,t.jsx)(n.code,{children:"before"}),":"]})," Defines preconditions or setup actions that must be performed before the main test logic is executed.\nThese actions might include preparing data, navigating to a specific screen, or setting up the environment."]}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:'before {\n    UltronLog.info("Setting up preconditions for TestMethod 2")\n}\n'})}),"\n"]}),"\n",(0,t.jsxs)(n.li,{children:["\n",(0,t.jsxs)(n.p,{children:[(0,t.jsxs)(n.strong,{children:[(0,t.jsx)(n.code,{children:"go"}),":"]})," Encapsulates the core logic or actions of the test. This is where the actual operations being tested are performed, such as interacting with UI elements or executing specific functionality."]}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:'go {\n    UltronLog.info("Executing the main logic of TestMethod 2")\n}\n'})}),"\n"]}),"\n",(0,t.jsxs)(n.li,{children:["\n",(0,t.jsxs)(n.p,{children:[(0,t.jsxs)(n.strong,{children:[(0,t.jsx)(n.code,{children:"after"}),":"]})," Block is used for postconditions or cleanup actions that need to occur after the main test logic has executed. This might include verifying results, resetting the environment, or clearing resources."]}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:'after {\n    UltronLog.info("Cleaning up after TestMethod 2")\n}\n'})}),"\n"]}),"\n"]}),"\n",(0,t.jsx)(n.p,{children:"These methods help clearly separate test phases, making tests easier to read and maintain."}),"\n",(0,t.jsxs)(n.h2,{id:"using-softassertion-for-flexible-error-handling",children:["Using ",(0,t.jsx)(n.code,{children:"softAssertion"})," for Flexible Error Handling"]}),"\n",(0,t.jsxs)(n.p,{children:["The ",(0,t.jsx)(n.code,{children:"softAssertion"})," mechanism in Ultron allows tests to catch and verify multiple exceptions during their execution without failing immediately. This feature is particularly useful for validating multiple conditions within a single test."]}),"\n",(0,t.jsxs)(n.h3,{id:"example-of-softassertion",children:["Example of ",(0,t.jsx)(n.code,{children:"softAssertion"})]}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:'class SampleTest : UltronTest() {\n    @Test\n    fun softAssertionTest() {\n        softAssertion(failOnException = false) {\n            hasText("NotExistText").withTimeout(100).assertIsDisplayed()\n            hasTestTag("NotExistTestTag").withTimeout(100).assertHasClickAction()\n        }\n        verifySoftAssertions()\n    }\n}\n'})}),"\n",(0,t.jsxs)(n.p,{children:["The ",(0,t.jsx)(n.code,{children:"softAssertion"})," mechanism does not inherently depend on ",(0,t.jsx)(n.code,{children:"UltronTest"}),".\nYou can use ",(0,t.jsx)(n.code,{children:"softAssertion"})," independently of the ",(0,t.jsx)(n.code,{children:"UltronTest"})," base class. However, in such cases, you must manually clear exceptions between tests to ensure they do not persist across test executions."]}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-kotlin",children:"class SampleTest {\n    @Test\n    fun softAssertionTest() {\n        UltronCommonConfig.testContext.softAnalyzer.clear()\n        softAssertion() {\n            //assert smth\n        }\n    }\n}\n"})}),"\n",(0,t.jsx)(n.h3,{id:"explanation",children:"Explanation"}),"\n",(0,t.jsxs)(n.ul,{children:["\n",(0,t.jsxs)(n.li,{children:[(0,t.jsx)(n.strong,{children:"Fail on Exception:"})," By default (",(0,t.jsx)(n.code,{children:"failOnException = true"}),"), ",(0,t.jsx)(n.code,{children:"softAssertion"})," will throw an exception after completing all operations within its block if any failures occur."]}),"\n",(0,t.jsxs)(n.li,{children:[(0,t.jsx)(n.strong,{children:"Manual Verification:"})," If ",(0,t.jsx)(n.code,{children:"failOnException"})," is set to ",(0,t.jsx)(n.code,{children:"false"}),", you can explicitly verify all caught exceptions at the end of the test using ",(0,t.jsx)(n.code,{children:"verifySoftAssertions()"}),"."]}),"\n"]}),"\n",(0,t.jsx)(n.p,{children:"This approach ensures granular control over how exceptions are handled and reported, making it easier to analyze and debug test failures."}),"\n",(0,t.jsx)(n.hr,{}),"\n",(0,t.jsxs)(n.h2,{id:"benefits-of-ultrontest-usage",children:["Benefits of ",(0,t.jsx)(n.code,{children:"UltronTest"})," usage"]}),"\n",(0,t.jsxs)(n.ul,{children:["\n",(0,t.jsx)(n.li,{children:"Simplifies test setup and teardown with consistent preconditions and postconditions."}),"\n",(0,t.jsx)(n.li,{children:"Enhances error handling by allowing multiple assertions within a single test."}),"\n",(0,t.jsx)(n.li,{children:"Improves test readability and maintainability."}),"\n"]}),"\n",(0,t.jsxs)(n.p,{children:["By leveraging ",(0,t.jsx)(n.code,{children:"UltronTest"})," and ",(0,t.jsx)(n.code,{children:"softAssertion"}),", you can build robust and flexible UI tests for your applications."]})]})}function h(e={}){const{wrapper:n}={...(0,o.R)(),...e.components};return n?(0,t.jsx)(n,{...e,children:(0,t.jsx)(a,{...e})}):a(e)}},8453:(e,n,s)=>{s.d(n,{R:()=>i,x:()=>l});var t=s(6540);const o={},r=t.createContext(o);function i(e){const n=t.useContext(r);return t.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function l(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(o):e.components||o:i(e.components),t.createElement(r.Provider,{value:n},e.children)}}}]);