pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "h3-kmp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":library")
include(":sample")
