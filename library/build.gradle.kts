import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "io.github.phansier.h3"
version = "0.0.1"

kotlin {
    jvmToolchain(21)

    android {
        namespace = "com.beriukhov.h3"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
//        defaultConfig {
//            minSdk = libs.versions.android.minSdk.get().toInt()
//            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//            externalNativeBuild {
//                cmake {
//                    arguments += listOf("-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON")
//                }
//            }
        }
//
//        externalNativeBuild {
//            cmake {
//                path = file("src/androidMain/cpp/CMakeLists.txt")
//                version = "3.22.1"
//            }
//        }

//        publishLibraryVariants("release")
//    }

    fun KotlinNativeTarget.h3CInterop() {
        compilations["main"].cinterops {
            val h3 by creating {
                defFile(project.file("../cinterop/h3/h3.def"))
                includeDirs(project.file("../cinterop/h3"))
            }
        }
    }
    iosX64() { h3CInterop() }
    iosArm64() { h3CInterop() }
    iosSimulatorArm64() { h3CInterop() }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.junit.ktx)
            implementation(libs.androidx.test.runner)
        }
        iosMain.dependencies {
        }
    }
    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }
    //https://kotlinlang.org/docs/native-objc-interop.html#export-of-kdoc-comments-to-generated-objective-c-headers
    targets.withType<KotlinNativeTarget> {
        compilations["main"].compileTaskProvider.configure {
            compilerOptions {
                freeCompilerArgs.add("-Xexport-kdoc")
            }
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

    if (project.hasProperty("signing.keyId")) signAllPublications()

    coordinates(group.toString(), "library", version.toString())

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
