plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

android {
    compileSdk = 31
    defaultConfig {
        applicationId = "com.atiurin.sampleapp"
        minSdk = 21
        targetSdk = 31
        multiDexEnabled = true
        testInstrumentationRunner = "com.atiurin.ultron.allure.UltronAllureTestRunner"
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.5"
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
    implementation(project(":ultron"))
    implementation(project(":ultron-compose"))
    implementation(project(":ultron-allure"))
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
