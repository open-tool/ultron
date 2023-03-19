buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
//        classpath ("com.github.dcendents:android-maven-gradle-plugin:2.1")
//        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
//        classpath("org.jetbrains.kotlin:kotlin-reflect:1.5.31")
        classpath(Plugins.androidMavenGradle)
        classpath(Plugins.kotlinGradle)
//        classpath(Libs.kotlinReflect)
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
    compileSdk = 31
    defaultConfig {
        minSdk = 16
        targetSdk = 31
        multiDexEnabled = true
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
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
            jvmTarget = "1.8"
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
        classifier = "sources"
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
        classifier = "javadoc"
        from(javadoc.destinationDir)
    }

    artifacts {
        add("archives", sourcesJar)
        add("archives", javadocJar)
    }
}