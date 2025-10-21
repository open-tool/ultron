import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    id("org.jetbrains.dokka")
}

group = project.findProperty("GROUP")!!
version = project.findProperty("VERSION_NAME")!!

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
        browser()
        nodejs()
    }
    sourceSets {
        applyDefaultHierarchyTemplate()
        val commonMain by getting {
            dependencies {
                implementation(libs.okio)
                implementation(libs.kotlinx.datetime)
                implementation(libs.atomicfu)
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation(libs.androidx.monitor)
                api(Libs.uiautomator)
                api(Libs.junit)
            }
        }
        val shared by creating {
            dependsOn(commonMain)
            dependencies {
                api(libs.kotlinx.coroutines.core)
            }
        }
        // desktop
        jvmMain {
            dependsOn(shared)
            dependencies {
                api(kotlin("stdlib"))
            }
        }
        val desktopMain by getting {
            dependsOn(shared)
            dependsOn(jvmMain.get())
        }
        // native
        val nativeMain by getting { dependsOn(shared) }
        // js
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
    namespace = "com.atiurin.ultron.common"
    compileSdk = 35
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

val dokkaOutputDir = layout.buildDirectory.dir("dokka").get().asFile

tasks.dokkaHtml.configure {
    outputDirectory.set(file(dokkaOutputDir))
}

val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") {
    delete(dokkaOutputDir)
}

val ultronComposeJavadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn(deleteDokkaOutputDir, tasks.dokkaHtml)
    from(dokkaOutputDir)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates(artifactId = "ultron-common")

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


