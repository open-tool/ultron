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
        minSdk = 21
        targetSdk = 31
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
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
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