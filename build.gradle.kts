import com.android.build.api.dsl.CommonExtension
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import kotlin.apply
import kotlin.jvm.java

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply  false
    alias(libs.plugins.androidKmpLibrary) apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
}

subprojects {
    afterEvaluate {
        tasks.withType(KotlinCompilationTask::class).configureEach {
            compilerOptions {
//                optIn.add("kotlin.RequiresOptIn")
            }
        }
//        extensions.findByType(CommonExtension::class.java)?.apply {
//            compileOptions.sourceCompatibility = JavaVersion.VERSION_21
//            compileOptions.targetCompatibility = JavaVersion.VERSION_21
//        }
    }
}