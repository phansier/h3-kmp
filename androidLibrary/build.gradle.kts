plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
}
kotlin {
    jvmToolchain(21)
}

group = "io.github.phansier.h3"
version = "0.0.6"

android {
    namespace = "com.beriukhov.h3.ndk"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        externalNativeBuild {
            cmake {
                arguments += listOf("-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON")
            }
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

    if (project.hasProperty("signing.keyId")) signAllPublications()

    coordinates(group.toString(), "androidLibrary", version.toString())

    pom {
        name = "H3 KMP"
        description = "KMP (iOS+Android) binding for H3 library"
        inceptionYear = "2025"
        url = "https://github.com/phansier/h3-kmp/"
        licenses {
            license {
                name = "MIT"
                url = "https://opensource.org/licenses/MIT"
            }
        }
        developers {
            developer {
                id = "a76f5e93-8114-4ba3-825a-f8156ac753dc"
                name = "Andrei Beriukhov"
                url = "beriukhov@gmail.com"
            }
        }
        scm {
            url = "https://github.com/phansier/h3-kmp/"
        }
    }
}