
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.vanniktech.mavenPublish)
    id("org.jetbrains.dokka")
}

group = project.findProperty("GROUP")!!
version = project.findProperty("VERSION_NAME")!!

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
    }
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
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
    wasmJs(){
        browser()
        nodejs()
    }
    js(IR){
        browser()
        nodejs()
    }

    @OptIn(ExperimentalComposeLibrary::class)
    sourceSets {
        applyDefaultHierarchyTemplate()
        val commonMain by getting {
            dependencies {
                api(project(":ultron-common"))
                implementation(kotlin("reflect"))
                api(libs.kotlin.test)
                api(compose.uiTest)
                implementation(libs.atomicfu)
            }
        }
        val androidMain by getting {
            dependencies {
                api(project(":ultron-common"))
                implementation(Libs.androidXRunner)
                api(Libs.composeUiTest)
            }
        }
        val shared by creating {
            dependsOn(commonMain)
        }
        jvmMain {
            dependsOn(shared)
            dependencies {
                api(kotlin("stdlib"))
            }
        }
        val desktopMain by getting {
            dependsOn(shared)
            dependsOn(jvmMain.get())
            dependencies {
                api(project(":ultron-common"))
                implementation(compose.uiTest)
            }
        }
        val nativeMain by getting { dependsOn(shared) }

        val jsWasmMain by creating {
            dependsOn(shared)
        }
        val jsMain by getting {
            dependsOn(jsWasmMain)
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        val wasmJsMain by getting {
            dependsOn(jsWasmMain)
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
    }
}

android {
    compileSdk = 35
    namespace = "com.atiurin.ultron.compose"
    defaultConfig {
        minSdk = 21
        multiDexEnabled = true
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_11
        sourceCompatibility = JavaVersion.VERSION_11
    }
}

val dokkaOutputDir = buildDir.resolve("dokka")
tasks.dokkaHtml { outputDirectory.set(file(dokkaOutputDir)) }
val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") { delete(dokkaOutputDir) }

val ultronComposeJavadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn(deleteDokkaOutputDir, tasks.dokkaHtml)
    from(dokkaOutputDir)
}
mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates(artifactId = "ultron-compose")

    pom {
        name = "Ultron Common"
        description = "Android & Compose Multiplatform UI testing framework"
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