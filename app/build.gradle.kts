plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.eventlistapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.eventlistapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.android.async.http)
    implementation(libs.material)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    implementation(libs.androidx.datastore.preferences)

    implementation (libs.glide)
    implementation(libs.filament.android)
    implementation(libs.androidx.datastore.preferences.core.jvm)
    implementation(libs.androidx.work.runtime.ktx)
    annotationProcessor (libs.compiler)

    implementation(libs.androidx.navigation.fragment.ktx.v274)
    implementation(libs.androidx.navigation.ui.ktx.v274)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation (libs.materialsearchbar)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    // To use Kotlin Symbol Processing (KSP)
    ksp(libs.androidx.room.compiler)
}