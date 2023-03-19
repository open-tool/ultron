//plugins {
//    id("com.android.application")
//    id("kotlin-android")
//    id("kotlin-android-extensions")
//}
//
//android {
//    compileSdk = 31
//    defaultConfig {
//        applicationId = "com.atiurin.sampleapp"
//        minSdk = 16
//        targetSdk = 31
//        multiDexEnabled = true
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//    }
//
//    compileOptions {
//        targetCompatibility = JavaVersion.VERSION_1_8
//        sourceCompatibility = JavaVersion.VERSION_1_8
//    }
//    buildFeatures {
//        compose = true
//    }
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }
//
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.0.5"
//    }
////
////    packagingOptions {
////        exclude 'META-INF/DEPENDENCIES'
////        exclude 'META-INF/LICENSE'
////        exclude 'META-INF/LICENSE.txt'
////        exclude 'META-INF/license.txt'
////        exclude 'META-INF/NOTICE'
////        exclude 'META-INF/NOTICE.txt'
////        exclude 'META-INF/notice.txt'
////        exclude "META-INF/AL2.0"
////        exclude "META-INF/LGPL2.1"
////        exclude("META-INF/*.kotlin_module")
////    }
//}
//
//dependencies {
//    implementation (project (":utlron"))
//    implementation (project (":utlron-compose"))
//    implementation (Libs.kotlinStdlib)
//    implementation (Libs.coroutines)
//    implementation (Libs.appcompat)
//    implementation (Libs.androidXKtx)
//    implementation (Libs.supportV4)
//    implementation (Libs.material)
//    implementation (Libs.constraintLayout)
//    implementation (Libs.recyclerView)
//    implementation (Libs.cardview)
//    implementation (Libs.espressoIdlingResource)
//
//    //compose
//    implementation(Libs.composeUi)
//    implementation(Libs.composeUiT)
//    implementation(Libs.composeUiTooling)
//    implementation(Libs.composeFoundation)
//    implementation(Libs.composeMaterial)
//    implementation(Libs.composeMaterialIconsCore)
//    implementation(Libs.composeMaterialIconsExtend)
//    implementation(Libs.composeActivity)
//
//
//    // AndroidJUnitRunner and JUnit Rules
//    testImplementation (Libs.junit)
//    testImplementation (Libs.robolectric)
//    testImplementation (Libs.mockito)
//    testImplementation (Libs.androidXTextCore)
//
//    androidTestImplementation(Libs.androidXRules)
//    androidTestImplementation(Libs.androidXTruth)
//    androidTestImplementation(Libs.androidXJunit)
//    // Espresso dependencies
//    androidTestImplementation(Libs.espressoIntents)
//    androidTestImplementation(Libs.espressoAccessibility)
//    androidTestImplementation(Libs.espressoConcurrent)
//}
