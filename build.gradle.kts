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

allprojects {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
