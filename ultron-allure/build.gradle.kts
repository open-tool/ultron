plugins {
    id("com.android.library")
    id("kotlin-android")
    id("org.jetbrains.dokka")
    id("maven-publish")
    id("signing")
}

group = project.findProperty("GROUP")!!
version =  project.findProperty("VERSION_NAME")!!


android {
    compileSdk = 34
    namespace = "com.atiurin.ultron.allure"
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
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
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
val javadocJar = tasks.create<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn(deleteDokkaOutputDir, tasks.dokkaHtml)
    from(dokkaOutputDir)
}

// Создание другого arтефакта, например sourcesJar, если он нужен
val sourcesJar = tasks.create<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(javadocJar)
            artifact(sourcesJar) // добавление sourcesJar в публикацию

            pom {
                name.set("ultron-allure")
                description.set("Android & Compose Multiplatform UI testing framework")
                url.set("https://github.com/open-tool/ultron")
                packaging = "aar"
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

signing {
    if (project.hasProperty("signing.gnupg.keyName")) {
        println("Signing lib...")
        useGpgCmd()
        sign(publishing.publications)
    }
}

//afterEvaluate {
//    tasks.findByName("generateMetadataFileForMavenPublication")?.let { generateMetadataTask ->
//        tasks.findByName("androidSourcesJar")?.let { androidSourcesJarTask ->
//            generateMetadataTask.dependsOn(androidSourcesJarTask)
//        }
//    }
//}
//afterEvaluate {
//    configure<PublishingExtension> {
//        publications.all {
//            val mavenPublication = this as? MavenPublication
//            mavenPublication?.artifactId =
//                "${project.name}${"-$name".takeUnless { "kotlinMultiplatform" in name }.orEmpty()}"
//        }
//    }
//}