plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "com.hfut.schedule.network"
    compileSdk = Integer.parseInt(libs.versions.maxAndroidVersion.get())

    defaultConfig {
        minSdk = Integer.parseInt(libs.versions.minAndroidVersion.get())

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Jsoup
    implementation(libs.jsoup)
    // OkHttp
    implementation(libs.okhttp)
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.scalars)
    implementation(libs.gson)
    // 用于和风天气密钥生成的JWT
    implementation(libs.eddsa)
    implementation(project(":shared"))
}