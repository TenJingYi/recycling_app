plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    //alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.recyclingapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.recyclingapp"
        minSdk = 24
        targetSdk = 36
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
    buildFeatures{

        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // CameraX core library using the camera2 implementation
    val camerax_version = "1.3.0"
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")

    // CameraX Lifecycle library
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")

    // CameraX View class (This fixes the PreviewView error!)
    implementation("androidx.camera:camera-view:${camerax_version}")

    // Optional: CameraX Extensions (for portrait/hdr etc)
    implementation("androidx.camera:camera-extensions:${camerax_version}")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.cardview:cardview:1.0.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Check for this specific line:
    implementation("com.google.ai.client.generativeai:generativeai:0.7.0")

    // Also recommended for Java users:
    implementation("com.google.guava:guava:31.1-android")

    implementation("androidx.camera:camera-view:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")

    implementation("com.google.android.gms:play-services-maps:18.2.0")

    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation("com.google.android.libraries.places:places:3.2.0")

    implementation("com.google.firebase:firebase-auth:23.1.0")
}