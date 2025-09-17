@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    androidLibrary {
        namespace = "com.xah.transition"
        compileSdk = Integer.parseInt(libs.versions.maxAndroidVersion.get())
        minSdk = Integer.parseInt(libs.versions.minAndroidVersion.get())

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }


    val xcfName = "transitionKit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    sourceSets {
        commonMain {
            dependencies {
//                implementation(libs.androidx.navigationevent.navigationevent)
//                implementation(compose.components.resources)
                implementation(libs.navigation.compose)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
//                implementation(libs.navigation.compose)
                implementation(libs.androidx.activity.compose)
            }
        }

        wasmJsMain {
            dependencies {
//                implementation(libs.navigation.compose.new)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.testExt.junit)
            }
        }

        iosMain {
            dependencies {
//                implementation(libs.navigation.compose.new)
            }
        }
        jvmMain {
            dependencies {
//                implementation(libs.navigation.compose.new)
            }
        }
    }

}