buildscript {
    extra.apply {
        set("RELEASE_REPOSITORY_URL", "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
        set("SNAPSHOT_REPOSITORY_URL", "https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Plugins.kotlinGradle)
        classpath(Plugins.androidToolsBuildGradle)
        classpath(Plugins.androidMavenGradle)
        classpath(Plugins.publishPlugin)
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
}


allprojects {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}