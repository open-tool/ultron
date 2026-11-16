import java.util.Base64
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    // Allure test configuration differs per platform (see AllureTestSetup): `skikoTest` holds the actual for
    // desktop, iOS, js and wasmJs, `androidSharedTest` the one shared by the Android unit and instrumented
    // test source sets. Extending the template keeps the default iosMain/iosTest wiring intact.
    applyDefaultHierarchyTemplate {
        common {
            group("skiko") {
                withJvm()
                withNative()
                withJs()
                withWasmJs()
            }
            group("androidShared") {
                withAndroidTarget()
            }
        }
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant {
            sourceSetTree.set(KotlinSourceSetTree.test)
        }
    }
    
    jvm("desktop")
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    js(IR){
        browser {
            testTask(Action {
                useKarma {
                    useChromeHeadless()
                    useConfigDirectory(project.projectDir.resolve("karma.config.d").resolve("js"))
                }
            })
        }
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs(){
        browser {
            testTask(Action {
                useKarma {
                    useChromeHeadless()
                    useConfigDirectory(project.projectDir.resolve("karma.config.d").resolve("wasm"))
                }
            })
        }
    }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.components.ui.tooling.preview)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.navigation.compose)
        }
        commonTest.dependencies {
            implementation(libs.compose.ui.test)
            implementation(kotlin("test"))
            implementation(project(":ultron-compose"))
            implementation(project(":ultron-allure"))
        }
        iosTest.dependencies {
            implementation(libs.okio) // reads Allure results back from the host directory
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
        val desktopTest by getting {
            dependencies {
                implementation(libs.compose.ui.test.junit4)
                implementation(compose.desktop.currentOs)
            }
        }
        @OptIn(ExperimentalWasmDsl::class)
        wasmJs()
        val wasmJsTest by getting

    }
}

android {
    namespace = "com.atiurin.samplekmp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.atiurin.samplekmp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
//    dependencies {
//        debugImplementation(compose.uiTooling)
//    }

}

dependencies {
    androidTestImplementation(libs.androidx.ui.test.junit4.android)
    androidTestImplementation(project(":ultron-compose"))
    debugImplementation(libs.androidx.ui.test.manifest)
}


// The Allure results directories are outputs of the test tasks: with org.gradle.caching=true an UP-TO-DATE or
// FROM-CACHE test task runs no test, and without the declaration it would also leave no results behind.
// Each target has its own subdirectory of build/allure-results and declares only that one: declaring the parent
// would overlap with the other targets' outputs, which disables caching and makes the tasks stale for each other.
tasks.named<Test>("desktopTest") {
    val results = layout.buildDirectory.dir("allure-results/desktop")
    environment("ULTRON_ALLURE_RESULTS_DIR", results.get().asFile.absolutePath)
    outputs.dir(results)
}

// Kotlin/Native simulator tests run through `simctl spawn`, which forwards only SIMCTL_CHILD_* variables.
tasks.withType<org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest>().configureEach {
    val results = layout.buildDirectory.dir("allure-results/ios")
    environment("SIMCTL_CHILD_ULTRON_ALLURE_RESULTS_DIR", results.get().asFile.absolutePath)
    outputs.dir(results)
}

// Browser tests keep Allure results in memory and print every file to the console the moment it is written
// (see karma.config.d/<target>); Karma stores the console in build/karma-console-<target>.log and the
// collect task decodes the lines back into files. Karma appends to that log, so the test task starts from an
// empty one, and the collect task starts from an empty directory: a run never shows results of an earlier one.
listOf("js" to "jsBrowserTest", "wasm" to "wasmJsBrowserTest").forEach { (target, browserTestTask) ->
    // Karma's basePath lives under the root project's build dir, so the config.d script writes there.
    val log = rootProject.layout.buildDirectory.file("karma-console-$target.log")
    val collect = tasks.register("collect${target.replaceFirstChar { it.uppercase() }}AllureResults") {
        val out = layout.buildDirectory.dir("allure-results/$target")
        inputs.files(log).optional()
        outputs.dir(out)
        doLast {
            val source = log.get().asFile
            val targetDir = out.get().asFile.apply { deleteRecursively(); mkdirs() }
            if (!source.isFile) { logger.lifecycle("No Karma console log at $source, nothing to collect"); return@doLast }
            val line = Regex("ULTRON_ALLURE_RESULT (\\S+) ([A-Za-z0-9+/=]+)")
            val written = mutableSetOf<String>()
            source.forEachLine { l ->
                line.find(l)?.let { m ->
                    targetDir.resolve(m.groupValues[1]).writeBytes(Base64.getDecoder().decode(m.groupValues[2]))
                    written += m.groupValues[1]
                }
            }
            logger.lifecycle("Collected ${written.size} Allure result files from the browser into $targetDir")
        }
    }
    tasks.matching { it.name == browserTestTask }.configureEach {
        doFirst { log.get().asFile.delete() }
        finalizedBy(collect)
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.atiurin.samplekmp"
            packageVersion = "1.0.0"
        }
    }
}
