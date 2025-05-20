plugins {
    id("com.android.application")
    id("kotlin-android")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.atiurin.sampleapp"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.atiurin.sampleapp"
        minSdk = 21
        targetSdk = 35
        multiDexEnabled = true
        testInstrumentationRunner = "com.atiurin.sampleapp.framework.CustomTestRunner"
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
        }
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
    implementation(project(":ultron-compose"))
    implementation(project(":ultron-allure"))
    implementation(project(":ultron-android"))
    implementation(Libs.kotlinStdlib)
    implementation(Libs.coroutines)
    implementation(Libs.appcompat)
    implementation(Libs.androidXKtx)
    implementation(Libs.supportV4)
    implementation(Libs.material)
    implementation(Libs.material3)
    implementation(Libs.constraintLayout)
    implementation(Libs.recyclerView)
    implementation(Libs.cardview)
    implementation(Libs.espressoIdlingResource)
    implementation(libs.androidx.navigation.compose)
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
