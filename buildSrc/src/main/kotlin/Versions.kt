object Versions {
    val kotlin = "1.5.31"
    val androidMavenGradlePlugin = "2.1"

    val recyclerView = "1.2.1"
    val espresso = "3.4.0"
    val uiautomator = "2.2.0"
    val accessibility = "4.0.0"
    val hamcrestCore = "2.2"
    val compose = "1.1.1"
    val androidXTest = "1.4.0"
    val junit = "4.13.2"
    //sample-app

    val coroutines = "1.4.2"
    val ktx = "1.6.0"
    val supportV4 = "1.0.0"
    val appcompat = "1.3.1"
    val material = "1.4.0"
    val constraintlayout = "2.1.4"
    val cardview = "1.0.0"
    val robolectric = "4.8.1"
    val mockito = "3.9.0"

    val junitExt = "1.1.3"
}

//'material'          : '1.4.0',
//'constraintlayout'  : '2.1.4',
//'cardview'          : '1.0.0',
//'robolectric'       : '4.8.1',
//'mockito'           : '3.9.0',
object Plugins {
    val androidMavenGradle = "com.github.dcendents:android-maven-gradle-plugin:${Versions.androidMavenGradlePlugin}"
    val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
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
    val composeActivity = "androidx.activity:activity-compose:${Versions.compose}"

    // sample-app test
    val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
    val mockito = "org.mockito:mockito-core:${Versions.mockito}"
    val androidXTextCore = "androidx.test:core:${Versions.androidXTest}"

    //sample-app androidTest
    val espressoIdlingResource = "androidx.test.espresso:espresso-idling-resource:${Versions.espresso}"
    val espressoIntents = "androidx.test.espresso:espresso-intents:${Versions.espresso}"
    val espressoAccessibility = "androidx.test.espresso:espresso-accessibility:${Versions.espresso}"
    val espressoConcurrent = "androidx.test.espresso:espresso-concurrent:${Versions.espresso}"
    val androidXRules = "androidx.test:rules:${Versions.androidXTest}"
    val androidXTruth = "androidx.test.ext:truth:${Versions.androidXTest}"
    val androidXJunit = "androidx.test.ext:junit:${Versions.junitExt}"
}