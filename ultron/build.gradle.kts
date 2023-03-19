//buildscript {
//    repositories {
//        google()
//        mavenCentral()
//    }
//    dependencies {
//        classpath ("com.github.dcendents:android-maven-gradle-plugin:2.1")
//        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
//        classpath("org.jetbrains.kotlin:kotlin-reflect:1.5.31")
////        classpath(Plugins.androidMavenGradle)
////        classpath(Plugins.kotlinGradle)
////        classpath(Libs.kotlinReflect)
//    }
//
//}
//
//plugins {
//    id("com.android.library")
//    id("kotlin-android")
//    id("com.vanniktech.maven.publish")
//}
//
//android {
//    compileSdk = 31
//    defaultConfig {
//        minSdk = 16
//        targetSdk = 31
//        multiDexEnabled = true
//    }
//    compileOptions {
//        targetCompatibility = JavaVersion.VERSION_1_8
//        sourceCompatibility = JavaVersion.VERSION_1_8
//    }
//}
//
//dependencies {
//    implementation(Libs.kotlinReflect)
//    implementation(Libs.kotlinStdlib)
//    implementation(Libs.recyclerView)
//    api(Libs.espressoCore)
//    api(Libs.espressoContrib)
//    api(Libs.espressoWeb)
//    api(Libs.uiautomator)
//    api(Libs.accessibility)
//    api(Libs.hamcrestCore)
//    testImplementation(Libs.junit)
//}
//
//tasks.test {
//    useJUnitPlatform()
//}