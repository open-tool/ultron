import org.jetbrains.compose.internal.utils.getLocalProperty

buildscript {
    extra.apply {
        set("RELEASE_REPOSITORY_URL", "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
        set("SNAPSHOT_REPOSITORY_URL", "https://s01.oss.sonatype.org/content/repositories/snapshots/")
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
    id("io.github.gradle-nexus.publish-plugin").version("2.0.0-rc-1")
}

nexusPublishing {
    repositories {
        sonatype {
            username
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(getLocalProperty("ossrhToken") ?: System.getenv("OSSRH_TOKEN"))
            password.set(getLocalProperty("ossrhTokenPassword") ?: System.getenv("OSSRH_PASSWORD"))
            stagingProfileId.set(getLocalProperty("sonatype.stagingProfileId") ?: System.getenv("OSSRH_STAGING_PROFILE_ID"))
        }
    }
}


allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
    }
}