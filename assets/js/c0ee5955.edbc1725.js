"use strict";(self.webpackChunkmy_website=self.webpackChunkmy_website||[]).push([[92],{2902:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>a,contentTitle:()=>r,default:()=>p,frontMatter:()=>s,metadata:()=>l,toc:()=>c});var o=t(4848),i=t(8453);const s={sidebar_position:2},r="Connect to project",l={id:"intro/connect",title:"Connect to project",description:"The framework has three libraries that could be added as dependencies.",source:"@site/docs/intro/connect.md",sourceDirName:"intro",slug:"/intro/connect",permalink:"/ultron/docs/intro/connect",draft:!1,unlisted:!1,tags:[],version:"current",sidebarPosition:2,frontMatter:{sidebar_position:2},sidebar:"tutorialSidebar",previous:{title:"Introduction",permalink:"/ultron/docs/"},next:{title:"Dependencies Management",permalink:"/ultron/docs/intro/dependencies"}},a={},c=[{value:"Android application instrumented UI tests",id:"android-application-instrumented-ui-tests",level:3},{value:"Compose Multiplatform UI tests",id:"compose-multiplatform-ui-tests",level:3}];function d(e){const n={code:"code",h1:"h1",h3:"h3",li:"li",p:"p",pre:"pre",strong:"strong",ul:"ul",...(0,i.R)(),...e.components};return(0,o.jsxs)(o.Fragment,{children:[(0,o.jsx)(n.h1,{id:"connect-to-project",children:"Connect to project"}),"\n",(0,o.jsx)(n.p,{children:"The framework has three libraries that could be added as dependencies."}),"\n",(0,o.jsxs)(n.ul,{children:["\n",(0,o.jsxs)(n.li,{children:[(0,o.jsx)(n.code,{children:"com.atiurin:ultron-compose"})," - could be used both for Android application and Compose Multiplatform UI tests"]}),"\n",(0,o.jsxs)(n.li,{children:[(0,o.jsx)(n.code,{children:"com.atiurin:ultron-android"})," - native Android UI tests based on Espresso(including web part) and UI Automator"]}),"\n",(0,o.jsxs)(n.li,{children:[(0,o.jsx)(n.code,{children:"com.atiurin:ultron-allure"})," - Allure report support for Android application UI tests"]}),"\n"]}),"\n",(0,o.jsxs)(n.p,{children:["You need ",(0,o.jsx)(n.strong,{children:"mavenCentral"})," repository."]}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-kotlin",children:"repositories {\n    mavenCentral()\n}\n"})}),"\n",(0,o.jsx)(n.h3,{id:"android-application-instrumented-ui-tests",children:"Android application instrumented UI tests"}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-kotlin",children:'dependencies {\n    androidTestImplementation("com.atiurin:ultron-compose:<latest_version>")\n    androidTestImplementation("com.atiurin:ultron-android:<latest_version>")\n    androidTestImplementation("com.atiurin:ultron-allure:<latest_version>")\n}\n'})}),"\n",(0,o.jsx)(n.h3,{id:"compose-multiplatform-ui-tests",children:"Compose Multiplatform UI tests"}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-kotlin",children:'kotlin {\n    sourceSets {\n         commonTest.dependencies {\n            implementation("com.atiurin:ultron-compose:<latest_version>")\n        }\n    }\n}\n'})}),"\n",(0,o.jsxs)(n.p,{children:["Since Multiplatform support in alpha state it's possible to have some problems with ",(0,o.jsx)(n.code,{children:"commonTest"})," usage."]}),"\n",(0,o.jsx)(n.p,{children:"In this case you can specify dependencies in relevant part."}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-kotlin",children:'kotlin {\n    androidTarget {\n        @OptIn(ExperimentalKotlinGradlePluginApi::class)\n        instrumentedTestVariant {\n            ...\n            dependencies {\n                implementation("com.atiurin:ultron-compose:<latest_version>")\n            }\n        }\n    }\n    sourceSets {\n        val desktopTest by getting {\n            dependencies {\n                implementation("com.atiurin:ultron-compose:<latest_version>")\n            }\n        }\n    }\n}\n'})})]})}function p(e={}){const{wrapper:n}={...(0,i.R)(),...e.components};return n?(0,o.jsx)(n,{...e,children:(0,o.jsx)(d,{...e})}):d(e)}},8453:(e,n,t)=>{t.d(n,{R:()=>r,x:()=>l});var o=t(6540);const i={},s=o.createContext(i);function r(e){const n=o.useContext(s);return o.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function l(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(i):e.components||i:r(e.components),o.createElement(s.Provider,{value:n},e.children)}}}]);