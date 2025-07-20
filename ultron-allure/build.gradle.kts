import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.androidLibrary)
    id("kotlin-android")
    id("org.jetbrains.dokka")
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = project.findProperty("GROUP")!!
version =  project.findProperty("VERSION_NAME")!!

kotlin {
    jvmToolchain(11)
}

android {
    namespace = "com.atiurin.ultron.allure"
    compileSdk = 35
    defaultConfig {
        minSdk = 21
    }

    sourceSets {
        named("main").configure {
            java.srcDir("src/main/java")
        }
        named("test").configure {
            java.srcDir("src/test/java")
        }
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_11
        sourceCompatibility = JavaVersion.VERSION_11
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}
dependencies {
    implementation(Libs.kotlinStdlib)
    api(project(":ultron-common"))
    api(Libs.allureAndroid)
    api(Libs.allureCommon)
    api(Libs.allureModel)
    api(Libs.allureJunit4)
    api(Libs.espressoCore)
}

val dokkaOutputDir = buildDir.resolve("dokka")
tasks.dokkaHtml { outputDirectory.set(file(dokkaOutputDir)) }
val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") { delete(dokkaOutputDir) }
val javadocJar = tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn(deleteDokkaOutputDir, tasks.dokkaHtml)
    from(dokkaOutputDir)
}


mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates(artifactId = "ultron-allure")

    pom {
        name = "Ultron Common"
        description = "Android & Compose Multiplatform UI testing framework"
        url = "https://github.com/open-tool/ultron"
        inceptionYear = "2021"

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        issueManagement {
            system = "GitHub Issues"
            url = "https://github.com/open-tool/ultron/issues"
        }

        developers {
            developer {
                id = "alex-tiurin"
                name = "Aleksei Tiurin"
                url = "https://github.com/open-tool"
            }
        }

        scm {
            url = "https://github.com/open-tool/ultron"
            connection = "scm:git@github.com:open-tool/ultron.git"
            developerConnection = "scm:git@github.com:open-tool/ultron.git"
        }
    }
}