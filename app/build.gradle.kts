plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    kotlin("kapt") // Apply the kapt plugin correctly
}

android {
    namespace = "com.example.disasteralert"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.disasteralert"
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

    packagingOptions {
        resources {
            excludes.add("META-INF/versions/9/OSGI-INF/MANIFEST.MF")
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
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.identity.jvm)
    implementation(libs.androidx.material3.android)
    implementation(libs.firebase.storage)
    implementation(libs.play.services.maps)

    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.androidx.recyclerview)
    kapt("com.github.bumptech.glide:compiler:4.15.1") // For Kotlin
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation ("com.google.android.exoplayer:exoplayer:2.16.1")



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
