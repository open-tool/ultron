plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.vanniktech.maven.publish")
}

group = project.findProperty("GROUP")!!
version =  project.findProperty("VERSION_NAME")!!

android {
    namespace = "com.atiurin.ultron.android"
    compileSdk = 34

    defaultConfig {
        minSdk = 16
        multiDexEnabled = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    api(project(":ultron-common"))
    implementation(Libs.kotlinReflect)
    implementation(Libs.kotlinStdlib)
    implementation(Libs.recyclerView)
    api(Libs.espressoCore)
    api(Libs.espressoContrib)
    api(Libs.espressoWeb)
    api(Libs.accessibility)
    api(Libs.hamcrestCore)
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