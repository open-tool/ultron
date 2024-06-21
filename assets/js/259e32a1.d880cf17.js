"use strict";(self.webpackChunkmy_website=self.webpackChunkmy_website||[]).push([[991],{5282:(n,e,o)=>{o.r(e),o.d(e,{assets:()=>a,contentTitle:()=>r,default:()=>u,frontMatter:()=>s,metadata:()=>l,toc:()=>c});var t=o(4848),i=o(8453);const s={sidebar_position:4},r="Configuration",l={id:"intro/configuration",title:"Configuration",description:"Each library of the framework has it's own config onject.",source:"@site/docs/intro/configuration.md",sourceDirName:"intro",slug:"/intro/configuration",permalink:"/docs/intro/configuration",draft:!1,unlisted:!1,tags:[],version:"current",sidebarPosition:4,frontMatter:{sidebar_position:4},sidebar:"tutorialSidebar",previous:{title:"Dependencies Management",permalink:"/docs/intro/dependencies"},next:{title:"Compose",permalink:"/docs/compose/"}},a={},c=[{value:"UltronComposeConfig",id:"ultroncomposeconfig",level:3},{value:"UltronCommonConfig",id:"ultroncommonconfig",level:3},{value:"UltronConfig",id:"ultronconfig",level:3},{value:"UltronAllureConfig",id:"ultronallureconfig",level:3}];function d(n){const e={a:"a",code:"code",h1:"h1",h3:"h3",hr:"hr",li:"li",p:"p",pre:"pre",strong:"strong",ul:"ul",...(0,i.R)(),...n.components};return(0,t.jsxs)(t.Fragment,{children:[(0,t.jsx)(e.h1,{id:"configuration",children:"Configuration"}),"\n",(0,t.jsx)(e.p,{children:"Each library of the framework has it's own config onject."}),"\n",(0,t.jsxs)(e.ul,{children:["\n",(0,t.jsxs)(e.li,{children:[(0,t.jsx)(e.code,{children:"UltronComposeConfig"})," - ultron-compose"]}),"\n",(0,t.jsxs)(e.li,{children:[(0,t.jsx)(e.code,{children:"UltronConfig"})," - ultron-android"]}),"\n",(0,t.jsxs)(e.li,{children:[(0,t.jsx)(e.code,{children:"UltronAllureConfig"})," - ultron-allure"]}),"\n",(0,t.jsxs)(e.li,{children:[(0,t.jsx)(e.code,{children:"UltronCommonConfig"})," - inside each library"]}),"\n"]}),"\n",(0,t.jsxs)(e.p,{children:["You can use recommended configuration and just apply it in ",(0,t.jsx)(e.strong,{children:"BaseTest"})," class (",(0,t.jsx)(e.a,{href:"https://github.com/open-tool/ultron/blob/master/sample-app/src/androidTest/java/com/atiurin/sampleapp/tests/BaseTest.kt#L29",children:"sample"}),") :"]}),"\n",(0,t.jsx)(e.pre,{children:(0,t.jsx)(e.code,{className:"language-kotlin",children:"@BeforeClass\n@JvmStatic\nfun config() {\n    UltronConfig.applyRecommended()\n    UltronAllureConfig.applyRecommended()\n    UltronComposeConfig.applyRecommended()\n}\n\n"})}),"\n",(0,t.jsx)(e.h3,{id:"ultroncomposeconfig",children:"UltronComposeConfig"}),"\n",(0,t.jsx)(e.hr,{}),"\n",(0,t.jsx)(e.p,{children:"Manages configurations for Compose part of the framework"}),"\n",(0,t.jsx)(e.pre,{children:(0,t.jsx)(e.code,{className:"language-kotlin",children:"UltronComposeConfig.apply {\n    operationTimeoutMs = 10_000\n    lazyColumnOperationTimeoutMs = 15_000\n    operationPollingTimeoutMs = 100\n    lazyColumnItemSearchLimit = 100\n}\n"})}),"\n",(0,t.jsx)(e.h3,{id:"ultroncommonconfig",children:"UltronCommonConfig"}),"\n",(0,t.jsx)(e.hr,{}),"\n",(0,t.jsx)(e.p,{children:"Provides an ability to config common parameters for your testing framework."}),"\n",(0,t.jsx)(e.pre,{children:(0,t.jsx)(e.code,{className:"language-kotlin",children:'UltronCommonConfig.apply {\n    logToFile = true\n    operationTimeoutMs = 10_000\n    logDateFormat = "MM-dd HH:mm:ss.SSS"\n}\n'})}),"\n",(0,t.jsx)(e.p,{children:"It also gives an API to add/remove operations listeners"}),"\n",(0,t.jsx)(e.pre,{children:(0,t.jsx)(e.code,{className:"language-kotlin",children:"UltronCommonConfig.addListener(CustomListener())\n"})}),"\n",(0,t.jsx)(e.h3,{id:"ultronconfig",children:"UltronConfig"}),"\n",(0,t.jsx)(e.hr,{}),"\n",(0,t.jsxs)(e.p,{children:[(0,t.jsx)(e.code,{children:"UltronConfig"})," object is responsible for configuring and managing settings related to the Espresso, EspressoWeb, and UiAutomator."]}),"\n",(0,t.jsxs)(e.p,{children:["You can set custom main settings using ",(0,t.jsx)(e.code,{children:"apply"})," method."]}),"\n",(0,t.jsx)(e.pre,{children:(0,t.jsx)(e.code,{className:"language-kotlin",children:"UltronConfig.apply {\n    accelerateUiAutomator = true\n    operationTimeoutMs = 10_000\n}\n"})}),"\n",(0,t.jsxs)(e.ul,{children:["\n",(0,t.jsxs)(e.li,{children:[(0,t.jsx)(e.code,{children:"UltronConfig.Espresso"})," nested Object:"]}),"\n"]}),"\n",(0,t.jsx)(e.p,{children:"Manages configurations specific to the Espresso part of the framework.\nProvides settings related to timeouts, view matchers, result analyzers, and action/assertion configurations."}),"\n",(0,t.jsx)(e.pre,{children:(0,t.jsx)(e.code,{className:"language-kotlin",children:"UltronConfig.Espresso.RECYCLER_VIEW_LOAD_TIMEOUT = 20_000\nUltronConfig.Espresso.RECYCLER_VIEW_OPERATIONS_TIMEOUT = 10_000\nUltronConfig.Espresso.RECYCLER_VIEW_ITEM_SEARCH_LIMIT = 100\nUltronConfig.Espresso.INCLUDE_VIEW_HIERARCHY_TO_EXCEPTION = true // false by default\nUltronConfig.Espresso.setResultAnalyzer { operationResult ->\n    // set custom operations result analyzer \n}\n"})}),"\n",(0,t.jsxs)(e.ul,{children:["\n",(0,t.jsxs)(e.li,{children:[(0,t.jsx)(e.code,{children:"UltronConfig.Espresso.ViewActionConfig"})," and ",(0,t.jsx)(e.code,{children:"UltronConfig.Espresso.ViewAssertionConfig"})," nested Objects:"]}),"\n"]}),"\n",(0,t.jsx)(e.p,{children:"Manage configurations for Espresso view actions and view assertions, respectively.\nProvide settings for allowed exceptions and result handlers."}),"\n",(0,t.jsx)(e.pre,{children:(0,t.jsx)(e.code,{className:"language-kotlin",children:"UltronConfig.Espresso.ViewActionConfig.allowedExceptions.add(CustomViewException::class.java)\nUltronConfig.Espresso.ViewAssertionConfig.allowedExceptions.add(CustomViewException::class.java)\n"})}),"\n",(0,t.jsxs)(e.ul,{children:["\n",(0,t.jsxs)(e.li,{children:[(0,t.jsx)(e.code,{children:"UltronConfig.Espresso.WebInteractionOperationConfig"})," nested Object:"]}),"\n"]}),"\n",(0,t.jsx)(e.p,{children:"Manages configurations for Espresso web interaction operations.\nProvides settings for allowed exceptions and result handlers."}),"\n",(0,t.jsx)(e.pre,{children:(0,t.jsx)(e.code,{className:"language-kotlin",children:"UltronConfig.Espresso.WebInteractionOperationConfig.allowedExceptions.add(CustomJSException::class.java)\n"})}),"\n",(0,t.jsxs)(e.ul,{children:["\n",(0,t.jsxs)(e.li,{children:[(0,t.jsx)(e.code,{children:"UltronConfig.UiAutomator"})," nested Object:"]}),"\n"]}),"\n",(0,t.jsx)(e.p,{children:"Manages configurations specific to the UiAutomator part of the framework.\nProvides settings related to timeouts, result analyzers, and UiDevice configurations."}),"\n",(0,t.jsx)(e.pre,{children:(0,t.jsx)(e.code,{className:"language-kotlin",children:"UltronConfig.UiAutomator.OPERATION_TIMEOUT = 15_000\nval device = UltronConfig.UiAutomator.uiDevice\nUltronConfig.UiAutomator.UiObject2Config.allowedExceptions.add(CustomViewException::class.java)\n"})}),"\n",(0,t.jsxs)(e.ul,{children:["\n",(0,t.jsxs)(e.li,{children:[(0,t.jsx)(e.code,{children:"UltronConfig.UiAutomator.UiObjectConfig"})," and ",(0,t.jsx)(e.code,{children:"UltronConfig.UiAutomator.UiObject2Config"})," nested Objects:"]}),"\n"]}),"\n",(0,t.jsx)(e.p,{children:"Manage configurations for UiAutomator operations using UiSelector and BySelector, respectively.\nProvide settings for allowed exceptions and result handlers."}),"\n",(0,t.jsx)(e.h3,{id:"ultronallureconfig",children:"UltronAllureConfig"}),"\n",(0,t.jsx)(e.hr,{}),"\n",(0,t.jsx)(e.p,{children:"Help us to configure Allure report."}),"\n",(0,t.jsx)(e.pre,{children:(0,t.jsx)(e.code,{className:"language-kotlin",children:"UltronAllureConfig.apply {\n    addScreenshotPolicy =  mutableSetOf(\n        AllureAttachStrategy.TEST_FAILURE,\n        AllureAttachStrategy.OPERATION_FAILURE,\n        AllureAttachStrategy.OPERATION_SUCCESS\n    )\n    addHierarchyPolicy = mutableSetOf(\n        AllureAttachStrategy.TEST_FAILURE\n    )\n    attachLogcat = false\n    attachUltronLog = true\n    addConditionsToReport = true\n    detailedAllureReport = true\n}\n"})}),"\n",(0,t.jsx)(e.p,{children:"It also allow us to add or remove RunListener."}),"\n",(0,t.jsx)(e.pre,{children:(0,t.jsx)(e.code,{className:"language-kotlin",children:"UltronAllureConfig.addRunListener(LogcatAttachRunListener())\nUltronAllureConfig.removeRunListener(LogcatAttachRunListener::class.java)\n"})})]})}function u(n={}){const{wrapper:e}={...(0,i.R)(),...n.components};return e?(0,t.jsx)(e,{...n,children:(0,t.jsx)(d,{...n})}):d(n)}},8453:(n,e,o)=>{o.d(e,{R:()=>r,x:()=>l});var t=o(6540);const i={},s=t.createContext(i);function r(n){const e=t.useContext(s);return t.useMemo((function(){return"function"==typeof n?n(e):{...e,...n}}),[e,n])}function l(n){let e;return e=n.disableParentContext?"function"==typeof n.components?n.components(i):n.components||i:r(n.components),t.createElement(s.Provider,{value:e},n.children)}}}]);