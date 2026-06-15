---
sidebar_position: 2
title: Release notes
---

# Release notes

This page summarizes Ultron releases from 2.5.1 onward. Older releases are available on the [GitHub Releases](https://github.com/open-tool/ultron/releases) page.

## Version 2.6.2

_Released October 29, 2025_ · [GitHub release](https://github.com/open-tool/ultron/releases/tag/2.6.2) · [Full changelog](https://github.com/open-tool/ultron/compare/2.6.1...2.6.2)

- Fixed Activity rule handling to avoid Activity idle state issues. [#127](https://github.com/open-tool/ultron/pull/127)
- Added automatic scrolling to target views. [#133](https://github.com/open-tool/ultron/pull/133)
- Fixed offset handling for Compose swipe options.

## Version 2.6.1

_Released August 26, 2025_ · [GitHub release](https://github.com/open-tool/ultron/releases/tag/2.6.1) · [Full changelog](https://github.com/open-tool/ultron/compare/2.6.0-alpha04...2.6.1)

- Migrated publishing to Maven Central Portal. [#128](https://github.com/open-tool/ultron/pull/128)
- Added named RecyclerView lookup support. [#124](https://github.com/open-tool/ultron/pull/124)
- Added `withDescription()` support for Compose lists. [#125](https://github.com/open-tool/ultron/pull/125)
- Updated `kotlinx-datetime` to 0.7.1. [#132](https://github.com/open-tool/ultron/pull/132)
- Added `assertSize` to `UltronComposeCollectionInteraction`. [#132](https://github.com/open-tool/ultron/pull/132)
- Updated `UltronComposeList.getItem` so `positionPropertyKey` can be found in any descendant of a list item. [#132](https://github.com/open-tool/ultron/pull/132)

## Version 2.6.0-alpha03

_Released May 31, 2025_ · [GitHub release](https://github.com/open-tool/ultron/releases/tag/2.6.0-alpha03) · [Full changelog](https://github.com/open-tool/ultron/compare/2.6.0-alpha02...2.6.0-alpha03)

- Added UI block descriptions for Compose, Espresso, Espresso Web, and UI Automator. [#121](https://github.com/open-tool/ultron/pull/121)
- Added [UI block documentation](https://open-tool.github.io/ultron/docs/common/uiblock).

## Version 2.6.0-alpha02

_Released May 27, 2025_ · [GitHub release](https://github.com/open-tool/ultron/releases/tag/2.6.0-alpha02) · [Full changelog](https://github.com/open-tool/ultron/compare/2.6.0-alpha01...2.6.0-alpha02)

- Updated Gradle, Android Gradle Plugin, Kotlin, Compose, and related build dependencies. [#118](https://github.com/open-tool/ultron/pull/118)
- Welcomed first-time contributor [@tamimattafi](https://github.com/tamimattafi).

## Version 2.6.0-alpha01

_Released May 26, 2025_ · [GitHub release](https://github.com/open-tool/ultron/releases/tag/2.6.0-alpha01) · [Full changelog](https://github.com/open-tool/ultron/compare/2.5.6...2.6.0-alpha01)

- Introduced the new `UltronRecyclerView` implementation. [#115](https://github.com/open-tool/ultron/pull/115) [#116](https://github.com/open-tool/ultron/pull/116) [#117](https://github.com/open-tool/ultron/pull/117)
- Updated the RecyclerView documentation, including the [`withRecyclerView` parameters](https://open-tool.github.io/ultron/docs/android/recyclerview#parameters-for-withrecyclerview-method) and [`UltronRecyclerViewImpl` parameter](https://open-tool.github.io/ultron/docs/android/recyclerview#ultronrecyclerviewimpl) sections. [#119](https://github.com/open-tool/ultron/pull/119)
- Welcomed first-time contributor [@dabrynskiy](https://github.com/dabrynskiy).

## Version 2.5.6

_Released March 15, 2025_ · [GitHub release](https://github.com/open-tool/ultron/releases/tag/2.5.6) · [Full changelog](https://github.com/open-tool/ultron/compare/2.5.5...2.5.6)

- Added Compose support for `CustomAccessibilityAction`. [#110](https://github.com/open-tool/ultron/pull/110)
- Added DatePicker testing samples for the [app component](https://github.com/open-tool/ultron/blob/master/sample-app/src/main/java/com/atiurin/sampleapp/compose/DatePicker.kt) and [UI test](https://github.com/open-tool/ultron/blob/master/sample-app/src/androidTest/java/com/atiurin/sampleapp/tests/compose/elements/DataPickerTest.kt).

## Version 2.5.5

_Released February 21, 2025_ · [GitHub release](https://github.com/open-tool/ultron/releases/tag/2.5.5) · [Full changelog](https://github.com/open-tool/ultron/compare/2.5.4...2.5.5)

- Extended the swipe API and fixed action chain behavior. [#106](https://github.com/open-tool/ultron/pull/106)

## Version 2.5.4

_Released February 2, 2025_ · [GitHub release](https://github.com/open-tool/ultron/releases/tag/2.5.4) · [Full changelog](https://github.com/open-tool/ultron/compare/2.5.3...2.5.4)

- Added Compose debug listener support and custom UI element descriptions. [#102](https://github.com/open-tool/ultron/pull/102)
- Fixed Espresso soft assertion behavior. [#103](https://github.com/open-tool/ultron/pull/103)

:::warning Breaking change

`UltronWebElement.executeOperation` returns a nullable result starting with this version.

:::

## Version 2.5.3

_Released January 4, 2025_ · [GitHub release](https://github.com/open-tool/ultron/releases/tag/2.5.3) · [Full changelog](https://github.com/open-tool/ultron/compare/2.5.2...2.5.3)

- Added Compose test environment support. [#99](https://github.com/open-tool/ultron/pull/99)
- Added `UltronComposeConfig.doBetweenOperationRetry`.
- Added `ComposeTestEnvironment` with access to `SemanticsNodeInteractionsProvider`, `Density`, and `MainTestClock`.
- Added `withComposeTestEnvironment` for running code with a `ComposeTestEnvironment` scope.

:::warning Breaking change

`SemanticsNodeInteractionProviderContainer` was replaced with `ComposeTestContainer`.

:::

## Version 2.5.2

_Released December 1, 2024_ · [GitHub release](https://github.com/open-tool/ultron/releases/tag/2.5.2) · [Full changelog](https://github.com/open-tool/ultron/compare/2.5.1...2.5.2)

- Fixed failed test processing. [#98](https://github.com/open-tool/ultron/pull/98)

## Version 2.5.1

_Released November 30, 2024_ · [GitHub release](https://github.com/open-tool/ultron/releases/tag/2.5.1)

- Removed the Guava dependency. [#90](https://github.com/open-tool/ultron/pull/90)
- Added `UltronTest` and soft assertion documentation. [#97](https://github.com/open-tool/ultron/pull/97)
- See the [`UltronTest` guide](https://open-tool.github.io/ultron/docs/common/ultrontest) for usage details.
