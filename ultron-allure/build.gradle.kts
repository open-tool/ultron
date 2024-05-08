plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.vanniktech.maven.publish")
}

group = project.findProperty("GROUP")!!
version =  project.findProperty("VERSION_NAME")!!


android {
    compileSdk = 34
    namespace = "com.atiurin.ultron.allure"
    defaultConfig {
        minSdk = 21
        targetSdk = 34
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
    implementation(project(":ultron"))
    implementation(Libs.kotlinStdlib)
    api(Libs.allureAndroid)
    api(Libs.allureCommon)
    api(Libs.allureModel)
    api(Libs.allureJunit4)
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