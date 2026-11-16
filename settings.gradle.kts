pluginManagement {
    repositories {
        google()
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        gradlePluginPortal()
        mavenCentral()
    }
}


rootProject.name = "ultron"
include(":sample-app")
include(":ultron-android")
include(":ultron-compose")
include(":ultron-allure")
include(":ultron-common")
include(":composeApp")
