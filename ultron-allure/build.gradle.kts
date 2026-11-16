import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.vanniktech.mavenPublish)
    id("org.jetbrains.dokka")
}

group = project.findProperty("GROUP")!!
version = project.findProperty("VERSION_NAME")!!

// One Allure module for every Ultron target. `androidMain` is the allure-kotlin based pipeline for
// instrumented tests (runner, run listeners, screenrecord); every other target uses Ultron's own
// Allure 2 result writer from `skikoMain`. `commonMain` holds only the `expect` config, the attach
// strategy and `step { }`. `UltronAllureConfig` is the entry point on both sides.
kotlin {
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
    }
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    jvm("desktop")
    macosX64()
    macosArm64()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs() {
        browser()
        nodejs()
    }
    js(IR) {
        browser {
            // The js unit tests (jsTest) run under Node (`jsNodeTest`); the browser variant of the same tests
            // would need Karma and Chrome on every `check`. The browser target itself stays, it is published.
            testTask { enabled = false }
        }
        nodejs()
    }

    sourceSets {
        applyDefaultHierarchyTemplate()
        val commonMain by getting {
            dependencies {
                // Only ultron-common here: the Android variant must keep the dependency graph of the
                // Android-only ultron-allure (no Compose, no okio/kotlinx-serialization).
                api(project(":ultron-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        // Everything rendered by Skiko: desktop, native (iOS/macOS), js, wasmJs. Android is the exception.
        val skikoMain by creating {
            dependsOn(commonMain)
            dependencies {
                api(project(":ultron-compose"))
                api(libs.compose.ui.test)
                // Ultron's own Allure 2 writer lives here, not in commonMain.
                implementation(libs.okio)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.atomicfu)
            }
        }
        jvmMain {
            dependsOn(skikoMain)
            dependencies {
                // H.264 encoder and mp4 muxer of the desktop target. Not in skikoMain: the Android
                // variant records with screenrecord and must not get it.
                implementation(libs.jcodec)
            }
        }
        val desktopMain by getting {
            dependsOn(skikoMain)
            dependsOn(jvmMain.get())
        }
        val desktopTest by getting {
            dependencies {
                // PNG frames are decoded through skiko, whose native library comes with `compose.desktop.currentOs`;
                // this module does not apply the Compose plugin, so the tests name it for the machine they run on.
                val os = System.getProperty("os.name").lowercase()
                val arch = System.getProperty("os.arch").lowercase()
                val osName = when {
                    os.contains("mac") -> "macos"
                    os.contains("win") -> "windows"
                    else -> "linux"
                }
                val cpu = if (arch.contains("aarch64") || arch.contains("arm64")) "arm64" else "x64"
                implementation("org.jetbrains.skiko:skiko-awt-runtime-$osName-$cpu:${libs.versions.skiko.get()}")
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.allure.kotlin.android)
                api(libs.allure.kotlin.commons)
                api(libs.allure.kotlin.model)
                api(libs.allure.kotlin.junit4)
                api(libs.androidx.espresso.core)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.junit)
            }
        }
        val nativeMain by getting { dependsOn(skikoMain) }
        val jsWasmMain by creating {
            dependsOn(skikoMain)
        }
        val jsMain by getting {
            dependsOn(jsWasmMain)
        }
        val wasmJsMain by getting {
            dependsOn(jsWasmMain)
        }
        // `jsTest` holds the unit tests of the jsWasmMain code (in-memory store, console transport), run with
        // `jsNodeTest`. They are not shared with wasmJsTest: the wasm test executable imports Compose's
        // `skiko.mjs`, which only the Compose Gradle plugin (not applied here) puts next to the bundle.
    }
}

android {
    compileSdk = 35
    namespace = "com.atiurin.ultron.allure"
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_11
        sourceCompatibility = JavaVersion.VERSION_11
    }
}

val dokkaOutputDir = layout.buildDirectory.dir("dokka").get().asFile
tasks.dokkaHtml { outputDirectory.set(file(dokkaOutputDir)) }
val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") { delete(dokkaOutputDir) }

val ultronAllureJavadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn(deleteDokkaOutputDir, tasks.dokkaHtml)
    from(dokkaOutputDir)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates(artifactId = "ultron-allure")

    pom {
        name = "Ultron Allure"
        description = "Allure reports for Ultron tests: Android instrumented and Compose Multiplatform"
        url = "https://github.com/open-tool/ultron"
        inceptionYear = "2021"

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        issueManagement {
            system = "GitHub Issues"
            url = "https://github.com/open-tool/ultron/issues"
        }

        developers {
            developer {
                id = "alex-tiurin"
                name = "Aleksei Tiurin"
                url = "https://github.com/open-tool"
            }
        }

        scm {
            url = "https://github.com/open-tool/ultron"
            connection = "scm:git@github.com:open-tool/ultron.git"
            developerConnection = "scm:git@github.com:open-tool/ultron.git"
        }
    }
}
