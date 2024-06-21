"use strict";(self.webpackChunkmy_website=self.webpackChunkmy_website||[]).push([[801],{2381:(e,t,i)=>{i.r(t),i.d(t,{assets:()=>a,contentTitle:()=>s,default:()=>m,frontMatter:()=>r,metadata:()=>l,toc:()=>o});var n=i(4848),c=i(8453);const r={sidebar_position:3},s="RecyclerView",l={id:"android/recyclerview",title:"RecyclerView",description:"Terms",source:"@site/docs/android/recyclerview.md",sourceDirName:"android",slug:"/android/recyclerview",permalink:"/docs/android/recyclerview",draft:!1,unlisted:!1,tags:[],version:"current",sidebarPosition:3,frontMatter:{sidebar_position:3},sidebar:"tutorialSidebar",previous:{title:"WebView",permalink:"/docs/android/webview"},next:{title:"UI Automator",permalink:"/docs/android/uiautomator"}},a={},o=[{value:"Terms",id:"terms",level:2},{value:"UltronRecyclerView",id:"ultronrecyclerview",level:2},{value:"<em>Best practice</em> - save <code>UltronRecyclerView</code> as page class properties",id:"best-practice---save-ultronrecyclerview-as-page-class-properties",level:3},{value:"UltronRecyclerViewItem",id:"ultronrecyclerviewitem",level:2},{value:"Simple Item",id:"simple-item",level:3},{value:"Complex item with children",id:"complex-item-with-children",level:3},{value:"<em>Best practice</em> - add a method to Page class that returns <code>FriendRecyclerItem</code>",id:"best-practice---add-a-method-to-page-class-that-returns-friendrecycleritem",level:3}];function d(e){const t={code:"code",em:"em",h1:"h1",h2:"h2",h3:"h3",img:"img",li:"li",p:"p",pre:"pre",strong:"strong",ul:"ul",...(0,c.R)(),...e.components};return(0,n.jsxs)(n.Fragment,{children:[(0,n.jsx)(t.h1,{id:"recyclerview",children:"RecyclerView"}),"\n",(0,n.jsx)(t.h2,{id:"terms",children:"Terms"}),"\n",(0,n.jsx)(t.p,{children:"Before we go forward we need to define some terms:"}),"\n",(0,n.jsxs)(t.ul,{children:["\n",(0,n.jsxs)(t.li,{children:["RecyclerView - list of some items (a standard Android framework class). Ultron has a class that wraps an interaction with RecyclerView - ",(0,n.jsx)(t.code,{children:"UltronRecyclerView"}),"."]}),"\n",(0,n.jsxs)(t.li,{children:["RecyclerViewItem - single item of RecyclerView list (there is a class ",(0,n.jsx)(t.code,{children:"UltronRecyclerViewItem"}),")"]}),"\n",(0,n.jsxs)(t.li,{children:["RecyclerViewItem.child - child element of RecyclerViewItem (just a term, there is no special class to work with child elements). So ",(0,n.jsx)(t.em,{children:"RecyclerViewItem.child"})," could be considered as a simple android View."]}),"\n"]}),"\n",(0,n.jsx)(t.p,{children:(0,n.jsx)(t.img,{src:"https://user-images.githubusercontent.com/12834123/107883156-4008d000-6efe-11eb-9764-8c57e767e5e2.png",alt:"Terms"})}),"\n",(0,n.jsx)(t.h2,{id:"ultronrecyclerview",children:"UltronRecyclerView"}),"\n",(0,n.jsxs)(t.p,{children:["Create an instance of ",(0,n.jsx)(t.code,{children:"UltronRecyclerView"})," by calling a method ",(0,n.jsx)(t.code,{children:"withRecyclerView(..)"})]}),"\n",(0,n.jsx)(t.pre,{children:(0,n.jsx)(t.code,{className:"language-kotlin",children:"withRecyclerView(R.id.recycler_friends).assertSize(CONTACTS.size)\n"})}),"\n",(0,n.jsx)(t.p,{children:"There is a list of methods to interact with RecyclerView and it could be extended."}),"\n",(0,n.jsxs)(t.h3,{id:"best-practice---save-ultronrecyclerview-as-page-class-properties",children:[(0,n.jsx)(t.em,{children:"Best practice"})," - save ",(0,n.jsx)(t.code,{children:"UltronRecyclerView"})," as page class properties"]}),"\n",(0,n.jsx)(t.pre,{children:(0,n.jsx)(t.code,{className:"language-kotlin",children:'object FriendsListPage : Page<FriendsListPage>() {\n    // param loadTimeout in ms specifies a time of waiting while RecyclerView items will be loaded\n    val recycler = withRecyclerView(R.id.recycler_friends, loadTimeout = 10_000L) \n    fun someStep(){\n        recycler.assertEmpty()\n        recycler.hasContentDescription("Description")\n    }\n}\n'})}),"\n",(0,n.jsxs)(t.p,{children:[(0,n.jsx)(t.code,{children:"UltronRecyclerView"})," api"]}),"\n",(0,n.jsx)(t.pre,{children:(0,n.jsx)(t.code,{className:"language-kotlin",children:"// ----- assertions -----\nassertEmpty()                                 // Asserts RecyclerView has no item\nassertSize(expected: Int)                     // Asserts RecyclerView list has [expected] items count during\nassertHasItemAtPosition(position: Int)        // Asserts RecyclerView list has item at [position]\nassertMatches(matcher: Matcher<View>)         // Assert RecyclerView matches custom condition\nassertItemNotExist(matcher: Matcher<View>, timeoutMs: Long) // watch java doc to understand how it works\nassertItemNotExistImmediately(matcher: Matcher<View>, timeoutMs: Long)\nisDisplayed()\nisNotDisplayed()\ndoesNotExist()\nisEnabled()\nisNotEnabled()\nhasContentDescription(contentDescription: String)\nhasContentDescription(resourceId: Int)\nhasContentDescription(charSequenceMatcher: Matcher<CharSequence>)\ncontentDescriptionContains(text: String)\n// ----- item providers for simple UltronRecyclerViewItem -----\n// all item provider methods has params [autoScroll: Boolean = true, scrollOffset: Int = 0]. It's shown only once but all of them has it\nitem(matcher: Matcher<View>, autoScroll: Boolean = true, scrollOffset: Int = 0): UltronRecyclerViewItem \nitem(position: Int, ..): UltronRecyclerViewItem \nfirstItem(..): UltronRecyclerViewItem\nlastItem(..): UltronRecyclerViewItem\n\n// Sometimes it is impossible to provide unique matcher for RecyclerView item\n// There is a set of methods to access not unique items by matcher and index\n// index is a value from 0 to lastIndex of matched items\nitemMatched(matcher: Matcher<View>, index: Int): UltronRecyclerViewItem\nfirstItemMatched(matcher: Matcher<View>, ..): UltronRecyclerViewItem\nlastItemMatched(matcher: Matcher<View>, ..): UltronRecyclerViewItem\n\n// ----- item providers for UltronRecyclerViewItem subclasses -----\n// following methods return a generic type T which is a subclass of UltronRecyclerViewItem\ngetItem(matcher: Matcher<View>, autoScroll: Boolean = true, scrollOffset: Int = 0): T  \ngetItem(position: Int, ..): T  \ngetFirstItem(..): T \ngetLastItem(..): T\n\n// ----- in case it's impossible to define unique matcher for `UltronRecyclerViewItem` -----\ngetItemMatched(matcher: Matcher<View>, index: Int, ..): T\ngetFirstItemMatched(matcher: Matcher<View>, ..): T\ngetLastItemMatched(matcher: Matcher<View>, ..): T\n"})}),"\n",(0,n.jsx)(t.h2,{id:"ultronrecyclerviewitem",children:"UltronRecyclerViewItem"}),"\n",(0,n.jsxs)(t.p,{children:[(0,n.jsx)(t.code,{children:"UltronRecyclerView"})," provides an access to ",(0,n.jsx)(t.code,{children:"UltronRecyclerViewItem"}),"."]}),"\n",(0,n.jsx)(t.h3,{id:"simple-item",children:"Simple Item"}),"\n",(0,n.jsxs)(t.p,{children:["If you don't need to interact with item child just use methods like ",(0,n.jsx)(t.code,{children:"item"}),", ",(0,n.jsx)(t.code,{children:"firstItem"}),", ",(0,n.jsx)(t.code,{children:"lastItem"}),", ",(0,n.jsx)(t.code,{children:"itemMatched"})," and etc"]}),"\n",(0,n.jsx)(t.pre,{children:(0,n.jsx)(t.code,{className:"language-kotlin",children:'recycler.item(position = 10, autoScroll = true).click() // find item at position 10 and scroll to this item \nrecycler.item(matcher = hasDescendant(withText("Janice"))).isDisplayed()\nrecycler.firstItem().click() //take first RecyclerView item\nrecycler.lastItem().isCompletelyDisplayed()\n\n// if it\'s impossible to specify unique matcher for target item\nval matcher = hasDescendant(withText("Friend"))\nrecycler.itemMatched(matcher, index = 2).click() //return 3rd matched item, because index starts from zero\nrecycler.firstItemMatched(matcher).isDisplayed()\nrecycler.lastItemMatched(matcher).isDisplayed()\nrecycler.getItemsAdapterPositionList(matcher) // return positions of all matched items\n'})}),"\n",(0,n.jsx)(t.p,{children:"You don't need to worry about scroll to item. By default autoscroll in all item accessor method equals true."}),"\n",(0,n.jsx)(t.h3,{id:"complex-item-with-children",children:"Complex item with children"}),"\n",(0,n.jsxs)(t.p,{children:["It's often required to interact with item child. The best solution will be to describe children as properties of ",(0,n.jsx)(t.code,{children:"UltronRecyclerViewItem"})," subclass."]}),"\n",(0,n.jsx)(t.pre,{children:(0,n.jsx)(t.code,{className:"language-kotlin",children:"class FriendRecyclerItem : UltronRecyclerViewItem() {\n    val avatar by child { withId(R.id.avatar) }\n    val name by child { withId(R.id.tv_name) }\n    val status by child { withId(R.id.tv_status) }\n}\n"})}),"\n",(0,n.jsx)(t.p,{children:(0,n.jsxs)(t.strong,{children:["Note: you have to use delegated initialisation with ",(0,n.jsx)(t.code,{children:"by child"}),"."]})}),"\n",(0,n.jsxs)(t.p,{children:["Now you're able to get ",(0,n.jsx)(t.code,{children:"FriendRecyclerItem"})," object using methods ",(0,n.jsx)(t.code,{children:"getItem"}),", ",(0,n.jsx)(t.code,{children:"getFirstItem"}),", ",(0,n.jsx)(t.code,{children:"getLastItem"})," etc"]}),"\n",(0,n.jsx)(t.pre,{children:(0,n.jsx)(t.code,{className:"language-kotlin",children:'recycler.getItem<FriendRecyclerItem>(position = 10, autoScroll = true).status.hasText("UNAGI")\nrecycler.getItem<FriendRecyclerItem>(matcher = hasDescendant(withText("Janice"))).status.textContains("Oh. My")\nrecycler.getFirstItem<FriendRecyclerItem>().avatar.click() //take first RecyclerView item\nrecycler.getLastItem<FriendRecyclerItem>().isCompletelyDisplayed()\n\n// if it\'s impossible to specify unique matcher for target item\nval matcher = hasDescendant(withText(containsString("Friend")))\nrecycler.getItemMatched<FriendRecyclerItem>(matcher, index = 2).name.click() //return 3rd matched item, because index starts from zero\nrecycler.getFirstItemMatched<FriendRecyclerItem>(matcher).name.hasText("Friend1")\nrecycler.getLastItemMatched<FriendRecyclerItem>(matcher).avatar.isDisplayed()\n'})}),"\n",(0,n.jsxs)(t.h3,{id:"best-practice---add-a-method-to-page-class-that-returns-friendrecycleritem",children:[(0,n.jsx)(t.em,{children:"Best practice"})," - add a method to Page class that returns ",(0,n.jsx)(t.code,{children:"FriendRecyclerItem"})]}),"\n",(0,n.jsx)(t.pre,{children:(0,n.jsx)(t.code,{className:"language-kotlin",children:"object FriendsListPage : Page<FriendsListPage>() {\n    val recycler = withRecyclerView(R.id.recycler_friends)\n    fun getListItem(contactName: String): FriendRecyclerItem {\n        return recycler.getItem(hasDescendant(allOf(withId(R.id.tv_name), withText(contactName))))\n    }\n    fun getListItem(positions: Int): FriendRecyclerItem {\n        return recycler.getItem(positions)\n    }\n}\n"})}),"\n",(0,n.jsxs)(t.p,{children:["use ",(0,n.jsx)(t.code,{children:"getListItem"})," inside ",(0,n.jsx)(t.code,{children:"FriendsListPage"})," steps"]}),"\n",(0,n.jsx)(t.pre,{children:(0,n.jsx)(t.code,{className:"language-kotlin",children:"object FriendsListPage : Page<FriendsListPage>() {\n    ...\n    fun assertStatus(name: String, status: String) = apply {\n        getListItem(name).status.hasText(status).isDisplayed()\n    }\n}\n"})}),"\n",(0,n.jsxs)(t.p,{children:[(0,n.jsx)(t.code,{children:"UltronRecyclerViewItem"})," api"]}),"\n",(0,n.jsx)(t.pre,{children:(0,n.jsx)(t.code,{className:"language-kotlin",children:"//actions \nscrollToItem(offset: Int = 0)\nclick()\nlongClick()\ndoubleClick()\nswipeUp()\nswipeDown()\nswipeLeft()\nswipeRight()\nperform(viewAction: ViewAction)\n\n//assertions\nisDisplayed()\nisNotDisplayed()\nisCompletelyDisplayed()\nisDisplayingAtLeast(percentage: Int)\nisClickable()\nisNotClickable()\nisEnabled()\nisNotEnabled()\nassertMatches(condition: Matcher<View>)\nhasContentDescription(contentDescription: String)\nhasContentDescription(resourceId: Int)\nhasContentDescription(charSequenceMatcher: Matcher<CharSequence>)\ncontentDescriptionContains(text: String)\n\n//general\ngetViewHolder(): RecyclerView.ViewHolder?\ngetChild(childMatcher: Matcher<View>): Matcher<View> //return matcher to a child element\nwithTimeout(timeoutMs: Long) //set custom timeout for the next operation\nwithResultHandler(..) // allows you to process action on item by your own way\n\n// click options\nclickTopLeft(offsetX: Int = 0, offsetY: Int = 0)\nclickTopCenter(offsetY: Int)\nclickTopRight(offsetX: Int = 0, offsetY: Int = 0)\nclickCenterRight(offsetX: Int = 0)\nclickBottomRight(offsetX: Int = 0, offsetY: Int = 0)\nclickBottomCenter(offsetY: Int = 0)\nclickBottomLeft(offsetX: Int = 0, offsetY: Int = 0)\nclickCenterLeft(offsetX: Int = 0)\n"})})]})}function m(e={}){const{wrapper:t}={...(0,c.R)(),...e.components};return t?(0,n.jsx)(t,{...e,children:(0,n.jsx)(d,{...e})}):d(e)}},8453:(e,t,i)=>{i.d(t,{R:()=>s,x:()=>l});var n=i(6540);const c={},r=n.createContext(c);function s(e){const t=n.useContext(r);return n.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function l(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(c):e.components||c:s(e.components),n.createElement(r.Provider,{value:t},e.children)}}}]);