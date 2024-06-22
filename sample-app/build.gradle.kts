plugins {
    id("com.android.application")
    id("kotlin-android")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.atiurin.sampleapp"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.atiurin.sampleapp"
        minSdk = 21
        targetSdk = 34
        multiDexEnabled = true
        testInstrumentationRunner = "com.atiurin.ultron.allure.UltronAllureTestRunner"
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    packagingOptions {
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/LICENSE")
        resources.excludes.add("META-INF/LICENSE.txt")
        resources.excludes.add("META-INF/license.txt")
        resources.excludes.add("META-INF/NOTICE")
        resources.excludes.add("META-INF/NOTICE.txt")
        resources.excludes.add("META-INF/notice.txt")
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
        resources.excludes.add("META-INF/*.kotlin_module")
    }
}

dependencies {
    implementation("com.atiurin:ultron-android:2.5.0-alpha01")
    implementation("com.atiurin:ultron-compose:2.5.0-alpha01")
    implementation("com.atiurin:ultron-allure:2.5.0-alpha01")
    implementation(Libs.kotlinStdlib)
    implementation(Libs.coroutines)
    implementation(Libs.appcompat)
    implementation(Libs.androidXKtx)
    implementation(Libs.supportV4)
    implementation(Libs.material)
    implementation(Libs.constraintLayout)
    implementation(Libs.recyclerView)
    implementation(Libs.cardview)
    implementation(Libs.espressoIdlingResource)

    //compose
    implementation(Libs.composeUi)
    implementation(Libs.composeUiTooling)
    implementation(Libs.composeFoundation)
    implementation(Libs.composeMaterial)
    implementation(Libs.composeMaterialIconsCore)
    implementation(Libs.composeMaterialIconsExtend)
    implementation(Libs.activityCompose)

    // AndroidJUnitRunner and JUnit Rules
    testImplementation(Libs.junit)
    testImplementation(Libs.robolectric)
    testImplementation(Libs.mockito)
    testImplementation(Libs.androidXTextCore)

    androidTestImplementation(Libs.androidXRules)
    androidTestImplementation(Libs.androidXTruth)
    androidTestImplementation(Libs.androidXJunit)
    // Espresso dependencies
    androidTestImplementation(Libs.espressoIntents)
    androidTestImplementation(Libs.espressoAccessibility)
    androidTestImplementation(Libs.espressoConcurrent)
}
