plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.medicalreport"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.medicalreport"
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
   /* kotlinOptions {
        jvmTarget = "17"
    }*/
    dataBinding {
        enable = true
    }

    buildFeatures {
        buildConfig = true
    }

    /*compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }*/

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Motion Toast Display
    implementation("com.github.Spikeysanju:MotionToast:1.4")

    // For dimensions
    implementation("com.intuit.ssp:ssp-android:1.1.1")
    implementation("com.intuit.sdp:sdp-android:1.1.0")

    //Image caching
    implementation("com.facebook.fresco:fresco:2.6.0")

    //Circular Image View
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //navigation jetpack
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.7.1")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")

    //okhttp
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // Koin
    implementation("io.insert-koin:koin-core:3.1.2")
    implementation("io.insert-koin:koin-android:3.1.2")
    implementation("io.insert-koin:koin-android-compat:3.1.2")
    implementation("io.insert-koin:koin-androidx-workmanager:3.1.2")

    constraints {
        implementation("androidx.work:work-runtime:2.7.0")
        implementation("androidx.work:work-runtime-ktx:2.7.0")
    }

    //lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.7.0")

    //Image Loader
    implementation("com.nostra13.universalimageloader:universal-image-loader:1.9.5")

    //image picker
    implementation("com.github.dhaval2404:imagepicker:2.1")

    //Pdf Editor
    implementation("io.github.afreakyelf:Pdf-Viewer:2.1.1")
    implementation("com.itextpdf:itextpdf:5.5.13.3")

    //Camera Preview
    implementation("androidx.camera:camera-camera2:1.0.0")
    implementation("androidx.camera:camera-lifecycle:1.0.0")
    implementation("androidx.camera:camera-view:1.0.0-alpha27")

    //Signature Pad
    implementation("com.github.gcacace:signature-pad:1.3.1")

    //Usb Camera library
    implementation(project(":libusbcamera"))
    implementation(project(":my-local-sdk"))

}