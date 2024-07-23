object Versions {
    const val kotlin = "2.0.0"
    const val androidToolsBuildGradle = "8.3.1"
    const val androidMavenGradlePlugin = "2.1"
    const val publishPlugin = "0.29.0"
    const val dokkaPlugin = "1.9.20"

    const val recyclerView = "1.2.1"
    const val espresso = "3.6.1"
    const val uiautomator = "2.2.0"
    const val accessibility = "4.0.0"
    const val hamcrestCore = "2.2"
    const val compose = "1.6.4"
    const val androidXTest = "1.4.0"
    const val junit = "4.13.2"
    const val allure = "2.4.0"
    //sample-app

    const val coroutines = "1.4.2"
    const val ktx = "1.6.0"
    const val supportV4 = "1.0.0"
    const val appcompat = "1.3.1"
    const val material = "1.4.0"
    const val constraintlayout = "2.1.4"
    const val cardview = "1.0.0"
    const val robolectric = "4.8.1"
    const val mockito = "3.9.0"
    const val activityCompose = "1.8.2"

    const val junitExt = "1.1.2"
}

object Plugins {
    val androidToolsBuildGradle = "com.android.tools.build:gradle:${Versions.androidToolsBuildGradle}"
    val androidMavenGradle = "com.github.dcendents:android-maven-gradle-plugin:${Versions.androidMavenGradlePlugin}"
    val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    val publishPlugin = "com.vanniktech:gradle-maven-publish-plugin:${Versions.publishPlugin}"
    val dokka = "org.jetbrains.dokka:dokka-gradle-plugin:${Versions.dokkaPlugin}"
}

object Libs {
    val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
    val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    val espressoContrib = "androidx.test.espresso:espresso-contrib:${Versions.espresso}"
    val espressoWeb = "androidx.test.espresso:espresso-web:${Versions.espresso}"
    val uiautomator = "androidx.test.uiautomator:uiautomator:${Versions.uiautomator}"
    val accessibility =
        "com.google.android.apps.common.testing.accessibility.framework:accessibility-test-framework:${Versions.accessibility}"
    val hamcrestCore = "org.hamcrest:hamcrest-core:${Versions.hamcrestCore}"
    val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
    val androidXRunner = "androidx.test:runner:${Versions.androidXTest}"
    val composeUiTest = "androidx.compose.ui:ui-test-junit4:${Versions.compose}"
    val junit = "junit:junit:${Versions.junit}"
    // allure
    val allureCommon = "io.qameta.allure:allure-kotlin-commons:${Versions.allure}"
    val allureModel = "io.qameta.allure:allure-kotlin-model:${Versions.allure}"
    val allureJunit4 = "io.qameta.allure:allure-kotlin-junit4:${Versions.allure}"
    val allureAndroid = "io.qameta.allure:allure-kotlin-android:${Versions.allure}"

    // sample-app
    val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    val androidXKtx = "androidx.core:core-ktx:${Versions.ktx}"
    val supportV4 = "androidx.legacy:legacy-support-v4:${Versions.supportV4}"
    val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    val material = "com.google.android.material:material:${Versions.material}"
    val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintlayout}"
    val cardview = "androidx.cardview:cardview:${Versions.cardview}"

    // sample-app compose
    val composeUi = "androidx.compose.ui:ui:${Versions.compose}"
    val composeUiTooling = "androidx.compose.ui:ui-tooling:${Versions.compose}" // Tooling support (Previews, etc.)
    val composeFoundation = "androidx.compose.foundation:foundation:${Versions.compose}" // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    val composeMaterial = "androidx.compose.material:material:${Versions.compose}"
    val composeMaterialIconsCore = "androidx.compose.material:material-icons-core:${Versions.compose}" // Material design icons
    val composeMaterialIconsExtend = "androidx.compose.material:material-icons-extended:${Versions.compose}"
    val activityCompose = "androidx.activity:activity-compose:${Versions.activityCompose}"

    // sample-app test
    val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
    val mockito = "org.mockito:mockito-core:${Versions.mockito}"
    val androidXTextCore = "androidx.test:core:${Versions.androidXTest}"

    //sample-app androidTest
    val espressoIdlingResource = "androidx.test.espresso:espresso-idling-resource:${Versions.espresso}"
    val espressoIntents = "androidx.test.espresso:espresso-intents:${Versions.espresso}"
    val espressoAccessibility = "androidx.test.espresso:espresso-accessibility:${Versions.espresso}"
    val espressoConcurrent = "androidx.test.espresso.idling:idling-concurrent:${Versions.espresso}"
    val androidXRules = "androidx.test:rules:${Versions.androidXTest}"
    val androidXTruth = "androidx.test.ext:truth:${Versions.androidXTest}"
    val androidXJunit = "androidx.test.ext:junit:${Versions.junitExt}"
}