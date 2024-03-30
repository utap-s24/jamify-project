plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
//    kotlin("kapt") version "1.9.23"
    id("kotlin-kapt")


}

android {
    namespace = "com.example.jamify"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.jamify"
        minSdk = 34
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    // TODO: Add the dependencies for Firebase products you want to use
    implementation ("com.google.firebase:firebase-analytics")
    // Declare the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation ("com.google.firebase:firebase-analytics-ktx")
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.firebase:firebase-firestore-ktx")
    implementation ("com.google.firebase:firebase-storage-ktx")
    implementation ("com.firebaseui:firebase-ui-auth:8.0.2")
    implementation ("com.firebaseui:firebase-ui-storage:8.0.2")

    // Glide
//    kapt("groupId:artifactId:version")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    kapt ("com.github.bumptech.glide:compiler:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
}