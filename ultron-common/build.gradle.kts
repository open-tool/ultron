import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("org.jetbrains.dokka")
    id("maven-publish")
    id("signing")
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

val dokkaOutputDir = buildDir.resolve("dokka")

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

publishing {
    publications {
        withType<MavenPublication> {
            artifact(ultronComposeJavadocJar.get())

            pom {
                name.set("Ultron Common")
                description.set("Android & Compose Multiplatform UI testing framework")
                url.set("https://github.com/open-tool/ultron")
                inceptionYear.set("2021")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                issueManagement {
                    system.set("GitHub Issues")
                    url.set("https://github.com/open-tool/ultron/issues")
                }

                developers {
                    developer {
                        id.set("alex-tiurin")
                        name.set("Aleksei Tiurin")
                        url.set("https://github.com/open-tool")
                    }
                }

                scm {
                    connection.set("scm:git@github.com:open-tool/ultron.git")
                    developerConnection.set("scm:git@github.com:open-tool/ultron.git")
                    url.set("https://github.com/open-tool/ultron")
                }
            }
        }
    }
}

tasks.withType<PublishToMavenRepository>().configureEach {
    dependsOn(tasks.withType<Sign>())
    dependsOn(ultronComposeJavadocJar)
    dependsOn(tasks.withType<Jar>())
    mustRunAfter(tasks.withType<Sign>())
}

tasks.withType<PublishToMavenLocal>().configureEach {
    dependsOn("signKotlinMultiplatformPublication")
    dependsOn("signAndroidReleasePublication")
    dependsOn("signDesktopPublication")
    dependsOn("signIosArm64Publication")
    dependsOn("signIosSimulatorArm64Publication")
    dependsOn("signIosX64Publication")
    dependsOn("signJsPublication")
    dependsOn("signWasmJsPublication")
    dependsOn("signMacosArm64Publication")
    dependsOn("signMacosX64Publication")

    mustRunAfter("signKotlinMultiplatformPublication")
    mustRunAfter("signAndroidReleasePublication")
    mustRunAfter("signDesktopPublication")
    mustRunAfter("signIosArm64Publication")
    mustRunAfter("signIosSimulatorArm64Publication")
    mustRunAfter("signIosX64Publication")
    mustRunAfter("signJsPublication")
    mustRunAfter("signWasmJsPublication")
    mustRunAfter("signMacosArm64Publication")
    mustRunAfter("signMacosX64Publication")
}

signing {
    println("Signing lib...")
    useGpgCmd()
    sign(publishing.publications)
}
