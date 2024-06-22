import org.jetbrains.compose.internal.utils.getLocalProperty

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
    id("io.github.gradle-nexus.publish-plugin").version("2.0.0-rc-1")
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(getLocalProperty("sonatype.username") ?: System.getenv("OSSRH_USERNAME"))
            password.set(getLocalProperty("sonatype.password") ?: System.getenv("OSSRH_PASSWORD"))
        }
    }
}
//nexusPublishing {
//    repositories {
//        sonatype {
//            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
//            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
//            username.set(gradleLocalProperties(rootDir).getProperty("sonatype.username") ?: System.getenv("OSSRH_USERNAME"))
//            password.set(gradleLocalProperties(rootDir).getProperty("sonatype.password") ?: System.getenv("OSSRH_PASSWORD"))
//            stagingProfileId.set(gradleLocalProperties(rootDir).getProperty("sonatype.stagingProfileId") ?: System.getenv("OSSRH_STAGING_PROFILE_ID"))
//        }
//    }
//}

allprojects {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}