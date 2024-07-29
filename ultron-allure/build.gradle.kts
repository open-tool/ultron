plugins {
    id("com.android.library")
    id("kotlin-android")
    id("org.jetbrains.dokka")
    id("maven-publish")
    id("signing")
}

group = project.findProperty("GROUP")!!
version =  project.findProperty("VERSION_NAME")!!

kotlin {
    jvmToolchain(11)
}

android {
    namespace = "com.atiurin.ultron.allure"
    compileSdk = 34
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

publishing {
    publications {
        create<MavenPublication>("release") {
            artifact(javadocJar.get())
            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set("Ultron Allure")
                description.set("Android & Compose Multiplatform UI testing framework")
                url.set("https://github.com/open-tool/ultron")
                inceptionYear.set("2021")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                issueManagement {
                    system.set("GitHub Issues")
                    url.set("https://github.com/open-tool/ultron/issues")
                }

                developers {
                    developer {
                        id.set("alex-tiurin")
                        name.set("Aleksei Tiurin")
                        url.set("https://github.com/open-tool")
                    }
                }

                scm {
                    connection.set("scm:git@github.com:open-tool/ultron.git")
                    developerConnection.set("scm:git@github.com:open-tool/ultron.git")
                    url.set("https://github.com/open-tool/ultron")
                }
            }
        }
    }
}

tasks.withType<PublishToMavenRepository>().configureEach {
    dependsOn(tasks.withType<Sign>())
    dependsOn(javadocJar)
    mustRunAfter(tasks.withType<Sign>())
}

tasks.named("generateMetadataFileForReleasePublication") {
    dependsOn(javadocJar)
}

signing {
    println("Signing lib...")
    useGpgCmd()
    sign(publishing.publications)
}
