plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrainsKotlinSerialization)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.inttelgo.tecnicos"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.inttelgo.tecnicos"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "2.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    //Image Picker
    implementation(libs.coil.compose)
    implementation (libs.androidx.activity.ktx)
    //Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    //DataStore
    implementation(libs.androidx.datastore.preferences)

    //Camera
    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view)
    implementation (libs.androidx.camera.extensions)
    implementation(libs.mlkit.barcode.scanning)
    implementation (libs.androidx.runtime)

    //DataBase
    implementation (libs.mysql.connector.java)

    //Retofit & OkHTTP
    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    //Maps SDK
    implementation(libs.play.services.maps)
    implementation (libs.play.services.location)

    // Firebase Cloud Messaging
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    //Corrutine

    implementation(libs.androidx.lifecycle.viewmodel)


    //Reproductor
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation (libs.androidx.media3.common)
    
    //Transcoder
    implementation(libs.transcoder)

    //Signature
    implementation(libs.signature.pad)

    //Animations
    implementation (libs.lottie.compose)

    //OkHTTP
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    //gson
    implementation(libs.gson)

    //noinspection NewerVersionAvailable
    implementation (libs.hilt.android)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.biometric)
}