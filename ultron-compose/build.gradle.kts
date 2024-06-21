plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    id("com.vanniktech.maven.publish")
}

kotlin {
    jvm()
    wasmJs()
//    js(IR) {}
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":ultron-common"))
            implementation(kotlin("reflect"))
            implementation(libs.kotlin.test)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
            implementation(libs.atomicfu)
        }
        val androidMain by getting {
            dependencies {
                api(project(":ultron-common"))
                implementation(Libs.androidXRunner)
                api(Libs.composeUiTest)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
//        val jsMain by getting {
//            dependencies {
//                implementation(kotlin("stdlib-js"))
//            }
//        }
//        val wasmJsMain by getting {
//            dependencies {
//                implementation(kotlin("stdlib"))
//            }
//        }
    }
}
android {
    compileSdk = 34
    namespace = "com.atiurin.ultron.compose"
    defaultConfig {
        minSdk = 16
        multiDexEnabled = true
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
}

tasks {
    val javadoc by creating(Javadoc::class) {
        options {
            this as StandardJavadocDocletOptions
            addStringOption("Xdoclint:none", "-quiet")
            addStringOption("Xmaxwarns", "1")
            addStringOption("charSet", "UTF-8")
        }
    }

    val javadocJar by creating(Jar::class) {
        dependsOn(javadoc)
        from(javadoc.destinationDir)
    }
}