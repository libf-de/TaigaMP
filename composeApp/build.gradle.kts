import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ktorfit)
}

kotlin {
    jvm("desktop")

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.cio)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.koin.android)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(compose.components.resources)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.viewmodel)
            implementation(libs.koin.navigation)
            implementation(libs.ktor)
            implementation(libs.ktor.contentnegotiation)
            implementation(libs.ktor.contentnegotiation.json)
            implementation(libs.ktor.authorization)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.androidx.datastore)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.paging.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.ktorfit)
            implementation(libs.kmlogging)
            implementation(libs.materialdialogs.color)
            implementation(libs.materialdialogs.datetime)
            implementation(libs.coil.compose)
            implementation(libs.coil.ktor)
            implementation(libs.androidx.constraintlayout)
            implementation(libs.markdown.renderer)
            implementation(libs.markdown.renderer.coil)
        }

        iosMain.dependencies {
            implementation(libs.ktor.darwin)
        }

        val desktopMain by getting {
            dependencies {
                implementation(libs.ktor.cio)

                // Compose Multiplatform dependencies
                implementation(compose.desktop.common)
                implementation(compose.desktop.currentOs)
                implementation(compose.preview)

                // Kotlinx coroutines
                implementation(libs.kotlinx.coroutines.swing)

                val lwjglVersion = "3.3.1"
                listOf("lwjgl", "lwjgl-tinyfd").forEach { lwjglDep ->
                    implementation("org.lwjgl:${lwjglDep}:${lwjglVersion}")
                    listOf(
                        "natives-windows",
                        "natives-windows-x86",
                        "natives-windows-arm64",
                        "natives-macos",
                        "natives-macos-arm64",
                        "natives-linux",
                        "natives-linux-arm64",
                        "natives-linux-arm32"
                    ).forEach { native ->
                        runtimeOnly("org.lwjgl:${lwjglDep}:${lwjglVersion}:${native}")
                    }
                }
            }
        }
    }
}

android {
    namespace = "de.libf.taigamp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "de.libf.taigamp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "TaigaMP"
            packageVersion = "1.0.0"
        }
    }
}