buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Plugins.androidMavenGradle)
        classpath(Plugins.kotlinGradle)
    }

}

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.vanniktech.maven.publish")
}

group = project.findProperty("GROUP")!!
version =  project.findProperty("VERSION_NAME")!!

android {
    namespace = "com.atiurin.ultron"
    compileSdk = 34
    defaultConfig {
        minSdk = 16
        targetSdk = 34
        multiDexEnabled = true
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
    sourceSets {
        named("main").configure {
            java.srcDir("src/main/java")
        }
        named("test").configure {
            java.srcDir("src/test/java")
        }
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}

dependencies {
    implementation(Libs.kotlinReflect)
    implementation(Libs.kotlinStdlib)
    implementation(Libs.recyclerView)
    api(Libs.espressoCore)
    api(Libs.espressoContrib)
    api(Libs.espressoWeb)
    api(Libs.uiautomator)
    api(Libs.accessibility)
    api(Libs.hamcrestCore)
    testImplementation(Libs.junit)
}

tasks {
    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(tasks)
    }

    val javadoc by creating(Javadoc::class) {
        options {
            this as StandardJavadocDocletOptions
            addStringOption("Xdoclint:none", "-quiet")
            addStringOption("Xmaxwarns", "1")
            addStringOption("charSet", "UTF-8")
        }
    }

    val javadocJar by creating(Jar::class){
        dependsOn(javadoc)
        from(javadoc.destinationDir)
    }

    artifacts {
        add("archives", sourcesJar)
        add("archives", javadocJar)
    }
}