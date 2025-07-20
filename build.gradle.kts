import org.jetbrains.compose.internal.utils.getLocalProperty

buildscript {
    extra.apply {
        set("RELEASE_REPOSITORY_URL", "https://central.sonatype.com/api/v1/publisher")
        set("SNAPSHOT_REPOSITORY_URL", "https://central.sonatype.com/api/v1/publisher")
    }

    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
    dependencies {
        classpath(Plugins.kotlinGradle)
        classpath(Plugins.androidToolsBuildGradle)
        classpath(Plugins.androidMavenGradle)
        classpath(Plugins.dokka)
    }
}

plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
    }
}