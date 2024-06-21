"use strict";(self.webpackChunkmy_website=self.webpackChunkmy_website||[]).push([[217],{4482:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>a,contentTitle:()=>s,default:()=>d,frontMatter:()=>i,metadata:()=>l,toc:()=>c});var o=t(4848),r=t(8453);const i={sidebar_position:3},s="Listeners",l={id:"common/listeners",title:"Listeners",description:"The framework has 2 types of listeners: UltronLifecycleListener & UltronRunListener",source:"@site/docs/common/listeners.md",sourceDirName:"common",slug:"/common/listeners",permalink:"/ultron/docs/common/listeners",draft:!1,unlisted:!1,tags:[],version:"current",sidebarPosition:3,frontMatter:{sidebar_position:3},sidebar:"tutorialSidebar",previous:{title:"Ultron Extension",permalink:"/ultron/docs/common/extension"}},a={},c=[{value:"UltronLifecycleListener",id:"ultronlifecyclelistener",level:2},{value:"Log operation example",id:"log-operation-example",level:3},{value:"Configuration",id:"configuration",level:3},{value:"Lifecycles",id:"lifecycles",level:3},{value:"Exclude operation from listeners monitor",id:"exclude-operation-from-listeners-monitor",level:3},{value:"UltronRunListener",id:"ultronrunlistener",level:2}];function u(e){const n={a:"a",code:"code",h1:"h1",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",strong:"strong",ul:"ul",...(0,r.R)(),...e.components};return(0,o.jsxs)(o.Fragment,{children:[(0,o.jsx)(n.h1,{id:"listeners",children:"Listeners"}),"\n",(0,o.jsx)(n.p,{children:"The framework has 2 types of listeners: UltronLifecycleListener & UltronRunListener"}),"\n",(0,o.jsx)(n.h2,{id:"ultronlifecyclelistener",children:"UltronLifecycleListener"}),"\n",(0,o.jsxs)(n.p,{children:["This one allows you to listen all stages of ",(0,o.jsx)(n.strong,{children:"Operation execution"}),"."]}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-kotlin",children:"abstract class UltronLifecycleListener {\n    /**\n     * executed before any action or assertion\n     */\n    override fun before(operation: Operation) = Unit\n\n    /**\n     * called when action or assertion failed\n     */\n    override fun afterFailure(operationResult: OperationResult<Operation>) = Unit\n    /**\n     * called when action or assertion has been executed successfully\n     */\n    override fun afterSuccess(operationResult: OperationResult<Operation>) = Unit\n    /**\n     * called in any case of action or assertion result\n     */\n    override fun after(operationResult: OperationResult<Operation>) = Unit    \n}\n"})}),"\n",(0,o.jsxs)(n.p,{children:[(0,o.jsx)(n.code,{children:"Operation"})," object contains all info about operation (name, description, type, timeout)"]}),"\n",(0,o.jsxs)(n.p,{children:[(0,o.jsx)(n.code,{children:"OperationResult"})," object contains all info about operation result (success, all exceptions that occured and exception that was thrown, description etc) and also has a reference to ",(0,o.jsx)(n.code,{children:"Operation"}),"."]}),"\n",(0,o.jsx)(n.p,{children:"All listener methods will be executed before an exception will be thrown. It gives you a guarantee that all exceptions in your tests will be processed  as you want."}),"\n",(0,o.jsx)(n.h3,{id:"log-operation-example",children:"Log operation example"}),"\n",(0,o.jsx)(n.p,{children:"For instance, here is a listener that logs everything to Ultron log."}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-kotlin",children:'class LogLifecycleListener : UltronLifecycleListener() {\n    override fun before(operation: Operation) {\n        UltronLog.info("Start execution of ${operation.name}")\n    }\n\n    override fun afterSuccess(operationResult: OperationResult<Operation>) {\n        UltronLog.info("Successfully executed ${operationResult.operation.name}")\n    }\n\n    override fun afterFailure(operationResult: OperationResult<Operation>) {\n        UltronLog.error("Failed ${operationResult.operation.name} with description: \\n" +\n                "${operationResult.description} ")\n    }\n}\n'})}),"\n",(0,o.jsx)(n.p,{children:"You can create you own custom listener in the same way."}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-kotlin",children:"class CustomLifecycleListener : UltronLifecycleListener() {...}\n"})}),"\n",(0,o.jsxs)(n.p,{children:["Add new listener for Ultron operations using ",(0,o.jsx)(n.code,{children:"UltronCommonConfig.addListener()"}),"."]}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-kotlin",children:"abstract class BaseTest {\n    companion object {\n        @BeforeClass @JvmStatic\n        fun configureUltron() {\n            UltronCommonConfig.addListener(CustomLifecycleListener())\n        }\n    }\n}\n"})}),"\n",(0,o.jsx)(n.h3,{id:"configuration",children:"Configuration"}),"\n",(0,o.jsx)(n.p,{children:"Basically we already know how to add new listener. But there are other options to configure Ultron listeners."}),"\n",(0,o.jsxs)(n.p,{children:["First of all Ultron by default already has ",(0,o.jsx)(n.a,{href:"https://github.com/alex-tiurin/ultron/blob/master/ultron/src/main/java/com/atiurin/ultron/listeners/LogLifecycleListener.kt",children:"LogLifecycleListener"})," that writes some usable info to logcat."]}),"\n",(0,o.jsx)(n.h3,{id:"lifecycles",children:"Lifecycles"}),"\n",(0,o.jsx)(n.p,{children:"Ultron has 4 different lifecycles that watch for different operations."}),"\n",(0,o.jsxs)(n.ul,{children:["\n",(0,o.jsx)(n.li,{children:"UltronEspressoOperationLifecycle"}),"\n",(0,o.jsx)(n.li,{children:"UltronWebLifecycle (WebView operations)"}),"\n",(0,o.jsx)(n.li,{children:"UltronUiAutomatorLifecycle"}),"\n",(0,o.jsx)(n.li,{children:"UltronComposeOperationLifecycle"}),"\n"]}),"\n",(0,o.jsx)(n.p,{children:"It is possible to add listener for any of these lifecycles."}),"\n",(0,o.jsx)(n.p,{children:(0,o.jsx)(n.code,{children:"UltronUiAutomatorLifecycle.addListener(CustomLifecycleListener())"})}),"\n",(0,o.jsxs)(n.p,{children:["In this case ",(0,o.jsx)(n.code,{children:"CustomLifecycleListener"})," will be applied only for UI Automator operations."]}),"\n",(0,o.jsx)(n.h3,{id:"exclude-operation-from-listeners-monitor",children:"Exclude operation from listeners monitor"}),"\n",(0,o.jsx)(n.p,{children:"Ultron allows it to exclude operation from all listeners. This option is based on operation type."}),"\n",(0,o.jsx)(n.p,{children:"For example, you've created a new operation"}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-kotlin",children:'enum class CustomUltronOperations : UltronOperationType {\n   ASSERT_HAS_ANY_CHILD\n}\nfun UltronUiObject2.assertHasAnyChild() = apply {\n    executeAssertion(\n            assertionBlock = { uiObject2ProviderBlock()!!.childCount > 0 },\n            name = "Assert $selectorDesc has any child",\n            type = CustomUltronOperations.ASSERT_HAS_ANY_CHILD,\n            description = "UiObject2 assertion \'${CustomUltronOperations.ASSERT_HAS_ANY_CHILD}\' of $selectorDesc during $timeoutMs ms",\n            timeoutMs = timeoutMs,\n            resultHandler = resultHandler\n    )\n}\n'})}),"\n",(0,o.jsx)(n.p,{children:"And you would like to exclude it from listeners for any reason no matter why."}),"\n",(0,o.jsx)(n.p,{children:"Add single line to Ultron configuration function."}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-kotlin",children:"abstract class BaseTest {\n    companion object {\n        @BeforeClass @JvmStatic\n        fun configureUltron() {\n            ... \n            UltronCommonConfig.operationsExcludedFromListeners.add(CustomUltronOperations.ASSERT_HAS_ANY_CHILD)\n        }\n    }\n}\n"})}),"\n",(0,o.jsx)(n.h2,{id:"ultronrunlistener",children:"UltronRunListener"}),"\n",(0,o.jsxs)(n.p,{children:["Allows you to add listener for Test Lifecycle. See ",(0,o.jsx)(n.a,{href:"https://github.com/open-tool/ultron/blob/master/ultron/src/main/java/com/atiurin/ultron/runner/RunListener.kt",children:"RunListener"}),"."]}),"\n",(0,o.jsxs)(n.p,{children:["It is available in case you use ",(0,o.jsx)(n.code,{children:"ultron-allure"})," and set ",(0,o.jsx)(n.code,{children:"testInstrumentationRunner"}),"."]}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-kotlin",children:'testInstrumentationRunner = "com.atiurin.ultron.allure.UltronAllureTestRunner"\n'})}),"\n",(0,o.jsx)(n.p,{children:"It could be used, for instance, to attach your custom application log to Allure Report."}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-kotlin",children:'class AppLogAttachRunListener() : UltronRunListener() {\n    override fun testFailure(failure: Failure) {\n        val logFile: File = AppLogProvider.provide()\n        val fileName = AttachUtil.attachFile(\n            name = "app_log_file",\n            file = logFile,\n            mimeType = MimeType.PLAIN_TEXT\n        )\n    }\n}\n'})}),"\n",(0,o.jsx)(n.p,{children:"Add custom RunListener to Allure config."}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-kotlin",children:"@BeforeClass @JvmStatic\nfun configureUltron() {\n    ...\n    UltronAllureConfig.addRunListener(AppLogAttachRunListener())\n}\n"})})]})}function d(e={}){const{wrapper:n}={...(0,r.R)(),...e.components};return n?(0,o.jsx)(n,{...e,children:(0,o.jsx)(u,{...e})}):u(e)}},8453:(e,n,t)=>{t.d(n,{R:()=>s,x:()=>l});var o=t(6540);const r={},i=o.createContext(r);function s(e){const n=o.useContext(i);return o.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function l(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(r):e.components||r:s(e.components),o.createElement(i.Provider,{value:n},e.children)}}}]);